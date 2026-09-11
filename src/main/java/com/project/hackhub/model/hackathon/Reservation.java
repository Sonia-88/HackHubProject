/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon;
 

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Entità JPA che gestisce le prenotazioni spaziali (Location) e temporali (TimeInterval).
 */
@Getter
@Entity
@NoArgsConstructor
public class Reservation {

    @Embedded
    private Location location;

    @Embedded
    private TimeInterval timeInterval;

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Costruisce una prenotazione verificando che location e intervallo temporale non siano nulli.
     *
     * @param location la località della prenotazione
     * @param timeInterval l'intervallo temporale della prenotazione
     * @throws IllegalArgumentException se location o timeInterval sono nulli
     */
    public Reservation(Location location, TimeInterval timeInterval){
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null.");

        if (timeInterval == null)
            throw new IllegalArgumentException("Time interval cannot be null.");

        this.location = location;
        this.timeInterval = timeInterval;
    }

    /**
     * Verifies if this reservation overlaps with another reservation.
     *
     * @param other the reservation to check overlap with; cannot be null
     * @return true if the time intervals overlap, false otherwise
     * @throws IllegalArgumentException if the reservation to compare is null
     * @throws IllegalStateException if one of the time intervals is not set
     *
     * @author Sonia Bevilacqua
     */
    public boolean overlapsWith(Reservation other) {
        if (other == null)
            throw new IllegalArgumentException("Reservation cannot be null.");

        if (this.timeInterval == null || other.timeInterval == null)
            throw new IllegalStateException("Time interval not set.");

        return this.timeInterval.overlapsWith(other.timeInterval);
    }

    /**
     * Checks if this reservation has the same {@link Location} as another reservation.
     *
     * @param other the reservation to compare; cannot be null
     * @return true if the two reservations share the same location, false otherwise
     * @throws IllegalArgumentException if the reservation to compare is null
     * @throws IllegalStateException if the location of one of the reservations is not set
     *
     * @author Sonia Bevilacqua
     */
    public boolean isSameLocation(Reservation other) {
        if (other == null)
            throw new IllegalArgumentException("Reservation cannot be null.");

        if (this.location == null || other.location == null)
            throw new IllegalStateException("Location not set.");

        return this.location.equals(other.location);
    }

}