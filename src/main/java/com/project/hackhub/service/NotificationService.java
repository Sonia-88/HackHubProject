/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.service;
 

import com.project.hackhub.model.hackathon.Notification;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servizio per la gestione e la persistenza delle notifiche di sistema destinate agli utenti.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Crea e salva una nuova notifica associata a un utente.
     *
     * @param user l'utente destinatario della notifica
     * @param message il testo del messaggio della notifica
     */
    public void createAndSaveNotification(User user, String message) {
        Notification notification = new Notification(user, message);
        notificationRepository.save(notification);
    }

    /**
     * Recupera tutte le notifiche associate a uno specifico utente tramite il suo UUID.
     *
     * @param userId l'identificativo UUID dell'utente
     * @return una lista di notifiche
     */
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserId(userId);
    }

}