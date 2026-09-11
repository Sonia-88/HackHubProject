/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.dto.HackathonDTO;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestisce le operazioni di modifica e cancellazione degli hackathon da parte degli organizzatori.
 */
@Component
@RequiredArgsConstructor
public class HackathonHandler {

    private final HackathonRepository hackathonRepo;
    private final UserRepository utenteRepository;
    private final UserStateService userStateService;

    /**
     * Elimina un hackathon verificando i permessi dell'organizzatore e rilasciando le prenotazioni associate.
     *
     * @param deleterId l'identificatore dell'utente che richiede la cancellazione
     * @param hackathonId l'identificatore dell'hackathon da eliminare
     */
    @Transactional
    public void deleteHackathon(UUID deleterId, UUID hackathonId) {

        User deleter = utenteRepository.findById(deleterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon hackathon = hackathonRepo.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        if (!deleter.hasPermission(Permission.CAN_DELETE_HACKATHON, hackathon)) {
            throw new UnsupportedOperationException("Insufficient permissions to delete hackathon");
        }
        if(!hackathon.getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
            throw new UnsupportedOperationException("Hackathon cannot be deleted in this state");
        }

        List<User> participants = new ArrayList<>();

        if (hackathon.getCoordinator() != null) participants.add(hackathon.getCoordinator());
        if (hackathon.getJudge() != null) participants.add(hackathon.getJudge());
        if (hackathon.getMentorsList() != null) participants.addAll(hackathon.getMentorsList());

        if (hackathon.getTeamsList() != null) {
            for (Team team : hackathon.getTeamsList()) {
                if (team.getTeamMembersList() != null) {
                    participants.addAll(team.getTeamMembersList());
                }
            }
        }

        EventManager.getInstance().notify(EventType.HACKATHON_DELETION, participants, "the hackathon " + hackathonId +  " has been deleted!",  hackathon);

        Reservation reservationTarget = hackathon.getReservation();

        if (reservationTarget != null) {
            List<User> allUsers = utenteRepository.findAll();
            for (User u : allUsers) {
                u.removeReservation(reservationTarget);
            }
            utenteRepository.saveAll(allUsers);
            utenteRepository.flush();
        }
        hackathonRepo.delete(hackathon);
    }

    /**
     * Modifica i dati di base di un hackathon tramite DTO durante la fase di iscrizione.
     *
     * @param editorId l'identificatore dell'editor
     * @param hackathonId l'identificatore dell'hackathon
     * @param dto il DTO con i nuovi campi
     */
    @Transactional
    public void updateHackathon(UUID editorId, UUID hackathonId, HackathonDTO dto) {
        User editor = utenteRepository.findById(editorId)
                .orElseThrow(() -> new IllegalArgumentException("Editor not found"));
        Hackathon hackathon = hackathonRepo.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        if (!editor.hasPermission(Permission.CAN_MODIFY_HACKATHON, hackathon)) {
            throw new UnsupportedOperationException("Insufficient permissions");
        }

        if(!hackathon.getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
            throw new UnsupportedOperationException("Operation cannot be performed in this state");
        }

        if(dto.judge() != null || !(dto.mentorsList() == null))
            throw new IllegalArgumentException("Staff roles cannot be modified through this endpoint;" +
                    "Please use the appropriate staff management endpoints.");
        if(dto.reservation() != null)
            throw new UnsupportedOperationException("The reservation cannot be changed; please remove and try again");
        if(dto.name() != null) hackathon.setName(dto.name());
        if(dto.ruleBook() != null) hackathon.setRuleBook(dto.ruleBook());
        if(dto.expiredSubscriptionsDate() != null)hackathon.setExpiredSubscriptionsDate(dto.expiredSubscriptionsDate());
        if(dto.maxTeamDimension() != null)hackathon.setMaxTeamDimension(dto.maxTeamDimension());
        if(dto.moneyPrize()!= null) hackathon.setMoneyPrize(dto.moneyPrize());

        List<User> usersToUpdate = new ArrayList<>();
        for(Team t : hackathon.getTeamsList()) {
            usersToUpdate.addAll(t.getTeamMembersList());
        }
        usersToUpdate.add(hackathon.getJudge());
        usersToUpdate.addAll(hackathon.getMentorsList());
        EventManager.getInstance().notify(EventType.MODIFIED_HACKATHON, usersToUpdate, "some fields of the hackathon" + hackathonId + "have been modified", hackathon);

        hackathonRepo.save(hackathon);
    }
}