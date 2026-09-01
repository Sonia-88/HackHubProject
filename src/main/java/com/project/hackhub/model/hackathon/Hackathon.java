/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon;
 

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.hackhub.model.hackathon.state.HackathonState;
import com.project.hackhub.model.hackathon.state.HackathonStateFactory;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.AidRequest;
import com.project.hackhub.model.team.Infraction;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

/**
 * Rappresenta l'entità principale dell'Hackathon, gestendone lo stato,
 * i team partecipanti, lo staff (mentor, giudice, coordinatore), i premi e le richieste di supporto.
 */
@Setter @Getter
@NoArgsConstructor
@Entity
public class Hackathon {

    @Id
    @GeneratedValue @Column(name = "hackathon_id")
    private UUID id;

    @Column(name = "HackathonName")
    private String name;

    private String ruleBook;
    private LocalDate expiredSubscriptionsDate;
    private Integer maxTeamDimension;

    @Enumerated(EnumType.STRING)
    private HackathonStateType stateType;

    @Transient
    private final HackathonStateFactory factory = new HackathonStateFactory();

    @JsonManagedReference
    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teamsList = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "hackathon_mentors")
    private List<User> mentorsList = new ArrayList<>();

    @Embedded
    private Money moneyPrize;

    @ManyToOne
    private User judge;

    @ManyToOne
    private User coordinator;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private Reservation reservation;

    @ElementCollection
    private List<Infraction> infractions = new ArrayList<>();

    @ElementCollection
    private List<AidRequest> aidRequests = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskList = new ArrayList<>();

    @OneToOne
    private Team winner;

    /**
     * Aggiunge un nuovo mentor alla lista dei mentor dell'hackathon.
     *
     * @param u l'utente mentor da aggiungere
     * @throws IllegalArgumentException se l'utente è nullo
     * @throws IllegalStateException se il mentor è già presente
     */
    public void addMentor(User u) {
        if (u == null)
            throw new IllegalArgumentException("Mentor cannot be null.");

        if (mentorsList.contains(u))
            throw new IllegalStateException("Mentor already present.");

        mentorsList.add(u);
    }

    /**
     * Restituisce l'oggetto di stato corrente dell'hackathon basato sul tipo.
     *
     * @return lo stato corrente dell'hackathon, oppure null se non impostato
     */
    public HackathonState getState() {
        if (stateType == null) {
            return null;
        }
        return factory.createState(stateType);
    }

    /**
     * Rimuove un mentor dalla lista dei mentor.
     * @param u utente registrato
     * @author Sonia Bevilacqua
     */
    public void removeMentor(User u) {
        if (u == null)
            throw new IllegalArgumentException("Mentor cannot be null.");
        if (!mentorsList.contains(u))
            throw new IllegalStateException("Mentor not present in the list.");
        mentorsList.remove(u);
    }

    /**
     * Rimuove un team dalla lista dei team partecipanti.
     *
     * @param t il team da rimuovere
     * @throws IllegalArgumentException se il team è nullo
     */
    public void removeTeam(Team t) {
        if (t == null)
            throw new IllegalArgumentException("Team cannot be null.");

        teamsList.remove(t);
    }

    /**
     * Aggiunge una richiesta di supporto alla lista delle richieste.
     *
     * @param a la richiesta di aiuto da aggiungere
     */
    public void addAidRequest(@NonNull AidRequest a) {

        aidRequests.add(a);
    }

    /**
     * Restituisce una mappa contenente i voti di tutti i team associati al loro nome.
     *
     * @return una mappa con nome del team e relativo voto
     */
    public Map<String, Float> getTeamsGrades() {
        Map<String, Float> grades = new HashMap<>();
        for(Team t : this.teamsList) {
            grades.put(t.getName(), t.getGrade());
        }
        return grades;
    }

    /**
     * Aggiunge un task all'hackathon.
     *
     * @param t il task da aggiungere
     */
    public void addTask(@NonNull Task t){
        taskList.add(t);
    }

    /**
     * Rimuove le infrazioni associate a uno specifico team.
     *
     * @param t il team di cui rimuovere le infrazioni
     * @throws IllegalArgumentException se il team è nullo
     */
    public void removeInfractionByTeam(Team t) {

        if(t == null)
            throw new IllegalArgumentException("Team cannot be null");

        this.infractions.removeIf(i -> i.getITeam().equals(t));
    }

    /**
     * Aggiunge una nuova infrazione alla lista.
     *
     * @param i l'infrazione da aggiungere
     * @throws IllegalArgumentException se l'infrazione è nulla
     */
    public void addInfraction(Infraction i) {
        if(i == null)
            throw new IllegalArgumentException("Infraction to remove cannot be null");

        this.infractions.add(i);
    }

    /**
     * Rimuove le richieste di supporto associate a uno specifico team.
     *
     * @param t il team di cui rimuovere le richieste
     */
    public void removeAidRequestByTeam(@NonNull Team t) {
        aidRequests.removeIf(a -> a.getTeam().equals(t));
    }
}