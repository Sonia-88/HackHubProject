/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user.state;
 

import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Set;

/**
 * Classe astratta base che implementa il pattern State per gestire
 * i permessi associati ai vari ruoli degli utenti.
 */
@NoArgsConstructor
public abstract class UserState {

    protected Set<Permission> permissions;

    /**
     * Costruisce uno stato utente con il set di permessi specificato.
     *
     * @param permissions l'insieme dei permessi concessi
     */
    protected UserState(Set<Permission> permissions) {
        this.permissions = (permissions == null)? Collections.emptySet() : permissions;
    }

    /**
     * Restituisce il tipo di stato associato.
     *
     * @return l'enumerativo {@code UserStateType}
     */
    public abstract UserStateType getType();

    /**
     * Verifica se lo stato corrente include un determinato permesso.
     *
     * @param p il permesso da verificare
     * @return true se il permesso è presente, false altrimenti
     */
    public boolean hasPermission(Permission p) {
        return permissions.contains(p);
    }

}