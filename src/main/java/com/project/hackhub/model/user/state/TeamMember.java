/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 

import java.util.EnumSet;

/**
 * Rappresenta lo stato di Membro del Team (TeamMember) di un utente,
 * definendo i permessi di partecipazione all'interno di una squadra.
 */
public class TeamMember extends UserState {

    /**
     * Costruisce lo stato {@code TeamMember} inizializzando i permessi di base della squadra.
     */
    public TeamMember() {
        super(EnumSet.of(Permission.CAN_INVITE_USERS,
                Permission.DETAILED_INFO,
                Permission.TEAM_PERMISSION,
                Permission.CAN_CANCEL_INVITATION));
    }

    /**
     * Restituisce il tipo di stato corrispondente al membro del team.
     *
     * @return l'enumerativo {@code UserStateType.TEAM_MEMBER}
     */
    @Override
    public UserStateType getType() {
        return UserStateType.TEAM_MEMBER;
    }
}