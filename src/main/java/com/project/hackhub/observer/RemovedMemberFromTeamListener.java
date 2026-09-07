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
 * Listener che gestisce la rimozione di un membro da un team, reimpostando il suo stato
 * a {@code DEFAULT_STATE} e inviando la notifica corrispondente.
 */
@Component
@RequiredArgsConstructor
public class RemovedMemberFromTeamListener implements EventListener {

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
     * Aggiorna lo stato dell'utente rimosso e invia la notifica di sistema.
     *
     * @param usersList la lista degli utenti (l'utente rimosso in testa)
     * @param message il messaggio di notifica
     * @param entity l'hackathon associato
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("Entity must not be null");
        if (!(entity instanceof Hackathon hackathon)) {
            throw new IllegalArgumentException("Entity must be a Hackathon");
        }

        User user = usersList.getFirst();
        userStateService.changeUserState(user, false, hackathon, UserStateType.DEFAULT_STATE);

        notificationService.createAndSaveNotification(user, message);
    }

    /**
     * Restituisce l'evento supportato.
     *
     * @return {@link EventType#REMOVED_MEMBER_FROM_TEAM}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.REMOVED_MEMBER_FROM_TEAM;
    }
}