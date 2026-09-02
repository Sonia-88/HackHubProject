/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.team;
 

import com.project.hackhub.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entità JPA che rappresenta un invito inviato da un team a un utente per unirsi alla squadra.
 */
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Invitation {

    @ManyToOne
    private Team sender;

    @ManyToOne
    @JoinColumn(name = "adressee_id")
    private User addressee;

    @Id
    @GeneratedValue
    private UUID id;

    boolean pending;

    /**
     * Costruisce un nuovo invito impostando il team mittente, l'utente destinatario e lo stato di attesa.
     *
     * @param team il team mittente
     * @param user l'utente destinatario
     * @throws IllegalArgumentException se il team o l'utente sono nulli
     */
    public Invitation(Team team, User user) {
        if (team == null || user == null)
            throw new IllegalArgumentException("Invalid parameters.");

        this.sender = team;
        this.addressee = user;
        this.pending = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invitation other = (Invitation) o;

        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}