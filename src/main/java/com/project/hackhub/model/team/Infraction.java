/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.team;
 

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.project.hackhub.model.hackathon.Hackathon;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rappresenta un'infrazione o violazione del regolamento commessa da un team.
 */
@Embeddable @NoArgsConstructor
@Getter
public class Infraction {

    @JsonIncludeProperties({"name", "id"})
    @ManyToOne
    @Getter
    private Team iTeam;
    private String iDescription;
    private String iType;
    private LocalDateTime timestamp;

    /**
     * Costruisce un'infrazione registrando il team, la descrizione, il tipo e la data/ora corrente.
     *
     * @param team il team coinvolto
     * @param description la descrizione dell'infrazione
     * @param type la tipologia dell'infrazione
     */
    public Infraction(Team team, String description, String type){
        this.iTeam = team;
        this.iDescription = description;
        this.iType = type;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Restituisce l'hackathon in cui è avvenuta l'infrazione.
     *
     * @return l'oggetto {@link Hackathon}
     */
    @JsonIgnore
    public Hackathon getHackathon() {
        return this.iTeam.getHackathon();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Infraction that)) return false;
        return Objects.equals(iTeam, that.iTeam) && Objects.equals(iDescription, that.iDescription) && Objects.equals(iType, that.iType) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iTeam, iDescription, iType, timestamp);
    }
}