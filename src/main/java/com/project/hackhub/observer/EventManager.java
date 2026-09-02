/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.observer;
 

import com.project.hackhub.model.user.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton class to handle listeners and events
 * @author Cosmina Androne
 */
@Component
public final class EventManager {

    private final List<EventListener> listeners = new ArrayList<>();
    private static final EventManager INSTANCE = new EventManager();

    /**
     * Restituisce l'istanza singleton di {@code EventManager}.
     *
     * @return l'istanza unica di EventManager
     */
    public static EventManager getInstance() {
        return INSTANCE;
    }

    /**
     * Notifies a list of {@link User} about a specific event happening
     *
     * @param e      the type of event happening
     * @param users  the users to notify
     * @param message il messaggio da recapitare
     * @param entity the entity to use
     * @throws IllegalArgumentException if the event or the entity are null or if the entity
     *                                  is not supported by the type of event
     * @author Cosmina Androne
     */
    public void notify(EventType e, List<User> users, String message, Object entity) {

        if (users == null || users.isEmpty()) return;
        if (e == null)
            throw new IllegalArgumentException("event type cannot be null");
        if (entity == null)
            throw new IllegalArgumentException("entity cannot be null");

        if (!e.getEntityClass().isInstance(entity)) {
            throw new IllegalArgumentException("Entity type mismatch");
        }

        for (EventListener el : listeners) {
            if (el.getSupportedEventType().equals(e)) {
                el.updateUsers(users,
                        message,
                        entity);
            }
        }
    }

    /**
     * adds a listener to the list
     *
     * @param listener the listener
     * @throws IllegalArgumentException if the listener to add is {@code null}
     * @author Cosmina Androne
     */
    public void addListenerToList(EventListener listener) {
        if (listener == null)
            throw new IllegalArgumentException("listener to add cannot be null");

        listeners.add(listener);
    }
}