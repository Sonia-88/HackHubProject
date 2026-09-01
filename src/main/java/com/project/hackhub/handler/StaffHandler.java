/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.exceptions.UserNotAvailableException;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Gestisce l'assegnazione, la rimozione e la modifica dei ruoli dello staff (mentor, giudici, coordinatori)
 * all'interno di un hackathon.
 */
@Component
@RequiredArgsConstructor
public class StaffHandler {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final UserStateService userStateService;

    /**
     * Aggiunge un utente come mentor all'hackathon.
     *
     * @param organizerId l'identificatore dell'organizzatore
     * @param hackathonId l'identificatore dell'hackathon
     * @param mentorId l'identificatore dell'utente da aggiungere come mentor
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void addMentor(UUID organizerId, UUID hackathonId, UUID mentorId) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentore non trovato"));

        if (!organizer.hasPermission(Permission.CAN_MANAGE_STAFF, hackathon)) {
            throw new UnsupportedOperationException("Permessi insufficienti per gestire lo staff");
        }
        if(!hackathon.getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE))
            throw new IllegalStateException("Hackathon deve essere nella fase di sottoscrizione per aggiungere mentori");

        if (!mentor.isAvailable(hackathon.getReservation())) {
            throw new IllegalStateException("Mentore non e' disponibile per questo hackathon");
        }

        hackathon.addMentor(mentor);
        userStateService.changeUserState(mentor, true, hackathon, UserStateType.MENTOR);
        hackathonRepository.save(hackathon);
        userRepository.save(mentor);
    }

    /**
     * Rimuove un mentor dall'hackathon ripristinando lo stato di default.
     *
     * @param organizerId l'identificatore dell'organizzatore
     * @param hackathonId l'identificatore dell'hackathon
     * @param mentorId l'identificatore del mentor da rimuovere
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void removeMentor(UUID organizerId, UUID hackathonId, UUID mentorId) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));
        User mentor = userRepository.findById(mentorId)
                .orElseThrow(() -> new IllegalArgumentException("Mentore non trovato"));

        if (!organizer.hasPermission(Permission.CAN_MANAGE_STAFF, hackathon)) {
            throw new UnsupportedOperationException("Permessi insufficienti per gestire lo staff");
        }

        if (!hackathon.getMentorsList().contains(mentor)) {
            throw new IllegalArgumentException("L'utente non e' un membro di questo hackathon");
        }
        if(hackathon.getMentorsList().size() == 1)
            throw new IllegalStateException("Non posso rimuovere l'unico mentore");

        hackathon.removeMentor(mentor);
        userStateService.changeUserState(mentor, false, hackathon, UserStateType.DEFAULT_STATE);
        hackathonRepository.save(hackathon);
        userRepository.save(mentor);
    }

    /**
     * Modifica il ruolo di un utente all'interno di un hackathon.
     *
     * @param organizerId l'identificatore dell'organizzatore
     * @param hackathonId l'identificatore dell'hackathon
     * @param targetUserId l'identificatore dell'utente di cui cambiare il ruolo
     * @param newRole il nuovo ruolo da assegnare
     */
    @Transactional
    public void changeStaffRole(UUID organizerId, UUID hackathonId, UUID targetUserId, String newRole) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utente richiesto non trovato"));

        if(!hackathon.getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
            throw new UnsupportedOperationException("L'operazione non puo' essere eseguita in questo stato");
        }

        if (!organizer.hasPermission(Permission.CAN_MANAGE_STAFF, hackathon)) {
            throw new UnsupportedOperationException("Permessi insufficienti per gestire lo staff");
        }

        UserStateType targetState = parseRole(newRole);

        if (organizer.getId().equals(targetUserId)) {
            throw new UnsupportedOperationException("L'organizzatore non può cambiare il proprio ruolo");
        }

        if (!targetUser.isAvailable(hackathon.getReservation())) {
            if (targetUser.equals(hackathon.getJudge()))
                throw new UserNotAvailableException("Ci deve essere almeno un giudice");
            else if(hackathon.getMentorsList().contains(targetUser) && hackathon.getMentorsList().size() == 1)
                throw new UserNotAvailableException("Ci deve essere almeno un mentore");
            else if (hackathon.getMentorsList().contains(targetUser));
            else
                throw new UserNotAvailableException("L'utente non appartiene all staff");
        }

        assignRoleToUser(targetUser, hackathon, targetState);
        hackathonRepository.save(hackathon);
    }

    /**
     * Converte una stringa di ruolo nel corrispondente {@code UserStateType}.
     */
    private UserStateType parseRole(String role) {
        return switch (role.toUpperCase()) {
            case "COORDINATOR" -> UserStateType.COORDINATOR;
            case "MENTOR" -> UserStateType.MENTOR;
            case "JUDGE" -> UserStateType.JUDGE;
            default -> throw new IllegalArgumentException("Ruolo non valido. Usa COORDINATOR, MENTOR, o JUDGE");
        };
    }

    /**
     * Assegna un ruolo specifico a un utente nell'ambito dell'hackathon.
     */
    private void assignRoleToUser(User user, Hackathon hackathon, UserStateType role) {
        switch (role) {
            case COORDINATOR -> {
                if (hackathon.getCoordinator() != null && !hackathon.getCoordinator().equals(user)) {
                    throw new IllegalStateException("L'hackathon non accetta piu' di un organizzatore");
                }
                hackathon.setCoordinator(user);
                userStateService.changeUserState(user, true, hackathon, UserStateType.COORDINATOR);
            }
            case MENTOR -> {
                if (user.equals(hackathon.getJudge())) {
                    userStateService.changeUserState(user, false, hackathon, UserStateType.JUDGE);
                    hackathon.setJudge(null);
                }

                if (!hackathon.getMentorsList().contains(user)) {
                    hackathon.addMentor(user);
                    userStateService.changeUserState(user, true, hackathon, UserStateType.MENTOR);
                }
            }
            case JUDGE -> {
                if(hackathon.getMentorsList().contains(user)) {
                    hackathon.removeMentor(user);
                    userStateService.changeUserState(user, false, hackathon, UserStateType.MENTOR);
                }

                User oldJudge = hackathon.getJudge();
                if (oldJudge != null && !oldJudge.equals(user)) {
                    userStateService.changeUserState(oldJudge, false, hackathon, UserStateType.JUDGE);
                    EventManager.getInstance().notify(EventType.CHANGE_STAFF_ROLE, List.of(oldJudge), "Sei stato sostituito nell'hackathon " + hackathon.getId(), hackathon);
                }

                hackathon.setJudge(user);
                userStateService.changeUserState(user, true, hackathon, UserStateType.JUDGE);
            }
            default -> throw new IllegalArgumentException("Ruolo non gestito: " + role);
        }
    }
}