/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.dto.SubmissionDTO;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.FileTemplate;
import com.project.hackhub.model.team.Submission;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.SubmissionRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Gestisce l'invio delle sottomissioni dei progetti da parte dei team e il recupero
 * delle sottomissioni globali per lo staff.
 */
@Service
public class SubmissionHandler {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;

    public SubmissionHandler(TeamRepository teamRepository, UserRepository userRepository, SubmissionRepository submissionRepository, HackathonRepository hackathonRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.submissionRepository = submissionRepository;
        this.hackathonRepository = hackathonRepository;
    }

    /**
     * Invia una nuova sottomissione per un team durante lo svolgimento dell'hackathon.
     *
     * @param teamLeader l'identificatore del team leader che effettua l'invio
     * @param dto il DTO con i dettagli della sottomissione
     */
    @Transactional
    public void sendSubmission(UUID teamLeader, SubmissionDTO dto){

        User leader = userRepository.findById(teamLeader).orElseThrow(()-> new IllegalArgumentException("teamLeader not found"));
        Team t = teamRepository.findById(dto.teamId()).orElseThrow(()-> new IllegalArgumentException("Team not found"));

        if(!t.getTeamLeader().equals(leader)) throw new IllegalArgumentException("TeamLeader doesn't match the given Team");
        if(!t.getHackathon().getState().getStateType().equals(HackathonStateType.ONGOING)) throw new IllegalStateException("Hackathon is not in ONGOING state");
        if(leader.hasPermission(Permission.CAN_SEND_SUBMISSION, t.getHackathon())){
            FileTemplate ft = new FileTemplate();
            ft.setFileName(dto.fileName());
            Submission s = new Submission(t, ft);
            s.setHackathon(t.getHackathon());
            this.submissionRepository.save(s);
        }
    }

    /**
     * Restituisce le ultime sottomissioni di tutti i team per un dato hackathon (riservato allo staff).
     *
     * @param user l'identificatore dell'utente dello staff
     * @param hackathon l'identificatore dell'hackathon
     * @return la lista delle sottomissioni
     */
    @Transactional
    public List<Submission> getAllTeamsSubmissions(UUID user, UUID hackathon){
        User u = userRepository.findById(user)
                .orElseThrow(()-> new IllegalArgumentException("staff not found"));
        Hackathon h = hackathonRepository.findById(hackathon)
                .orElseThrow(()-> new IllegalArgumentException("Hackathon not found"));
        if(h.getState().getStateType() != HackathonStateType.APPRAISAL &&
                h.getState().getStateType() != HackathonStateType.CONCLUDED)
            throw new IllegalStateException("Hackathon state is not APPRAISAL or CONCLUDED");
        if(u.hasPermission(Permission.STAFF_PERMISSION, h))
            return this.submissionRepository.findLatestSubmissionsByHackathon(h);
        else throw new IllegalArgumentException("user does not have the required permission");
    }
}