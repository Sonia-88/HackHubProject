/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.repository;
 

import com.project.hackhub.model.hackathon.Location;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.TimeInterval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione delle prenotazioni di luoghi e orari.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * Verifica l'esistenza di una prenotazione per una specifica location e un intervallo di tempo.
     *
     * @param location la località della prenotazione
     * @param timeInterval l'intervallo temporale
     * @return true se la prenotazione esiste, false altrimenti
     */
    boolean existsByLocationAndTimeInterval(Location location, TimeInterval timeInterval);
}