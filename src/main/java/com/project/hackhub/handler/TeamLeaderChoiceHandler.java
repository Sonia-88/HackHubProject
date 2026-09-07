/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Gestisce il cambio del team leader all'interno di una squadra.
 */
@Component
@AllArgsConstructor
public class TeamLeaderChoiceHandler {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserStateService userStateService;

    /**
     * Modifica il leader di un team promuovendo un altro membro esistente.
     *
     * @param newLeader l'identificatore del nuovo leader
     * @param oldLeader l'identificatore del precedente leader
     * @param t l'identificatore del team
     * @author Cosmina Androne
     */
    @Transactional
    public void chooseNewTeamLeader(UUID newLeader, UUID oldLeader, UUID t){

        User oldLeaderU = userRepository.findById(oldLeader).orElseThrow
                (() ->  new IllegalArgumentException("oldLeader cannot be null"));

        User newLeaderU = userRepository.findById(newLeader).orElseThrow
                (() ->  new IllegalArgumentException("newLeader cannot be null"));

        Team team = teamRepository.findById(t).orElseThrow
                (() ->  new IllegalArgumentException("team cannot be null"));

        if(!team.getTeamMembersList().contains(oldLeaderU))
            throw new IllegalArgumentException("the new leader must be a member of the team");

        if(team.getTeamMembersList().size() == 1)
            throw new UnsupportedOperationException("Cannot choose new Leader with only one member");

        if(!oldLeaderU.hasPermission(Permission.CAN_MODIFY_LEADER, team.getHackathon()))
            throw new IllegalArgumentException("user does not have the permission to do this action");

        if(!team.getHackathon().getState().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE))
            throw new UnsupportedOperationException("Cannot choose new Leader");

        team.setTeamLeader(newLeaderU);
        teamRepository.save(team);
        userStateService.changeUserState(oldLeaderU, true, team.getHackathon(), UserStateType.TEAM_MEMBER);
        EventManager notifier = EventManager.getInstance();
        notifier.notify(EventType.NEW_LEADER, List.of(newLeaderU), "you have been chosen as new team leader for the team" + team.getId(), team);
    }
}