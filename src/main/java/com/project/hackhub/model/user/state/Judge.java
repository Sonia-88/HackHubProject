/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.user.state;


import java.util.EnumSet;

/**
 * Rappresenta lo stato di Giudice (Judge) di un utente, definendo
 * i permessi relativi alla valutazione delle sottomissioni e l'accesso allo staff.
 */
public class Judge extends UserState {

    /**
     * Costruisce lo stato {@code Judge} inizializzando i permessi specifici di valutazione.
     */
    public Judge() {
        super(EnumSet.of(Permission.CAN_GRADE_SUBMISSION,
                Permission.DETAILED_INFO,
                Permission.STAFF_PERMISSION));

    }

    /**
     * Restituisce il tipo di stato corrispondente al giudice.
     *
     * @return l'enumerativo {@code UserStateType.JUDGE}
     */
    @Override
    public UserStateType getType() {
        return UserStateType.JUDGE;
    }
}