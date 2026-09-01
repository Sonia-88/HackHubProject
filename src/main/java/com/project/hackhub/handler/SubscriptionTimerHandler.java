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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gestisce la chiusura temporizzata delle iscrizioni e degli hackathon,
 * aggiornando i relativi stati nel database in modo schedulato.
 */
@Component
@AllArgsConstructor
public class SubscriptionTimerHandler {

    private final HackathonRepository hackathonRepository;

    /**
     * Chiude la fase di iscrizione degli hackathon scaduti portandoli nello stato ONGOING.
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void handleExpiredSubscriptions() {

        List<Hackathon> expiredHackathons = hackathonRepository.getExpiredSubscriptions(LocalDateTime.now());

        for (Hackathon h : expiredHackathons) {
            h.setStateType(HackathonStateType.ONGOING);
            hackathonRepository.save(h);
            System.out.println("L'Hackathon '" + h.getName() + "' è ora in stato ONGOING (Iscrizioni chiuse)!");
        }
    }

    /**
     * Conclude gli hackathon scaduti spostandoli nella fase di valutazione APPRAISAL.
     */
    @Scheduled(fixedRate = 300000)
    @Transactional
    public void handleEndedHackathons() {

        List<Hackathon> endedHackathons = hackathonRepository.getExpiredSubmissions(LocalDateTime.now());

        for (Hackathon h : endedHackathons) {
            h.setStateType(HackathonStateType.APPRAISAL);
            hackathonRepository.save(h);
            System.out.println("L'Hackathon '" + h.getName() + "' è terminato! Passato in stato APPRAISAL (Valutazione).");
        }
    }
}