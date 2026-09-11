/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.state;
 


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.report.ReportData;

/**
 * Interfaccia che definisce il contratto per i vari stati dell'Hackathon
 * secondo il pattern State.
 */
public interface HackathonState {

    /**
     * Restituisce il tipo di stato dell'hackathon.
     *
     * @return l'enumerativo {@code HackathonStateType}
     */
    HackathonStateType getStateType();

    /**
     * Genera e restituisce i dati del report in base allo stato corrente.
     *
     * @param h l'hackathon di riferimento
     * @return un oggetto {@code ReportData}
     */
    ReportData getReportData(Hackathon h);

}