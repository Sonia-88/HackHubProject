/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.service.calendar;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Oggetto incorporabile (Embeddable) che rappresenta uno slot temporale (intervallo di tempo)
 * caratterizzato da data/ora di inizio e di fine.
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor
public class Slot {
    @Column(name = "start_time")
    private LocalDateTime start;
    @Column(name = "end_time")
    private LocalDateTime end;

    /**
     * Costruisce un nuovo slot temporale con inizio e fine specificati.
     *
     * @param start la data e ora di inizio
     * @param end la data e ora di fine
     */
    public Slot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Slot slot = (Slot) o;
        return Objects.equals(start, slot.start) && Objects.equals(end, slot.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}