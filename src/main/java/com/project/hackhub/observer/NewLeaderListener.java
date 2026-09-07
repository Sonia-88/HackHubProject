/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.observer;


import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.service.NotificationService;
import com.project.hackhub.service.UserStateService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener che gestisce l'evento di nomina di un nuovo team leader,
 * aggiornando lo stato dell'utente a {@code TEAM_LEADER} e inviando la notifica.
 */
@Component
@AllArgsConstructor
public class NewLeaderListener implements EventListener {

    private final UserStateService userStateService;
    private final NotificationService notificationService;

    /**
     * Registra il listener nell'{@link EventManager} all'avvio.
     */
    @PostConstruct
    public void init() {
        EventManager.getInstance().addListenerToList(this);
    }

    /**
     * Restituisce l'evento supportato.
     *
     * @return {@link EventType#NEW_LEADER}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.NEW_LEADER;
    }

    /**
     * Aggiorna lo stato dell'utente designato a nuovo leader e crea la notifica.
     *
     * @param usersList la lista degli utenti (il nuovo leader in testa)
     * @param message il messaggio di notifica
     * @param entity il team di riferimento
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("team cannot be null");

        Team team = (Team) entity;
        User user = usersList.getFirst();

        userStateService.changeUserState(user, true, team.getHackathon(), UserStateType.TEAM_LEADER);
        notificationService.createAndSaveNotification(user, message);
    }
}