/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.hackathon.state;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.report.ReportData;

/**
 * Rappresenta lo stato in corso (OnGoing) dell'Hackathon,
 * gestendo la visibilità dei dati operativi durante lo svolgimento dell'evento.
 */
public class OnGoing implements HackathonState {

    /**
     * Restituisce il tipo di stato corrispondente alla fase in corso.
     *
     * @return l'enumerativo {@code HackathonStateType.ONGOING}
     */
    @Override
    public HackathonStateType getStateType() {
        return HackathonStateType.ONGOING;
    }

    /**
     * Raccoglie e popola i dati del report specifici per la fase di svolgimento.
     *
     * @param h l'hackathon di riferimento
     * @return un oggetto {@code ReportData} con le informazioni operative
     */
    public ReportData getReportData(Hackathon h){
        ReportData r = new ReportData();
        //public data
        r.setName(h.getName());
        r.setRuleBook(h.getRuleBook());
        r.setState(h.getState());
        r.setTeamsList(h.getTeamsList());
        //details
        r.setCoordinator(h.getCoordinator());
        r.setJudge(h.getJudge());
        r.setMentorsList(h.getMentorsList());
        r.setAidRequests(h.getAidRequests());
        r.setInfractions(h.getInfractions());

        return r;
    }
}