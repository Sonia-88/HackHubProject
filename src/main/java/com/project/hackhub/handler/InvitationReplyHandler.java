/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.repository.InvitationRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gestisce la risposta agli inviti ricevuti (accettazione o rifiuto) da parte degli utenti.
 */
@Component
@AllArgsConstructor
public class InvitationReplyHandler {

    private final InvitationRepository invitationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserStateService userStateService;

    /**
     * Rimuove un invito dal sistema e dalla lista del team mittente.
     *
     * @param invitation l'identificatore dell'invito da rimuovere
     * @author Cosmina Androne
     */
    private void removeInvitation(UUID invitation) {

        Invitation i = invitationRepository.findById(invitation).orElseThrow(
                () -> new IllegalArgumentException("the invitation cannot be null"));

        Team t = i.getSender();
        t.removeInvitationFromList(i);
        teamRepository.save(t);
        invitationRepository.delete(i);
    }

    /**
     * Accetta un invito a far parte di un team, aggiornando lo stato dell'utente.
     *
     * @param user l'identificatore dell'utente che accetta
     * @param invitation l'identificatore dell'invito
     * @author Cosmina Androne
     */
    @Transactional
    public void acceptInvitation(UUID user, UUID invitation) {

        User u = userRepository.findById(user).orElseThrow(
                () -> new IllegalArgumentException("user cannot be null"));
        Invitation i = invitationRepository.findById(invitation).orElseThrow(
                () -> new IllegalArgumentException("the invitation cannot be null"));

        User addressee = i.getAddressee();
        Hackathon h = i.getSender().getHackathon();

        if(!u.hasPermission(Permission.CAN_ACCEPT_INVITATION, h)) {
            throw new IllegalArgumentException("user does not have permission "); }

        if(!h.getState().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)
                || !addressee.isAvailable(h.getReservation()))
            throw new UnsupportedOperationException("cannot perform operation");

        addressee.removeInvitation(i);
        userStateService.changeUserState(addressee, true, h, UserStateType.TEAM_MEMBER);
        i.getSender().addTeamMember(addressee);
        removeInvitation(invitation);
    }

    /**
     * Rifiuta un invito a far parte di un team.
     *
     * @param user l'identificatore dell'utente che rifiuta
     * @param invitation l'identificatore dell'invito
     * @author Cosmina Androne
     */
    @Transactional
    public void declineInvitation(UUID user, UUID invitation) {

        User u = userRepository.findById(user).orElseThrow(
                () -> new IllegalArgumentException("user cannot be null"));
        Invitation i = invitationRepository.findById(invitation).orElseThrow(
                () -> new IllegalArgumentException("the invitation cannot be null"));

        User addressee = i.getAddressee();
        Hackathon h = i.getSender().getHackathon();

        if (!u.hasPermission(Permission.CAN_DECLINE_INVITATION, h) || !h.getState().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE))
            throw new UnsupportedOperationException("cannot perform operation");

        addressee.removeInvitation(i);
        userRepository.save(addressee);
        removeInvitation(invitation);
    }
}