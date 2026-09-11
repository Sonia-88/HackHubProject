/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.observer;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.team.Team;
import lombok.Getter;

/**
 * Enumerazione che definisce le diverse tipologie di eventi gestite dal sistema di notifiche e osservatori.
 */
public enum EventType {

    /** Eliminazione di un hackathon. */
    HACKATHON_DELETION(Hackathon.class),
    /** Rimozione di un membro dal team. */
    REMOVED_MEMBER_FROM_TEAM(Hackathon.class),
    /** Modifica dei dati di un hackathon. */
    MODIFIED_HACKATHON(Hackathon.class),
    /** Segnalazione di un'infrazione. */
    INFRACTION(Hackathon.class),
    /** Proclamazione del team vincitore. */
    PROCLAIM_WINNER(Hackathon.class),
    /** Espulsione di un team. */
    EXPULSION_TEAM(Hackathon.class),
    /** Disiscrizione di un team. */
    UNSUBSCRIBE_TEAM(Team.class),
    /** Nomina di un nuovo leader nel team. */
    NEW_LEADER(Team.class),
    /** Invito inviato a un utente. */
    USER_INVITATION(Invitation.class),
    /** Penalizzazione di un team. */
    PENALIZED_TEAM(Hackathon.class),
    /** Modifica del ruolo nello staff. */
    CHANGE_STAFF_ROLE(Hackathon.class),
    /** Proposta di chiamata pendente. */
    PENDING_CALL_PROPOSAL(Hackathon.class);

    @Getter private final Class<?> entityClass;

    EventType(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

}