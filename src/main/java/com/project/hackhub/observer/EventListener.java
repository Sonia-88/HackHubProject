/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.observer;
 

import com.project.hackhub.model.user.User;

import java.util.List;

/**
 * Interfaccia che definisce il contratto per i listener coinvolti nel pattern Observer.
 */
public interface EventListener{

    /**
     * Gets the {@link EventType} supported by this listener
     * @return the {@link EventType} supported by this listener
     * @author Cosmina Androne
     */
    EventType getSupportedEventType();

    /**
     * Aggiorna gli utenti associati all'evento verificatosi.
     *
     * @param usersList la lista degli utenti coinvolti
     * @param message il messaggio descrittivo dell'evento
     * @param entity l'entità di dominio associata all'evento
     */
    void updateUsers(List<User> usersList,
                     String message,
                     Object entity);


}