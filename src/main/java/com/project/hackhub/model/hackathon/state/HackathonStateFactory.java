/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.state;
 

/**
 * Factory class responsabile della creazione dinamica delle istanze di {@code HackathonState}
 * in base al tipo di stato richiesto.
 */
public class HackathonStateFactory {

    /**
     * Crea e restituisce l'istanza concreta dello stato dell'hackathon.
     *
     * @param st il tipo di stato desiderato
     * @return l'implementazione corrispondente di {@code HackathonState}
     */
    public HackathonState createState(HackathonStateType st){

        return switch(st){
            case SUBSCRIPTION_PHASE -> new SubscriptionTime();
            case ONGOING -> new OnGoing();
            case APPRAISAL -> new Appraisal();
            case CONCLUDED -> new Concluded();
        };
    };


}