/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.state;
 


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.report.ReportData;

/**
 * Rappresenta lo stato concluso (Concluded) dell'Hackathon,
 * gestendo la generazione del report finale con vincitori e voti.
 */
public class Concluded implements HackathonState {

    /**
     * Restituisce il tipo di stato corrispondente alla fase conclusiva.
     *
     * @return l'enumerativo {@code HackathonStateType.CONCLUDED}
     */
    @Override
    public HackathonStateType getStateType() {
        return HackathonStateType.CONCLUDED;
    }

    /**
     * Raccoglie e popola i dati del report specifici per la fase conclusiva.
     *
     * @param h l'hackathon di riferimento
     * @return un oggetto {@code ReportData} con le informazioni finali dell'evento
     */
    @Override
    public ReportData getReportData(Hackathon h){
        ReportData r = new ReportData();
        r.setName(h.getName());
        r.setRuleBook(h.getRuleBook());
        r.setState(h.getState());
        r.setTeamsGrades(h.getTeamsGrades());
        r.setCoordinator(h.getCoordinator());
        r.setJudge(h.getJudge());
        r.setMentorsList(h.getMentorsList());
        r.setWinner(h.getWinner());

        return r;
    }
}