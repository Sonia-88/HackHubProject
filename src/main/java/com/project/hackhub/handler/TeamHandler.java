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
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.UserStateService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Gestisce la creazione, la modifica dei team e la gestione dei membri affiliati.
 */
@Component
@RequiredArgsConstructor
public class TeamHandler {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserStateService userStateService;

    /**
     * Crea un nuovo team associato a un hackathon. Il creatore diventa il Team Leader.
     *
     * @param creatorId l'identificatore del creatore
     * @param hackathonId l'identificatore dell'hackathon
     * @param teamName il nome del team
     */
    @Transactional
    public void createTeam(UUID creatorId, UUID hackathonId, String teamName) {

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("Creatore non trovato"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

        if (!creator.hasPermission(Permission.CAN_CREATE_TEAM, hackathon)) {
            throw new UnsupportedOperationException("L'utente non può creare un team in questo hackathon");
        }
        if(hackathon.getStateType() != HackathonStateType.SUBSCRIPTION_PHASE) {
            throw new UnsupportedOperationException("Teams può essere creato solamente ad iscrizioni aperte");
        }

        UserStateType statoAttuale = userStateService.getUserStateInHackathon(creator, hackathon);

        if (statoAttuale == UserStateType.TEAM_LEADER || statoAttuale == UserStateType.TEAM_MEMBER) {
            throw new IllegalStateException("Errore: Fai già parte di un team per questo Hackathon!");
        }

        createTeam(teamName, creator, hackathon);
    }

    /**
     * Modifica il nome di un team (consentito solo al team leader o all'organizzatore).
     *
     * @param editorId l'identificatore dell'utente richiedente
     * @param teamId l'identificatore del team
     * @param newName il nuovo nome da assegnare
     */
    @Transactional
    public void updateTeam(UUID editorId, UUID teamId, String newName) {
        User editor = userRepository.findById(editorId)
                .orElseThrow(() -> new IllegalArgumentException("Editor not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));

        if(!team.getHackathon().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
            throw new UnsupportedOperationException("Operation cannot be performed in this state");
        }

        boolean isLeader = team.getTeamLeader().getId().equals(editorId);
        boolean isOrganizer = editor.hasPermission(Permission.CAN_MANAGE_TEAMS, team.getHackathon());

        if (!isLeader && !isOrganizer) {
            throw new UnsupportedOperationException("Only team leader or organizer can modify the team");
        }

        for(Team t: team.getHackathon().getTeamsList())
            if(t.getName().equals(newName))
                throw new IllegalArgumentException("A team with the same name already exists in this hackathon.");

        team.setName(newName);
        teamRepository.save(team);
    }

    /**
     * Rimuove un membro dal team (consentito solo al team leader o all'organizzatore).
     *
     * @param requesterId l'identificatore del richiedente
     * @param teamId l'identificatore del team
     * @param memberId l'identificatore del membro da rimuovere
     */
    @Transactional
    public void removeMember(UUID requesterId, UUID teamId, UUID memberId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        if(!team.getHackathon().getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
            throw new UnsupportedOperationException("Operation cannot be performed in this state");
        }

        boolean isLeader = team.getTeamLeader().getId().equals(requesterId);
        boolean isOrganizer = requester.hasPermission(Permission.CAN_MANAGE_TEAMS, team.getHackathon());

        if (!isLeader && !isOrganizer) {
            throw new UnsupportedOperationException("Insufficient permissions");
        }
        removeTeamMember(member, team);
    }

    /**
     * Metodo di supporto privato per l'effettiva creazione del team.
     */
    private void createTeam(String name, User leader, Hackathon hackathon) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Team name cannot be null or blank.");
        if (leader == null)
            throw new IllegalArgumentException("Leader cannot be null.");
        if (hackathon == null)
            throw new IllegalArgumentException("Hackathon cannot be null.");

        for(Team t: hackathon.getTeamsList())
            if(t.getName().equals(name))
                throw new IllegalArgumentException("A team with the same name already exists in this hackathon.");

        Team team = new Team(name, hackathon, leader);
        team.addTeamMember(leader);
        userStateService.changeUserState(leader, true, hackathon, UserStateType.TEAM_LEADER);

        teamRepository.save(team);
    }

    /**
     * Metodo di supporto privato per la rimozione di un membro dal team.
     */
    private void removeTeamMember(User user, Team team) {
        if (user == null)
            throw new IllegalArgumentException("User cannot be null.");
        if (team == null)
            throw new IllegalArgumentException("Team cannot be null.");

        if (user.equals(team.getTeamLeader()))
            throw new UnsupportedOperationException("Cannot remove the team leader with this method.");

        if (!team.getTeamMembersList().contains(user))
            throw new IllegalStateException("User is not a member of the team.");

        EventManager.getInstance().notify(EventType.REMOVED_MEMBER_FROM_TEAM, List.of(user), "you have been removed from the team" + team.getId(), team.getHackathon());
        team.removeTeamMember(user);
        teamRepository.save(team);
    }
}