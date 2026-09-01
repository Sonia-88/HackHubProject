/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 

/**
 * Factory class responsabile della creazione dinamica delle istanze di {@code UserState}
 * in base al tipo di stato utente specificato.
 */
public class UserStateFactory {

    /**
     * Crea e restituisce l'istanza concreta di {@code UserState} corrispondente.
     *
     * @param st il tipo di stato utente {@code UserStateType}
     * @return l'implementazione concreta di {@code UserState}
     */
    public UserState createUserState(UserStateType st) {
        return switch (st) {
            case COORDINATOR -> new Coordinator();
            case MENTOR -> new Mentor();
            case JUDGE -> new Judge();
            case TEAM_MEMBER -> new TeamMember();
            case TEAM_LEADER -> new TeamLeader();
            case DEFAULT_STATE -> new DefaultState();
        };
    }
}