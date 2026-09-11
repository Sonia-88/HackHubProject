/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 


import java.util.EnumSet;

/**
 * Rappresenta lo stato di Team Leader di un utente, estendendo i privilegi
 * di membro del team con la facoltà di gestire inviti, sottomissioni e leadership.
 */
public class TeamLeader extends UserState {

    /**
     * Costruisce lo stato {@code TeamLeader} inizializzando i permessi specifici del capo team.
     */
    public TeamLeader() {
        super(EnumSet.of(Permission.CAN_INVITE_USERS,
                Permission.DETAILED_INFO,
                Permission.TEAM_PERMISSION,
                Permission.CAN_CANCEL_INVITATION,
                Permission.CAN_UNSUBSCRIBE_TEAM,
                Permission.CAN_SEND_AID_REQUEST,
                Permission.CAN_HANDLE_AID_REQUEST,
                Permission.CAN_SEND_SUBMISSION,
                Permission.CAN_MODIFY_LEADER));
    }

    /**
     * Restituisce il tipo di stato corrispondente al team leader.
     *
     * @return l'enumerativo {@code UserStateType.TEAM_LEADER}
     */
    @Override
    public UserStateType getType() {
        return UserStateType.TEAM_LEADER;
    }
}