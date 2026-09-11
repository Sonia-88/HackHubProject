/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.repository;
 

import com.project.hackhub.model.hackathon.builder.HackathonSnapshot;
import com.project.hackhub.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione delle bozze e degli snapshot degli hackathon.
 */
@Repository
public interface HackathonSnapshotRepository extends JpaRepository<HackathonSnapshot, UUID> {

    /**
     * Trova lo snapshot associato a un determinato coordinatore/autore.
     *
     * @param coordinator l'utente coordinatore creatore della bozza
     * @return un Optional contenente lo snapshot dell'hackathon
     */
    Optional<HackathonSnapshot> findByAuthor(User coordinator);
}