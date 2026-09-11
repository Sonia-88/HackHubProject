/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.observer;


import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.user.User;
import com.project.hackhub.service.NotificationService;
import com.project.hackhub.service.UserStateService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener che gestisce l'invio di un invito a un utente, registrandolo nel sistema
 * tramite il servizio di gestione stati e inviando la notifica.
 */
@Component
@AllArgsConstructor
public class UserInvitationListener implements EventListener {

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
     * @return {@link EventType#USER_INVITATION}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.USER_INVITATION;
    }

    /**
     * Associa l'invito all'utente destinatario e crea la notifica.
     *
     * @param usersList la lista degli utenti (l'utente invitato in testa)
     * @param message il messaggio di invito
     * @param entity l'invito associato
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("invitation cannot be null");

        Invitation invitation = (Invitation) entity;
        User user = usersList.getFirst();

        userStateService.addInvitation(user, invitation);
        notificationService.createAndSaveNotification(user, message);
    }
}