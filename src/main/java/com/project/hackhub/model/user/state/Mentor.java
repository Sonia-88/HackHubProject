/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.user.state;


import java.util.EnumSet;

/**
 * Rappresenta lo stato di Mentor di un utente, definendo i permessi
 * per la segnalazione di infrazioni e la gestione delle richieste di supporto.
 */
public class Mentor extends UserState {

    /**
     * Costruisce lo stato {@code Mentor} inizializzando i permessi di mentoring.
     */
    public Mentor(){
        super(EnumSet.of(Permission.CAN_REPORT_INFRACTION,
                Permission.CAN_PROPOSE_CALL,
                Permission.DETAILED_INFO,
                Permission.STAFF_PERMISSION,
                Permission.CAN_HANDLE_AID_REQUEST,
                Permission.CAN_DELETE_INFRACTION));
    }

    /**
     * Restituisce il tipo di stato corrispondente al mentor.
     *
     * @return l'enumerativo {@code UserStateType.MENTOR}
     */
    @Override
    public UserStateType getType() {
        return UserStateType.MENTOR;
    }
}