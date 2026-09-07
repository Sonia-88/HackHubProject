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
 * Listener che gestisce la notifica ai partecipanti in seguito alla proclamazione del team vincitore.
 */
@Component
@RequiredArgsConstructor
public class ProclaimedWinnerListener implements EventListener {

    private final NotificationService notificationService;

    /**
     * Registra il listener nell'{@link EventManager} all'avvio.
     */
    @PostConstruct
    public void init() {
        EventManager.getInstance().addListenerToList(this);
    }

    /**
     * Invia la notifica di proclamazione del vincitore agli utenti.
     *
     * @param usersList la lista degli utenti da notificare
     * @param message il messaggio di vittoria
     * @param entity l'hackathon di riferimento
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("Hackathon cannot be null");

        for (User user : usersList) {
            notificationService.createAndSaveNotification(user, message);
        }
    }

    /**
     * Restituisce l'evento supportato.
     *
     * @return {@link EventType#PROCLAIM_WINNER}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.PROCLAIM_WINNER;
    }
}