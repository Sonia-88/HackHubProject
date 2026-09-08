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
 * Listener che gestisce la ricezione e la notifica di nuove infrazioni registrate nel sistema.
 */
@Component
@RequiredArgsConstructor
public class InfractionListener implements EventListener {

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
     * @return {@link EventType#INFRACTION}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.INFRACTION;
    }

    /**
     * Invia la notifica di infrazione agli utenti interessati.
     *
     * @param usersList la lista degli utenti da notificare
     * @param message il messaggio descrittivo dell'infrazione
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