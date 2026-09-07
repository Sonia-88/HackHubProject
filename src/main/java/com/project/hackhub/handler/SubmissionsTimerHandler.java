/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.repository.HackathonRepository;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class SubmissionsTimerHandler {

    private final HackathonRepository hackathonRepository;

    /**
     * Closes the possibility to send submissions to a Hackathon changing its state in  {@link HackathonStateType#APPRAISAL}
     */
    @Scheduled(fixedRate = 300000) // ogni 5 minuti
    @Transactional
    public void handleExpiredSubmissions() {

        List<Hackathon> expiredHackathons = hackathonRepository.getExpiredSubmissions(LocalDateTime.now());

        for (Hackathon h : expiredHackathons) {
            h.setStateType(HackathonStateType.APPRAISAL);
            hackathonRepository.save(h);
        }
    }
}
