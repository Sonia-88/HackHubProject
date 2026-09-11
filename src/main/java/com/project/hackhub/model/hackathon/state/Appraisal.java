/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.state;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.report.ReportData;

/**
 * Rappresenta lo stato di valutazione (Appraisal) dell'Hackathon,
 * in cui vengono definiti i dati del report relativi a questa fase.
 */
public class Appraisal implements HackathonState {

    /**
     * Restituisce il tipo di stato corrispondente alla fase di valutazione.
     *
     * @return l'enumerativo {@code HackathonStateType.APPRAISAL}
     */
    @Override
    public HackathonStateType getStateType() {
        return HackathonStateType.APPRAISAL;
    }

    /**
     * Raccoglie e popola i dati del report specifici per la fase di valutazione.
     *
     * @param h l'hackathon di riferimento
     * @return un oggetto {@code ReportData} con le informazioni della fase
     */
    @Override
    public ReportData getReportData(Hackathon h){
        ReportData r = new ReportData();
        //public data
        r.setName(h.getName());
        r.setRuleBook(h.getRuleBook());
        r.setState(h.getState());
        //dettagli
        r.setCoordinator(h.getCoordinator());
        r.setJudge(h.getJudge());
        r.setMentorsList(h.getMentorsList());
        r.setInfractions(h.getInfractions());

        return r;
    }
}