/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.builder;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.Reservation;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Classe concreta {@link Builder} utilizzata per creare istanze di {@link Hackathon}.
 * @author Sonia Bevilacqua
 */
public class HackathonBuilder implements Builder {

    @Setter @Getter
    private Hackathon hackathon;

    /**
     * Resetta il builder per creare una nuova istanza di Hackathon.
     */
    @Override
    public void reset() {
        hackathon = new Hackathon();
    }

    /**
     * Imposta il nome dell'hackathon se non è nullo o vuoto.
     * @param n il nome da impostare
     */
    @Override
    public void setName(String n) {

        if(n != null && !n.isBlank())
            hackathon.setName(n);

    }

    /**
     * Imposta il regolamento dell'hackathon se non è nullo o vuoto.
     * @param r il regolamento da impostare
     */
    @Override
    public void setRuleBook(String r) {

        if(r != null && !r.isBlank())
            hackathon.setRuleBook(r);
    }

    /**
     * Imposta lo stato dell'hackathon sullo stato predefinito 'SUBSCRIPTION_PHASE'.
     */
    @Override
    public void setState() {
        hackathon.setStateType(HackathonStateType.SUBSCRIPTION_PHASE);
    }

    /**
     * Imposta la dimensione massima del team se il numero è compreso tra 2 e 19.
     * @param num la dimensione massima del team
     */
    @Override
    public void setMaxTeamDimension(Integer num) {

        if(num != null && num > 1 && num < 20)
            hackathon.setMaxTeamDimension(num);
    }

    /**
     * Imposta la prenotazione per l'hackathon.
     * @param p la prenotazione
     */
    @Override
    public void setReservation(Reservation p) {
        hackathon.setReservation(p);
    }

    /**
     * Imposta il premio in denaro se l'oggetto Money è valido (non nullo e quantità >= 0).
     * @param p il premio in denaro
     */
    @Override
    public void setMoneyPrice(Money p) {
        if(p != null && p.getQuantity() >= 0)
            hackathon.setMoneyPrize(p);
    }

    /**
     * Aggiunge la lista dei mentor all'hackathon.
     * @param mentorsList la lista dei mentor
     */
    @Override
    public void addMentorsList(List<User> mentorsList) {
        if(mentorsList!=null)
            hackathon.setMentorsList(mentorsList);
    }

    /**
     * Imposta la data di scadenza delle iscrizioni.
     * @param d la data di scadenza delle iscrizioni
     */
    @Override
    public void setExpiredSubscriptionDate(LocalDate d) {
        if(d != null) {
            hackathon.setExpiredSubscriptionsDate(d);
        }
    }

    /**
     * Imposta il giudice per l'hackathon.
     * @param u il giudice
     */
    @Override
    public void setJudge(User u) {
        hackathon.setJudge(u);

    }

    /**
     * Imposta il coordinatore per l'hackathon.
     * @param coordinator il coordinatore
     */
    @Override
    public void setCoordinator(User coordinator) {
        hackathon.setCoordinator(coordinator);
    }

    /**
     * Restituisce l'istanza di Hackathon costruita.
     * @return l'istanza di Hackathon
     */
    public Hackathon getProduct() {
        return this.hackathon;
    }

    /**
     * Salva o aggiorna lo stato corrente del builder come memento.
     *
     * @param author l'autore del memento
     * @return un nuovo HackathonBuilderMemento
     */
    public HackathonBuilderMemento saveMemento(User author) {
        return HackathonBuilderMemento.fromBuilder(this, author);
    }

    /**
     * Ripristina lo stato del builder dal memento fornito.
     * @param memento il memento da cui ripristinare
     */
    public void restoreMemento(HackathonBuilderMemento memento) {
        memento.restoreInto(this);
    }

    /**
     * Ripristina lo stato del builder da uno snapshot, includendo gli utenti associati.
     * @param snapshot lo snapshot da cui ripristinare
     * @param userRepository repository per risolvere gli UUID degli utenti
     */
    public void restoreFromSnapshot(HackathonSnapshot snapshot, UserRepository userRepository) {
        HackathonBuilderMemento memento = new HackathonBuilderMemento(snapshot);
        restoreMemento(memento);

        if (snapshot.getMentorsList() != null && !snapshot.getMentorsList().isEmpty()) {
            List<User> mentors = snapshot.getMentorsList().stream()
                    .map(uuid -> userRepository.findById(uuid)
                            .orElseThrow(() -> new IllegalArgumentException("Mentor not found: " + uuid)))
                    .toList();
            addMentorsList(mentors);
        }

        if (snapshot.getJudge() != null) {
            User judge = userRepository.findById(snapshot.getJudge())
                    .orElseThrow(() -> new IllegalArgumentException("Judge not found: " + snapshot.getJudge()));
            setJudge(judge);
        }
    }

    /**
     * Verifica se la configurazione dell'hackathon è completa.
     * @return true se tutti i campi obbligatori sono presenti, false altrimenti
     */
    public boolean isComplete() {
        return hackathon.getName() != null
                && hackathon.getRuleBook() != null
                && hackathon.getExpiredSubscriptionsDate() != null
                && hackathon.getMaxTeamDimension() != null
                && hackathon.getMaxTeamDimension() != 0
                && hackathon.getMoneyPrize() != null
                && hackathon.getJudge() != null
                && hackathon.getReservation() != null
                && hackathon.getMentorsList() != null
                && !hackathon.getMentorsList().isEmpty();
    }
}