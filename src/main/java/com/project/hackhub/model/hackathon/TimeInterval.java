/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.hackathon;


import jakarta.persistence.Embeddable;

import java.time.LocalDate;

/**
 * Record incorporabile (Embeddable) che definisce un intervallo temporale tramite data di inizio e di fine.
 */
@Embeddable
public record TimeInterval(
        /** La data di inizio dell'intervallo. */
        LocalDate startDate,
        /** La data di fine dell'intervallo. */
        LocalDate endDate
) {

    /**
     * Costruttore compattato per validare che le date non siano nulle
     * e che la data di fine non preceda quella di inizio.
     *
     * @throws IllegalArgumentException se le date sono nulle o l'end è prima dello start
     */
    public TimeInterval {
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("Le date non possono essere nulle");

        if (endDate.isBefore(startDate))
            throw new IllegalArgumentException("La data di fine non può precedere la data di inizio");
    }

    /**
     * Verifica se questo intervallo temporale si sovrappone a un altro intervallo.
     *
     * @param other l'altro intervallo temporale da confrontare
     * @return true se c'è sovrapposizione, false altrimenti
     * @throws IllegalArgumentException se l'intervallo da confrontare è nullo
     */
    public boolean overlapsWith(TimeInterval other) {
        if (other == null)
            throw new IllegalArgumentException("Interval cannot be null.");

        // overlap se:
        // start <= other.end AND other.start <= end
        return !this.endDate.isBefore(other.startDate)
                && !other.endDate.isBefore(this.startDate);
    }
}