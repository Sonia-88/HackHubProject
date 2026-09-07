/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;


import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gestisce l'abbandono volontario di un team da parte di un utente.
 */
@Component
@AllArgsConstructor
public class TeamPartecipationHandler {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserStateService userStateService;

    /**
     * Rimuove un utente da un team a seguito della sua richiesta di abbandono.
     *
     * @param user l'identificatore dell'utente che lascia il team
     * @param team l'identificatore del team
     * @author Cosmina Androne
     */
    @Transactional
    public void leaveTeam(UUID user, UUID team) {

        User user1 = userRepository.findById(user).orElseThrow(
                () -> new IllegalArgumentException("user cannot be null"));

        Team t = teamRepository.findById(team).orElseThrow(
                () -> new IllegalArgumentException("team to leave cannot be null"));

        if(t.getTeamMembersList().size() < 2)
            throw new UnsupportedOperationException("cannot leave team! " +
                    "Must delete hackathon team participation!");

        if(user1.equals(t.getTeamLeader()))
            throw new UnsupportedOperationException("the team leader cannot leave the team! " +
                    "Must choose a new team leader before leaving the team!");

        userStateService.changeUserState(user1, false, t.getHackathon(), UserStateType.DEFAULT_STATE);
        t.removeTeamMember(user1);
        teamRepository.save(t);
    }
}