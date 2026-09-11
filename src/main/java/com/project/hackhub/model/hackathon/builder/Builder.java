/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.builder;
 

import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.user.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Interfaccia che definisce i passaggi e i metodi necessari per la costruzione
 * di un oggetto Hackathon complessa seguendo il pattern Builder.
 */
public interface Builder {

    /** Resetta il builder per avviare una nuova costruzione. */
    void reset();
    /** Imposta il nome dell'hackathon. */
    void setName(String n);
    /** Imposta il regolamento dell'hackathon. */
    void setRuleBook(String r);
    /** Imposta lo stato iniziale dell'hackathon. */
    void setState();
    /** Imposta la dimensione massima dei team. */
    void setMaxTeamDimension(Integer num);
    /** Imposta la prenotazione (sede e date). */
    void setReservation(Reservation p);
    /** Imposta il premio in denaro. */
    void setMoneyPrice(Money p);
    /** Aggiunge la lista dei mentor all'hackathon. */
    void addMentorsList(List<User> mentorsList);
    /** Imposta la data di scadenza delle iscrizioni. */
    void setExpiredSubscriptionDate(LocalDate d);
    /** Imposta il giudice dell'hackathon. */
    void setJudge(User u);
    /** Imposta il coordinatore dell'hackathon. */
    void setCoordinator(User coordinator);
}