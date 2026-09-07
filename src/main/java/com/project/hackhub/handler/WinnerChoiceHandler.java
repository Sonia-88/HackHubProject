/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Submission;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.SubmissionRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.project.hackhub.observer.EventType.PROCLAIM_WINNER;

/**
 * Gestisce la proclamazione del team vincitore dell'hackathon e il recupero delle squadre partecipanti.
 */
@AllArgsConstructor
@Service
public class WinnerChoiceHandler {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SubmissionRepository submissionRepository;

    /**
     * Proclama il team vincitore dell'hackathon dopo aver verificato le valutazioni e lo stato dell'evento.
     *
     * @param teamId l'identificatore del team vincitore
     * @param organizerId l'identificatore dell'organizzatore
     * @param hackathonId l'identificatore dell'hackathon
     * @return il team proclamato vincitore
     */
    @Transactional
    public Team proclaimWinner(UUID teamId, UUID organizerId, UUID hackathonId) {

        Team t = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        if(!h.getTeamsList().contains(t))
            throw new IllegalArgumentException("Team is not part of the hackathon");
        if(!organizer.hasPermission(Permission.CAN_PROCLAIM_WINNER, h))
            throw new IllegalArgumentException("User does not have required permission");
        if(h.getStateType() != HackathonStateType.APPRAISAL)
            throw new IllegalStateException("Hackathon is not in the right state to proclaim winner");

        if(!checkAppraisals(h))
            throw new IllegalArgumentException("Not all submissions have been appraised yet, cannot proclaim winner");

        h.setWinner(t);
        List<User> usersToUpdate = h.getTeamsList().stream()
                .flatMap(team -> team.getTeamMembersList().stream())
                .toList();
        EventManager.getInstance().notify(PROCLAIM_WINNER, usersToUpdate, "the team winner has been proclaimed!", h);
        setConcluded(h);
        return t;
    }

    /**
     * Verifica che tutte le sottomissioni siano state valutate.
     */
    private boolean checkAppraisals(Hackathon h) {

        List<Submission> submissions = submissionRepository.findLatestSubmissionsByHackathon(h);

        for(Submission s : submissions) {
            if(s.getGrade() == null)
                return false;
        }
        return true;
    }

    /**
     * Imposta lo stato dell'Hackathon su CONCLUDED e salva le modifiche.
     */
    private void setConcluded(Hackathon h) {
        h.setStateType(HackathonStateType.CONCLUDED);
        this.hackathonRepository.save(h);
    }

    /**
     * Restituisce tutti gli identificativi dei team partecipanti all'hackathon.
     *
     * @param organizerId l'identificatore dell'organizzatore
     * @param hackathonId l'identificatore dell'hackathon
     * @return una lista di UUID dei team
     */
    @Transactional
    public List<UUID> getAllTeams(UUID organizerId, UUID hackathonId) {

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon h = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        if(!organizer.hasPermission(Permission.CAN_PROCLAIM_WINNER, h))
            throw new IllegalArgumentException("User does not have required permission");
        if(h.getStateType() != HackathonStateType.APPRAISAL)
            throw new IllegalStateException("Hackathon is in the wrong state");

        return h.getTeamsList().stream().map(Team::getId).toList();
    }
}