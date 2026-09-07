/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.service.calendar;
 

import com.project.hackhub.model.hackathon.Hackathon;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

/**
 * Un servizio adattatore simulato che verrebbe utilizzato per interagire con un sistema di calendario esterno.
 */
@Component
public class CalendarAdapter {

    /**
     * Restituisce gli slot disponibili per l'hackathon specificato.
     *
     * @param hackathon l'hackathon di riferimento
     * @return una lista di slot disponibili
     */
    public List<Slot> getAvailableSlots(Hackathon hackathon) {
        return simulateExternalCalendar(hackathon);
    }

    /**
     * Questo metodo simula un'interazione con un sistema di calendario esterno
     * restituendo un elenco di slot fittizi (mock) per il dato hackathon. In quanto tale, deve
     * essere sostituito con la logica effettiva per interagire con il sistema di calendario esterno in un'implementazione reale.
     * @param hackathon l'hackathon per cui recuperare gli slot disponibili per la proposta di chiamata
     * @return una lista di slot disponibili per la proposta di chiamata per il dato hackathon
     */
    private List<Slot> simulateExternalCalendar(Hackathon hackathon) {
        List<Slot> mockSlots = new ArrayList<>();
        mockSlots.add(
                new Slot(LocalDateTime.of(2026, Month.MAY,15,10,30,0),
                        LocalDateTime.of(2026, Month.MAY,15,10,45,0)));
        mockSlots.add(
                new Slot(LocalDateTime.of(2026, Month.MAY,15,10,00,0),
                        LocalDateTime.of(2026, Month.MAY,15,10,15,0)));
        mockSlots.add(
                new Slot(LocalDateTime.of(2026, Month.MAY,15,11,15,0),
                        LocalDateTime.of(2026, Month.MAY,15,11,30,0)));
        return mockSlots;
    }

    /**
     * Rimuove uno slot dal calendario (simulato).
     *
     * @param hackathon l'hackathon di riferimento
     * @param slot lo slot da rimuovere
     * @return true se l'operazione ha successo
     */
    public boolean removeSlot(Hackathon hackathon, Slot slot){
        return true;
    }
}