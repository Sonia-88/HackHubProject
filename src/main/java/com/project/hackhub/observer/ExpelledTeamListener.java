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
 * Listener che gestisce l'evento di espulsione di un team, reimpostando lo stato
 * dei membri coinvolti a {@code DEFAULT_STATE} e inviando le relative notifiche.
 */
@Component
@AllArgsConstructor
public class ExpelledTeamListener implements EventListener {

    private final UserStateService userStateService;
    private final NotificationService notificationService;

    /**
     * Registra automaticamente il listener nell'{@link EventManager} all'avvio.
     */
    @PostConstruct
    public void init() {
        EventManager.getInstance().addListenerToList(this);
    }

    /**
     * Restituisce il tipo di evento supportato.
     *
     * @return {@link EventType#EXPULSION_TEAM}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.EXPULSION_TEAM;
    }

    /**
     * Aggiorna gli utenti notificando l'espulsione e aggiornandone lo stato.
     *
     * @param usersList la lista degli utenti da aggiornare
     * @param message il messaggio di notifica
     * @param entity l'entità associata (Hackathon)
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("hackathon cannot be null");

        Hackathon hackathon = (Hackathon) entity;
        for(User u: usersList) {
            userStateService.changeUserState(u, false, hackathon, UserStateType.DEFAULT_STATE);

            notificationService.createAndSaveNotification(u, message);
        }
    }
}