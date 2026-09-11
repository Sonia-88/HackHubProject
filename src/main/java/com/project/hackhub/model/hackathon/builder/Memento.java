/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.builder;
 

/**
 * Interfaccia che rappresenta il pattern Memento per l'accesso allo stato salvato.
 */
public interface Memento {

    /**
     * Restituisce lo snapshot contenente lo stato memorizzato.
     *
     * @return l'oggetto {@link HackathonSnapshot}
     */
    HackathonSnapshot getState();

}