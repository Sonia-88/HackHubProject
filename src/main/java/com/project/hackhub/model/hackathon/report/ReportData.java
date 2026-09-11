/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.report;
 

import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.state.HackathonState;
import com.project.hackhub.model.team.AidRequest;
import com.project.hackhub.model.team.Infraction;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Modello di dati aggregato intermedio utilizzato per costruire le diverse
 * classi finali di Report.
 *
 * <p>Questa classe raccoglie tutte le informazioni di dominio richieste dai builder di report
 * prima di trasformarle in una rappresentazione specifica (es. vista pubblica, dettagliata o per lo staff).</p>
 *
 * <p>Non viene esposta direttamente ai client ed è intesa esclusivamente come
 * contenitore di dati interno tra il livello di dominio e i DTO dei report.</p>
 */
@Getter @Setter
public class ReportData {
    private String name;
    private String ruleBook;
    private LocalDate expiredSubscriptionsDate;
    private int maxTeamDimension;
    private HackathonState state;
    private List<Team> teamsList;
    private List<User> mentorsList;
    private Money moneyPrice;
    private User judge;
    private User coordinator;
    private Reservation reservation;
    private Map<String, Float> teamsGrades;
    private List<AidRequest> aidRequests;
    private List<Infraction> infractions;
    private Team winner;
}