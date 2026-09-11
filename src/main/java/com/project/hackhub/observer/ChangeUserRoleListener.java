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
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Listener che gestisce l'evento di cambio di ruolo nello staff, aggiornando lo stato
 * dell'utente e creando una notifica di sistema dedicata.
 */
@Component
@AllArgsConstructor
public class ChangeUserRoleListener implements EventListener {

    private final UserStateService userStateService;
    private final NotificationService notificationService;

    /**
     * Registra automaticamente questo listener all'interno dell'{@link EventManager} all'avvio.
     */
    @PostConstruct
    public void init() {
        EventManager.getInstance().addListenerToList(this);
    }

    /**
     * Restituisce il tipo di evento supportato da questo listener.
     *
     * @return {@link EventType#CHANGE_STAFF_ROLE}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.CHANGE_STAFF_ROLE;
    }

    /**
     * Aggiorna gli utenti notificati modificandone lo stato in {@code DEFAULT_STATE} e inviando una notifica.
     *
     * @param usersList la lista degli utenti da aggiornare
     * @param message il messaggio della notifica
     * @param entity l'entità di contesto (Hackathon)
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if (usersList == null || usersList.isEmpty()) return;
        if (message == null) throw new IllegalArgumentException("message needed");
        if (entity == null) throw new IllegalArgumentException("hackathon cannot be null");

        Hackathon hackathon = (Hackathon) entity;
        User user = usersList.getFirst();

        userStateService.changeUserState(user, false, hackathon, UserStateType.DEFAULT_STATE);

        notificationService.createAndSaveNotification(user, message);
    }
}