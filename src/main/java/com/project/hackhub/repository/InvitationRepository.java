/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.repository;
 

import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione degli inviti ai team.
 */
@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    /**
     * Verifica se esiste un invito in sospeso (pending) inviato da un determinato team a un utente.
     *
     * @param sender il team mittente
     * @param addressee l'utente destinatario
     * @return true se esiste un invito pendente, false altrimenti
     */
    boolean existsBySenderAndAddresseeAndPendingTrue(Team sender, User addressee);
}