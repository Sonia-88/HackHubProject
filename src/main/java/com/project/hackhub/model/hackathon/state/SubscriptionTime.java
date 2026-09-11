/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.state;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.report.ReportData;

/**
 * Rappresenta lo stato della fase di iscrizione (SubscriptionTime) dell'Hackathon,
 * gestendo la raccolta dei dati informativi utili ai partecipanti prima dell'inizio.
 */
public class SubscriptionTime implements HackathonState {

    /**
     * Restituisce il tipo di stato corrispondente alla fase di iscrizione.
     *
     * @return l'enumerativo {@code HackathonStateType.SUBSCRIPTION_PHASE}
     */
    @Override
    public HackathonStateType getStateType() {
        return HackathonStateType.SUBSCRIPTION_PHASE;
    }

    /**
     * Raccoglie e popola i dati del report specifici per la fase di iscrizione.
     *
     * @param h hackathon di riferimento
     * @return un oggetto {@code ReportData} con i dettagli di iscrizione e prenotazione
     */
    public ReportData getReportData(Hackathon h){
        ReportData r = new ReportData();
        //public data
        r.setName(h.getName());
        r.setRuleBook(h.getRuleBook());
        r.setState(h.getState());
        r.setMoneyPrice(h.getMoneyPrize());
        r.setExpiredSubscriptionsDate(h.getExpiredSubscriptionsDate());
        r.setReservation(h.getReservation());
        r.setMaxTeamDimension(h.getMaxTeamDimension());
        //details
        r.setCoordinator(h.getCoordinator());
        r.setJudge(h.getJudge());
        r.setMentorsList(h.getMentorsList());

        return r;
    }
}