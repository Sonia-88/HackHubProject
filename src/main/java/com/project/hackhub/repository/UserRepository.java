/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.repository;


import com.project.hackhub.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione degli utenti registrati nel sistema.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Verifica se esiste già un utente registrato con un determinato indirizzo email.
     *
     * @param email l'indirizzo email da verificare
     * @return true se l'email esiste già, false altrimenti
     */
    boolean existsByPersonalData_Email(String email);

    /**
     * Cerca un utente in base al suo username anagrafico.
     *
     * @param userName lo username da cercare
     * @return un Optional contenente l'utente trovato
     */
    Optional<User> findByPersonalData_UserName(String userName);

    /**
     * Cerca un utente in base al suo indirizzo email anagrafico.
     *
     * @param email l'email da cercare
     * @return un Optional contenente l'utente trovato
     */
    Optional<User> findByPersonalData_Email(String email);
}