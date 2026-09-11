/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.user.state;


/**
 * Enumerazione che elenca tutti i permessi granulari che possono essere associati
 * ai diversi ruoli e stati degli utenti all'interno di un hackathon.
 */
public enum Permission {
    CAN_INVITE_USERS,
    CAN_MANAGE_INFRACTIONS,
    CAN_MODIFY_HACKATHON,
    CAN_GRADE_SUBMISSION,
    CAN_CREATE_TEAM,
    CAN_PROPOSE_CALL,
    CAN_REPORT_INFRACTION,
    CAN_UNSUBSCRIBE_TEAM,
    STAFF_PERMISSION,
    CAN_SEND_AID_REQUEST,
    DETAILED_INFO,
    CAN_MODIFY_LEADER,
    TEAM_PERMISSION,
    CAN_MANAGE_STAFF,
    CAN_MANAGE_TEAMS,
    CAN_DELETE_HACKATHON,
    CAN_CANCEL_INVITATION,
    CAN_SEND_SUBMISSION,
    CAN_EXPEL_TEAM,
    CAN_PENALIZE_TEAM,
    CAN_PROCLAIM_WINNER,
    CAN_ADD_TASK,
    CAN_ACCEPT_INVITATION,
    CAN_DECLINE_INVITATION,
    CAN_HANDLE_AID_REQUEST,
    CAN_DELETE_INFRACTION
}