/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 

import java.util.EnumSet;

/**
 * Rappresenta lo stato di Coordinatore di un utente, definendo l'insieme
 * dei permessi amministrativi e di gestione dell'hackathon associati a questo ruolo.
 */
public class Coordinator extends UserState {

    /**
     * Costruisce lo stato {@code Coordinator} inizializzando i permessi specifici del ruolo.
     */
    public Coordinator() {
        super(EnumSet.of(Permission.CAN_MODIFY_HACKATHON,
                Permission.CAN_MANAGE_INFRACTIONS,
                Permission.DETAILED_INFO,
                Permission.STAFF_PERMISSION,
                Permission.CAN_PROCLAIM_WINNER,
                Permission.CAN_ADD_TASK,
                Permission.CAN_EXPEL_TEAM,
                Permission.CAN_PENALIZE_TEAM,
                Permission.CAN_DELETE_HACKATHON,
                Permission.CAN_MANAGE_STAFF,
                Permission.CAN_DELETE_INFRACTION
        ));

    }

    /**
     * Restituisce il tipo di stato corrispondente al coordinatore.
     *
     * @return l'enumerativo {@code UserStateType.COORDINATOR}
     */
    @Override
    public UserStateType getType() {
        return UserStateType.COORDINATOR;
    }
}