/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.repository;


import com.project.hackhub.model.hackathon.Notification;
import com.project.hackhub.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione e il recupero delle notifiche utente.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Restituisce la lista delle notifiche di un utente ordinate per data di creazione decrescente.
     *
     * @param user l'utente di riferimento
     * @return una lista di notifiche ordinate
     */
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    /**
     * Restituisce la lista delle notifiche associate a un utente tramite il suo UUID.
     *
     * @param userId l'identificativo UUID dell'utente
     * @return una lista di notifiche
     */
    List<Notification> findByUserId(UUID userId);
}