/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 

import java.util.EnumSet;

/**
 * Rappresenta lo stato predefinito (DefaultState) di un utente all'interno di un hackathon,
 * ovvero quando non ricopre ruoli speciali o non fa parte di alcun team.
 */
public class DefaultState extends UserState {

    /**
     * Costruisce lo stato predefinito assegnando i permessi base di visualizzazione e creazione team.
     */
    public DefaultState() {
        super(EnumSet.of(Permission.CAN_CREATE_TEAM,
                Permission.DETAILED_INFO,
                Permission.CAN_ACCEPT_INVITATION,
                Permission.CAN_DECLINE_INVITATION));
    }

    /**
     * Restituisce il tipo di stato predefinito.
     *
     * @return l'enumerativo {@code UserStateType.DEFAULT_STATE}
     */
    @Override
    public UserStateType getType() {
        return UserStateType.DEFAULT_STATE;
    }
}