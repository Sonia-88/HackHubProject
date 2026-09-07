/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.hackathon.builder;


import com.project.hackhub.dto.HackathonDTO;
import com.project.hackhub.exceptions.UserNotAvailableException;
import com.project.hackhub.handler.HackathonCreationHandler;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.UserRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

/**
 * Classe Director che orchestra l'utilizzo di un {@link Builder}.
 * @author Cosmina Androne
 *
 */
@AllArgsConstructor
public class Director {

    private final Builder builder;
    private final UserRepository userRepository;
    private final HackathonCreationHandler hackathonCreationHandler;

    /**
     * Popola il builder utilizzando i dati forniti nel DTO e verificando le disponibilità.
     *
     * @param dto il DTO con le informazioni dell'hackathon
     * @param p la prenotazione associata
     */
    public void populateBuilder(HackathonDTO dto, Reservation p) {

        if (dto == null) {
            throw new IllegalArgumentException("dto cannot be null");
        }

        setBasicInfo(dto);
        setReservation(dto);
        setJudge(dto, p);
        setMentors(dto, p);
        setAdditionalInfo(dto);
    }

    /** Imposta le informazioni di base (nome e regolamento). */
    private void setBasicInfo(HackathonDTO dto) {
        if (dto.name() != null)
            builder.setName(dto.name());

        if (dto.ruleBook() != null)
            builder.setRuleBook(dto.ruleBook());
    }

    /** Imposta la prenotazione se disponibile. */
    private void setReservation(HackathonDTO dto) {
        if (dto.reservation() != null && hackathonCreationHandler.isReservationAvailable(dto.reservation()))
            builder.setReservation(dto.reservation());
    }

    /** Imposta il giudice verificandone la disponibilità. */
    private void setJudge(HackathonDTO dto, Reservation p) {
        if (dto.judge() == null) return;
        User j = userRepository.findById(dto.judge())
                .orElseThrow(() -> new IllegalArgumentException("Judge not found"));

        if(p != null) {
            if (j.isAvailable(p))
                builder.setJudge(j);
            else
                throw new UserNotAvailableException("Judge is not available for the given reservation");
        }
    }

    /** Imposta la lista dei mentor filtrandoli per disponibilità. */
    private void setMentors(HackathonDTO dto, Reservation p) {

        if (dto.mentorsList() == null) return;

        if(p != null) {
            List<User> mentors = dto.mentorsList().stream()
                    .map(userRepository::findById)
                    .map(opt -> opt.filter(u -> u.isAvailable(p)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();


            if (!mentors.isEmpty()) {
                builder.addMentorsList(mentors);
            }
        }
    }

    /** Imposta le informazioni aggiuntive (scadenza iscrizioni, premio, dimensione team). */
    private void setAdditionalInfo(HackathonDTO dto) {

        if (dto.expiredSubscriptionsDate() != null)
            builder.setExpiredSubscriptionDate(dto.expiredSubscriptionsDate());

        if (dto.moneyPrize() != null)
            builder.setMoneyPrice(dto.moneyPrize());

        if (dto.maxTeamDimension() != null)
            builder.setMaxTeamDimension(dto.maxTeamDimension());
    }
}