/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.report.HackathonReportAssembler;
import com.project.hackhub.model.hackathon.report.Report;
import com.project.hackhub.model.hackathon.report.ReportData;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Gestisce il recupero delle informazioni generali relative agli hackathon
 * e la generazione dei report dettagliati in base allo stato dell'evento
 * e ai permessi dell'utente richiedente.
 */
@Component
public class InfoHandler {
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final HackathonReportAssembler reportBuilder = new HackathonReportAssembler();

    public InfoHandler(HackathonRepository hr, UserRepository ur){
        this.hackathonRepository = hr;
        this.userRepository = ur;
    }

    /**
     * Restituisce una lista contenente gli identificativi univoci di tutti gli Hackathon presenti nel sistema.
     *
     * @return una {@code List} di {@code UUID} rappresentante gli ID di tutti gli Hackathon
     * @author Sonia Bevilacqua
     */
    @Transactional
    public List<UUID> getAllHackathons() {
        List<Hackathon> list = this.hackathonRepository.findAll();
        List<UUID> res = new ArrayList<>();
        for(Hackathon h : list)
            res.add(h.getId());
        return res;}

    /**
     * Restituisce un report contenente informazioni su un determinato Hackathon in base al suo stato
     * corrente e ai permessi dell'utente che effettua la richiesta.
     *
     * @param hackathonId un ID univoco associato a un determinato Hackathon
     * @param userId un ID univoco associato all'utente che desidera accedere al report (può essere null per i visitatori)
     * @return un oggetto {@code Report} dell'Hackathon se l'ID è mappato correttamente
     * @throws IllegalArgumentException se l'hackathonId non è associato ad alcun Hackathon esistente
     * @author Cosmina Androne
     */
    @Transactional
    public Report getHackathonReport(UUID hackathonId, UUID userId){
        Hackathon h = hackathonRepository.findById(hackathonId).orElse(null);
        if (h == null)
            throw new IllegalArgumentException("Hackathon given does not exist");

        User u = null;
        if (userId != null) {
            u = userRepository.findById(userId).orElse(null);
        }

        ReportData data = h.getState().getReportData(h);

        return reportBuilder.build(data, h, u);
    }
}