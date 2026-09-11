/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon;


import com.project.hackhub.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entità JPA che rappresenta una notifica di sistema inviata a un utente.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Il messaggio testuale della notifica. */
    private String message;

    /** La data e l'ora di creazione della notifica. */
    private LocalDateTime createdAt;

    /** Flag che indica se la notifica è stata letta (default: false). */
    private boolean isRead = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Costruisce una nuova notifica associata a un utente con il messaggio specificato.
     *
     * @param user l'utente destinatario della notifica
     * @param message il testo della notifica
     */
    public Notification(User user, String message) {
        this.user = user;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }
}