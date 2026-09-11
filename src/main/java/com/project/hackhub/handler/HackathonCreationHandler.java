/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.dto.HackathonCreationResponse;
import com.project.hackhub.dto.HackathonDTO;
import com.project.hackhub.dto.TaskDTO;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.Task;
import com.project.hackhub.model.hackathon.builder.Director;
import com.project.hackhub.model.hackathon.builder.HackathonBuilder;
import com.project.hackhub.model.hackathon.builder.HackathonBuilderMemento;
import com.project.hackhub.model.hackathon.builder.HackathonSnapshot;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.repository.*;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static com.project.hackhub.model.user.state.Permission.CAN_ADD_TASK;

/**
 * Gestisce la creazione complessa degli hackathon utilizzando i pattern Builder e Memento,
 * oltre all'inserimento dei task e alla verifica della disponibilità delle prenotazioni.
 */
@Component
@AllArgsConstructor
public class HackathonCreationHandler {

    private final TaskRepository taskRepository;
    private final HackathonRepository hackathonRepo;
    private final HackathonSnapshotRepository snapshotRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final UserStateService userStateService;

    /**
     * Inserisce un nuovo task all'interno di un hackathon durante la fase di iscrizione.
     *
     * @param coordinator l'identificatore del coordinatore che esegue l'operazione
     * @param taskDTO il DTO con le informazioni del task
     * @param hackathonId l'identificatore dell'hackathon
     */
    @Transactional
    public void insertTask(UUID coordinator, TaskDTO taskDTO, UUID hackathonId) {

        User c = userRepository.findById(coordinator)
                .orElseThrow(() -> new IllegalArgumentException("Coordinator can't null"));

        Hackathon h = hackathonRepo.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon can't null"));

        if(!c.hasPermission(CAN_ADD_TASK, h) || !h.getState().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE))
            throw new UnsupportedOperationException("User doesn't have permission to add task or hackathon is not in state IN_ISCRIZIONE");

        Task t = new Task(taskDTO.title(), taskDTO.description(), taskDTO.template());
        this.taskRepository.save(t);
        h.addTask(t);
        this.hackathonRepo.save(h);
    }

    /**
     * Verifica se una prenotazione (location e intervallo temporale) risulta disponibile.
     *
     * @param reservation la prenotazione da verificare
     * @return {@code true} se è disponibile, {@code false} altrimenti
     * @author Sonia Bevilacqua
     */
    public boolean isReservationAvailable(Reservation reservation) {

        if (reservation == null)
            return false;

        if (reservation.getLocation() == null || reservation.getTimeInterval() == null)
            return false;

        return !reservationRepository.existsByLocationAndTimeInterval(
                reservation.getLocation(),
                reservation.getTimeInterval()
        );
    }

    /**
     * Gestisce la creazione o la sospensione (tramite snapshot/memento) di un hackathon.
     *
     * @param dto il DTO con i dati dell'hackathon
     * @param coordinatorId l'identificatore del coordinatore organizzatore
     * @return un oggetto {@code HackathonCreationResponse} con l'esito del processo
     */
    @Transactional
    public HackathonCreationResponse createHackathon(
            HackathonDTO dto,
            UUID coordinatorId
    ) {

        if (dto == null) {
            throw new IllegalArgumentException("HackathonDTO non può essere vuoto");
        }

        LocalDate today = LocalDate.now();

        if (dto.expiredSubscriptionsDate() != null && dto.expiredSubscriptionsDate().isBefore(today)) {
            throw new IllegalArgumentException("Errore di Business: Le iscrizioni non possono chiudere nel passato.");
        }

        if (dto.reservation() != null && dto.reservation().getTimeInterval() != null) {
            LocalDate startDate = dto.reservation().getTimeInterval().startDate();

            if (startDate.isBefore(today)) {
                throw new IllegalArgumentException("Errore di Business: L'Hackathon non può iniziare nel passato.");
            }

            if (dto.expiredSubscriptionsDate() != null && startDate.isBefore(dto.expiredSubscriptionsDate())) {
                throw new IllegalArgumentException("Errore di Business: L'inizio dell'evento deve essere successivo alla chiusura delle iscrizioni.");
            }
        }

        User coordinator = userRepository.findById(coordinatorId)
                .orElseThrow(() -> new IllegalArgumentException("Coordinator not found"));

        if(!coordinator.isOrganizer()) throw new IllegalArgumentException("this user cannot organize hackathons!");

        HackathonBuilder builder = new HackathonBuilder();
        Optional<HackathonSnapshot> existingSnapshot = snapshotRepository.findByAuthor(coordinator);
        builder.reset();

        existingSnapshot.ifPresent(snapshot -> {
            builder.restoreFromSnapshot(snapshot, userRepository);
        });

        Reservation p = dto.reservation();
        if (p == null) {
            p = existingSnapshot.map(HackathonSnapshot::getReservation).orElse(null);
        }

        Director director = new Director(builder, userRepository, this);
        director.populateBuilder(dto, p);

        HackathonSnapshot debug = builder.saveMemento(coordinator).getSnapshot();
        System.out.println("\n=== COSA VEDE IL COSTRUTTORE? ===");
        System.out.println("Name: " + debug.getName());
        System.out.println("RuleBook: " + debug.getRuleBook());
        System.out.println("Date Iscr: " + debug.getExpiredSubscriptionsDate());
        System.out.println("MaxTeamDim: " + debug.getMaxTeamDimension());
        System.out.println("Prize: " + debug.getMoneyPrice());
        System.out.println("Reservation: " + debug.getReservation());
        System.out.println("Judge: " + debug.getJudge());
        System.out.println("Mentors (Size): " + (debug.getMentorsList() != null ? debug.getMentorsList().size() : "null"));
        System.out.println("=================================\n");

        if (builder.isComplete()) {
            return completeHackathonCreation(builder, coordinator, existingSnapshot);
        } else {
            return suspendHackathonCreation(builder, coordinator, existingSnapshot);
        }
    }

    /**
     * Completa il processo di creazione salvando l'hackathon sul database e aggiornando gli stati dello staff.
     */
    private HackathonCreationResponse completeHackathonCreation(
            HackathonBuilder builder,
            User coordinator,
            Optional<HackathonSnapshot> existingSnapshot
    ) {
        builder.setCoordinator(coordinator);
        builder.setState();

        Hackathon hackathon = builder.getProduct();
        reservationRepository.save(hackathon.getReservation());
        hackathonRepo.save(hackathon);

        updateStaffState(hackathon);

        existingSnapshot.ifPresent(snapshotRepository::delete);
        coordinator.setOrganizer(false);
        userRepository.save(coordinator);

        return new HackathonCreationResponse(true, "Hackathon created successfully");
    }

    /**
     * Sospende la creazione dell'hackathon salvando uno snapshot incompleto per poterlo riprendere in seguito.
     */
    private HackathonCreationResponse suspendHackathonCreation(
            HackathonBuilder builder,
            User coordinator,
            Optional<HackathonSnapshot> existingSnapshot
    ) {

        HackathonSnapshot snapshot = existingSnapshot.orElse(new HackathonSnapshot());
        snapshot.setAuthor(coordinator);

        HackathonBuilderMemento memento = builder.saveMemento(coordinator);
        HackathonSnapshot currentState = memento.getSnapshot();

        if (existingSnapshot.isPresent()) {
            snapshot.setName(currentState.getName() != null ? currentState.getName() : snapshot.getName());
            snapshot.setRuleBook(currentState.getRuleBook() != null ? currentState.getRuleBook() : snapshot.getRuleBook());
            snapshot.setExpiredSubscriptionsDate(currentState.getExpiredSubscriptionsDate() != null ?
                    currentState.getExpiredSubscriptionsDate() : snapshot.getExpiredSubscriptionsDate());
            snapshot.setMaxTeamDimension(currentState.getMaxTeamDimension() != null ?
                    currentState.getMaxTeamDimension() : snapshot.getMaxTeamDimension());
            snapshot.setMoneyPrice(currentState.getMoneyPrice() != null ? currentState.getMoneyPrice() : snapshot.getMoneyPrice());
            snapshot.setReservation(currentState.getReservation() != null ? currentState.getReservation() : snapshot.getReservation());
            snapshot.setJudge(currentState.getJudge() != null ? currentState.getJudge() : snapshot.getJudge());
            snapshot.setMentorsList(currentState.getMentorsList() != null ? currentState.getMentorsList() : snapshot.getMentorsList());
        } else {
            snapshot.setName(currentState.getName());
            snapshot.setRuleBook(currentState.getRuleBook());
            snapshot.setExpiredSubscriptionsDate(currentState.getExpiredSubscriptionsDate());
            snapshot.setMaxTeamDimension(currentState.getMaxTeamDimension());
            snapshot.setMoneyPrice(currentState.getMoneyPrice());
            snapshot.setReservation(currentState.getReservation());
            snapshot.setJudge(currentState.getJudge());
            snapshot.setMentorsList(currentState.getMentorsList());
        }

        snapshotRepository.save(snapshot);

        return new HackathonCreationResponse(false, "Hackathon creation suspended, missing information");
    }

    /**
     * Aggiorna gli stati utente di tutti i membri dello staff assegnati all'hackathon.
     */
    private void updateStaffState(Hackathon hackathon) {
        userStateService.changeUserState(hackathon.getJudge(), true, hackathon, UserStateType.JUDGE);
        userStateService.changeUserState(hackathon.getCoordinator(), true, hackathon, UserStateType.COORDINATOR);

        if (hackathon.getMentorsList() != null) {
            for (User mentor : hackathon.getMentorsList()) {
                userStateService.changeUserState(mentor, true, hackathon, UserStateType.MENTOR);
            }
        }
    }
}