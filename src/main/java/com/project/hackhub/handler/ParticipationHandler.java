/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gestisce la disiscrizione di un intero team dall'hackathon.
 */
@Component
@AllArgsConstructor
public class ParticipationHandler {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;

    /**
     * Disiscrive un team da un Hackathon, rimuovendone la partecipazione ed eliminando il team.
     *
     * @param team l'identificatore del team da disiscrivere
     * @param user l'identificatore dell'utente che effettua l'azione
     * @author Cosmina Androne
     */
    @Transactional
    public void unsubscribeTeam(UUID team, UUID user){

        Team t = teamRepository.findById(team).orElseThrow(() ->
                new IllegalArgumentException("team cannot be null"));

        User user1 = userRepository.findById(user).orElseThrow(
                () -> new IllegalArgumentException("user cannot be null"));

        Hackathon h = t.getHackathon();

        if(!user1.hasPermission(Permission.CAN_UNSUBSCRIBE_TEAM, h)
                || !h.getState().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE))
            throw new UnsupportedOperationException("Cannot unsubscribe team");

        h.removeTeam(t);
        hackathonRepository.save(h);
        EventManager notifier = EventManager.getInstance();
        notifier.notify(EventType.UNSUBSCRIBE_TEAM, t.getTeamMembersList(), "the team leader has unsubscribed the team" + t.getId(), t);
        teamRepository.delete(t);
    }
}