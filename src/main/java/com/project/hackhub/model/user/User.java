/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.user;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.user.state.UserStateFactory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

/**
 * Entità JPA che rappresenta un utente registrato nel sistema HackHub,
 * gestendone le credenziali, gli inviti ricevuti e gli stati associati ai vari hackathon.
 */
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id @GeneratedValue
    @Getter private UUID id;

    @OneToMany(mappedBy = "addressee")
    @Getter private Set<Invitation> invitationsList = new LinkedHashSet<>();

    @Embedded
    @Setter @Getter @NonNull private PersonalData personalData;

    @Getter private String passwordHash;

    @Getter @Setter private boolean organizer = false;

    @Transient
    private final UserStateFactory factory = new UserStateFactory();

    @ElementCollection
    @CollectionTable(name = "state_in_hackathon",
            joinColumns = @JoinColumn(name = "utente_registrato_id"))
    @MapKeyJoinColumn(name = "id")
    private Map<Reservation, UserStateType> stateInHackathon = new HashMap<>();

    /**
     * Costruisce un nuovo utente con i dati anagrafici e l'hash della password specificati.
     *
     * @param a i dati personali dell'utente
     * @param passwordHash la password crittografata dell'utente
     */
    public User(@NonNull PersonalData a, String passwordHash){
        this.personalData = a;
        this.passwordHash = passwordHash;
    }

    /**
     * Restituisce lo stato dell'utente in relazione a un determinato hackathon.
     *
     * @param hackathon l'hackathon di riferimento
     * @return l'oggetto {@link UserState} corrente
     */
    public UserState getState(Hackathon hackathon){
        if (hackathon == null) return new DefaultState();
        UserStateType type = stateInHackathon.get(hackathon.getReservation());
        if (type == null) return new DefaultState();
        return factory.createUserState(type);
    }

    /**
     * Imposta o aggiorna lo stato dell'utente per una specifica prenotazione/hackathon.
     *
     * @param reservation la prenotazione associata
     * @param newStateType il nuovo tipo di stato da impostare
     */
    public void setState(Reservation reservation, UserStateType newStateType){

        if(stateInHackathon.containsKey(reservation))
            if(stateInHackathon.get(reservation).equals(newStateType))
                return;

        this.stateInHackathon.put(reservation, newStateType);
    }

    /**
     * Rimuove la prenotazione e il relativo stato associato dell'utente.
     *
     * @param p la prenotazione da rimuovere
     * @throws IllegalArgumentException se la prenotazione è nulla
     */
    public void removeReservation(Reservation p){

        if(p == null)
            throw new IllegalArgumentException("Reservation can't be null");
        stateInHackathon.remove(p);
    }

    /**
     * Verifica se l'utente è disponibile in un dato intervallo di tempo/prenotazione,
     * controllando che non vi siano sovrapposizioni con altri impegni.
     *
     * @param p la prenotazione da verificare
     * @return true se l'utente è disponibile, false altrimenti
     * @throws IllegalArgumentException se la prenotazione è nulla
     */
    public boolean isAvailable(Reservation p){
        if(p == null)
            throw new IllegalArgumentException("Reservation can't be null");
        for(Reservation reservation : stateInHackathon.keySet()){
            if(reservation.overlapsWith(p))
                return false;}
        return true;
    }

    /**
     * Aggiunge un invito alla lista degli inviti ricevuti dall'utente.
     *
     * @param i l'invito da aggiungere
     * @throws IllegalArgumentException se l'invito è nullo o già presente
     */
    public void addInvitation(Invitation i){
        if (i == null)
            throw new IllegalArgumentException("Can't add empty invitation");
        if(this.invitationsList.contains(i))
            throw new IllegalArgumentException("Can't duplicate invitation");
        this.invitationsList.add(i);
    }

    /**
     * Rimuove un invito dalla lista degli inviti ricevuti.
     *
     * @param i l'invito da rimuovere
     * @throws IllegalArgumentException se l'invito è nullo
     * @throws NoSuchElementException se l'invito non è presente nella lista
     */
    public void removeInvitation(Invitation i){
        if(i == null)
            throw new IllegalArgumentException("Invitation can't be null");
        if(!invitationsList.contains(i))
            throw new NoSuchElementException("Invitation not contained in invitationList");
        invitationsList.remove(i);
    }

    /**
     * Verifica se l'utente possiede un determinato permesso nel contesto di un hackathon.
     *
     * @param p il permesso da verificare
     * @param h l'hackathon di riferimento
     * @return true se l'utente possiede il permesso, false altrimenti
     */
    public boolean hasPermission(Permission p, Hackathon h){
        return this.getState(h).hasPermission(p);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /**
     * Restituisce il tipo di stato grezzo (UserStateType) dell'utente per un dato hackathon.
     *
     * @param hackathon di riferimento
     * @return il tipo di stato, oppure DEFAULT_STATE se non associato
     */
    public UserStateType getRawStateType(Hackathon hackathon){
        if (hackathon == null) return UserStateType.DEFAULT_STATE;

        UserStateType type = stateInHackathon.get(hackathon.getReservation());

        return type != null ? type : UserStateType.DEFAULT_STATE;
    }

}