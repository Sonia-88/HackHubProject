/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.observer;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.service.NotificationService;
import com.project.hackhub.service.UserStateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener che gestisce l'eliminazione di un hackathon, aggiornando gli stati
 * degli utenti coinvolti e salvando le notifiche di cancellazione.
 */
@Component
@RequiredArgsConstructor
public class HackathonDeletionListener implements EventListener {

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
     * Esegue l'aggiornamento sullo stato degli utenti e invia la notifica di eliminazione.
     *
     * @param usersList la lista degli utenti da notificare
     * @param message il messaggio di notifica
     * @param entity l'hackathon eliminato
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("Entity must not be null");

        Hackathon hackathon = (Hackathon) entity;
        for (User user : usersList) {
            userStateService.changeUserState(user, false, hackathon, UserStateType.DEFAULT_STATE);

            notificationService.createAndSaveNotification(user, message);
        }
    }

    /**
     * Restituisce l'evento supportato.
     *
     * @return {@link EventType#HACKATHON_DELETION}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.HACKATHON_DELETION;
    }
}