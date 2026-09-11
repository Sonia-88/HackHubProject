/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon;
 

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Oggetto incorporabile (Embeddable) che rappresenta un importo monetario con relativa valuta.
 */
@Embeddable
@NoArgsConstructor
@Getter
public final class Money {

    /** La quantità numerica del denaro. */
    private double quantity;
    /** Il codice della valuta (es. EUR). */
    private String currency;

    /**
     * Costruisce un oggetto Money validando che la quantità non sia negativa e la valuta non sia vuota.
     *
     * @param quantity la quantità di denaro
     * @param currency il codice della valuta
     * @throws IllegalArgumentException se la quantità è nulla/negativa o la valuta è vuota
     */
    public Money(Double quantity, String currency) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-null and >= 0");
        }
        if (currency == null || currency.isEmpty()) {
            throw new IllegalArgumentException("Currency code must be non-null and non-empty");
        }
        this.quantity = quantity;
        this.currency = currency;
    }
}