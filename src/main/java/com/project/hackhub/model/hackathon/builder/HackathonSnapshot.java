/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.builder;
 

import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entità JPA che memorizza uno snapshot leggero dello stato di configurazione
 * di un hackathon in fase di creazione (bozza).
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HackathonSnapshot {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(unique = true)
    private User author;

    private String name;

    @Column(length = 5000)
    private String ruleBook;

    private LocalDate expiredSubscriptionsDate;

    private Integer maxTeamDimension;

    @Embedded
    private Money moneyPrice;

    @OneToOne(cascade = CascadeType.ALL)
    private Reservation reservation;

    @ElementCollection
    private List<UUID> mentorsList = new ArrayList<>();

    private UUID judge;
}