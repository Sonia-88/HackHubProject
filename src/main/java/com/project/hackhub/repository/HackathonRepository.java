/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.repository;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.team.Infraction;
import com.project.hackhub.model.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione e il recupero delle entità {@link Hackathon}.
 */
@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, UUID> {

    /**
     * Recupera gli hackathon la cui data di scadenza delle iscrizioni è superata
     * e si trovano ancora in fase di iscrizione.
     *
     * @param now la data e ora corrente
     * @return una lista di hackathon con iscrizioni scadute
     */
    @Query("SELECT h FROM Hackathon h WHERE h.expiredSubscriptionsDate < :now\n AND h.stateType = HackathonStateType.SUBSCRIPTION_PHASE")
    List<Hackathon> getExpiredSubscriptions(@Param("now") LocalDateTime now);

    /**
     * Recupera gli hackathon la cui data di fine prenotazione è superata
     * e si trovano ancora nello stato ONGOING.
     *
     * @param now la data e ora corrente
     * @return una lista di hackathon con sottomissioni scadute
     */
    @Query("SELECT h FROM Hackathon h WHERE h.reservation.timeInterval.endDate < :now\n AND h.stateType = HackathonStateType.ONGOING")
    List<Hackathon> getExpiredSubmissions(LocalDateTime now);

    /**
     * Trova le infrazioni associate a uno specifico team all'interno di un dato hackathon.
     *
     * @param h l'hackathon di riferimento
     * @param t il team coinvolto
     * @return un Optional contenente la lista delle infrazioni
     */
    @Query(" SELECT i FROM Hackathon h JOIN h.infractions i WHERE h = :hackathon AND i.iTeam = :team")
    Optional <List<Infraction>> findInfractionByTeam(@Param("hackathon") Hackathon h,
                                                     @Param("team") Team t);

    /**
     * Trova un team all'interno degli hackathon tramite il suo identificativo UUID.
     *
     * @param team l'ID del team da cercare
     * @return un Optional contenente il team trovato
     */
    @Query("SELECT t FROM Hackathon h JOIN h.teamsList t WHERE t.id = :team")
    Optional<Team> findByTeamId(@Param("team")UUID team);


}