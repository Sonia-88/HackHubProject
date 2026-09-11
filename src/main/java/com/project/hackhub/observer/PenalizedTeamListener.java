/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.observer;


import com.project.hackhub.model.user.User;
import com.project.hackhub.service.NotificationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener che gestisce la notifica ai membri di un team in seguito a una penalizzazione subita.
 */
@Component
@RequiredArgsConstructor
public class PenalizedTeamListener implements EventListener {

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
     * @return {@link EventType#PENALIZED_TEAM}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.PENALIZED_TEAM;
    }

    /**
     * Invia la notifica di penalizzazione a tutti gli utenti del team.
     *
     * @param usersList la lista degli utenti da notificare
     * @param message il messaggio descrittivo della penalizzazione
     * @param entity l'entità di contesto
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("hackathon cannot be null");

        for (User user : usersList) {
            notificationService.createAndSaveNotification(user, message);
        }
    }
}