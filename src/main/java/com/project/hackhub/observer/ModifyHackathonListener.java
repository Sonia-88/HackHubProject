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
 * Listener che gestisce la notifica agli utenti in caso di modifiche apportate a un hackathon.
 */
@Component
@RequiredArgsConstructor
public class ModifyHackathonListener implements EventListener{

    private final NotificationService notificationService;

    /**
     * Registra il listener nell'{@link EventManager} all'avvio.
     */
    @PostConstruct
    public void init() {
        EventManager.getInstance().addListenerToList(this);
    }

    /**
     * Notifica gli utenti in merito alle modifiche effettuate all'evento.
     *
     * @param usersList la lista degli utenti da notificare
     * @param message il messaggio di aggiornamento
     * @param entity l'entità di contesto
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("Entity must not be null");

        for (User user : usersList) {
            notificationService.createAndSaveNotification(user, message);
        }
    }

    /**
     * Restituisce l'evento supportato.
     *
     * @return {@link EventType#MODIFIED_HACKATHON}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.MODIFIED_HACKATHON;
    }
}