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
 * Listener che gestisce la disiscrizione di un team, aggiornando lo stato di tutti i membri
 * a {@code DEFAULT_STATE} e inviando la relativa notifica.
 */
@Component
@AllArgsConstructor
public class UnsubscribeTeamListener implements EventListener {

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
     * @return {@link EventType#UNSUBSCRIBE_TEAM}
     */
    @Override
    public EventType getSupportedEventType() {
        return EventType.UNSUBSCRIBE_TEAM;
    }

    /**
     * Aggiorna lo stato dei membri del team ritirato e crea le notifiche.
     *
     * @param usersList la lista dei membri del team
     * @param message il messaggio di notifica
     * @param entity il team disiscritto
     */
    @Override
    public void updateUsers(List<User> usersList, String message, Object entity) {
        if(usersList == null || usersList.isEmpty()) return;
        if(message == null) throw new IllegalArgumentException("message needed");
        if(entity == null) throw new IllegalArgumentException("team cannot be null");

        Team team = (Team) entity;
        for(User u: usersList) {
            userStateService.changeUserState(u, false, team.getHackathon(), UserStateType.DEFAULT_STATE);

            notificationService.createAndSaveNotification(u, message);
        }
    }
}