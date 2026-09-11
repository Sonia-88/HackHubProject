/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.repository;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.team.Submission;
import com.project.hackhub.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione delle sottomissioni dei progetti.
 */
@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {

    /**
     * Recupera l'ultima sottomissione (in ordine di timestamp) effettuata da ciascun team per un dato hackathon.
     *
     * @param hackathon l'hackathon di riferimento
     * @return una lista delle sottomissioni più recenti per team
     */
    @Query("SELECT s FROM Submission s WHERE s.hackathon = :hackathon " +
            "AND s.timestamp = (SELECT MAX(sub2.timestamp) FROM Submission sub2 " +
            "WHERE sub2.hackathon = :hackathon AND sub2.team = s.team)")
    List<Submission> findLatestSubmissionsByHackathon(@Param("hackathon") Hackathon hackathon);

    /**
     * Elimina tutte le sottomissioni effettuate da uno specifico team.
     *
     * @param team il team di cui eliminare le sottomissioni
     */
    void deleteByTeam(Team team);
}