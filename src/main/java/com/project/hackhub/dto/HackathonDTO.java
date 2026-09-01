/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.dto;


import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Oggetto di trasferimento dati (DTO) contenente tutte le informazioni necessarie per la creazione o la modifica di un hackathon.
 */
public record HackathonDTO(

        /** Il nome dell'hackathon. */
        String name,
        /** Il regolamento o libro delle regole dell'evento. */
        String ruleBook,
        /** La data di scadenza delle iscrizioni (deve essere nel presente o nel futuro). */
        @FutureOrPresent(message = "La data di scadenza delle iscrizioni non può essere nel passato")
        LocalDate expiredSubscriptionsDate,
        /** La dimensione massima consentita per ciascun team. */
        Integer maxTeamDimension,
        /** La lista degli identificativi UUID dei mentori assegnati all'hackathon. */
        List<UUID> mentorsList,
        /** Il premio in denaro in palio. */
        Money moneyPrize,
        /** L'identificativo UUID del giudice assegnato all'evento. */
        UUID judge,
        /** I dettagli della prenotazione (luogo e intervallo temporale) dell'hackathon. */
        Reservation reservation
) {}