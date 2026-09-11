/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon;
 

import com.project.hackhub.model.team.FileTemplate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entità JPA che rappresenta un task o consegna assegnata all'interno dell'hackathon.
 */
@Entity @NoArgsConstructor
@Setter
@Getter
public class Task {
    @Id @GeneratedValue
    private UUID id;
    /** La descrizione dettagliata del task. */
    private String description;
    /** Il titolo del task. */
    private String title;

    /**
     * Costruisce un nuovo task con titolo, descrizione e template di file associato.
     *
     * @param title il titolo del task
     * @param description la descrizione del task
     * @param f il template di file di riferimento
     */
    public Task(String title, String description, FileTemplate f){
        this.title = title;
        this.description = description;
    }
}