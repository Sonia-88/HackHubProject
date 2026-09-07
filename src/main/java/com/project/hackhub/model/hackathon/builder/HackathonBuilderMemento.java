/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.hackathon.builder;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.user.User;
import lombok.Getter;

import java.util.ArrayList;

/**
 * Implementazione concreta del pattern Memento per {@link HackathonBuilder}.

 * Questa classe cattura e memorizza lo stato di un {@link HackathonBuilder} in un momento specifico nel tempo
 * incapsulando un {@link HackathonSnapshot}. Lo snapshot memorizza solo i dati essenziali (stringhe, date, UUID)
 * senza mantenere riferimenti completi alle entità, consentendo una persistenza leggera.
 */
public class HackathonBuilderMemento implements Memento {

    @Getter
    private final HackathonSnapshot snapshot;

    /**
     * Costruisce un nuovo HackathonBuilderMemento incapsulando lo snapshot fornito.
     *
     * @param snapshot lo snapshot contenente lo stato salvato; non deve essere nullo
     */
    public HackathonBuilderMemento(HackathonSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    /**
     * Metodo di fabbrica (factory method) che crea un HackathonBuilderMemento dallo stato corrente del builder.
     * Estrae tutti i dati rilevanti dall'oggetto {@link Hackathon} interno e li memorizza in un nuovo {@link HackathonSnapshot}.
     *
     * @param builder il builder contenente lo stato dell'hackathon da catturare; non deve essere nullo
     * @param author l'utente che crea questo memento; non deve essere nullo
     * @return un nuovo HackathonBuilderMemento che incapsula lo snapshot creato
     */
    public static HackathonBuilderMemento fromBuilder(
            HackathonBuilder builder,
            User author
    ) {

        Hackathon hackathon = builder.getProduct();

        HackathonSnapshot snapshot = new HackathonSnapshot();

        snapshot.setAuthor(author);

        snapshot.setName(hackathon.getName());
        snapshot.setRuleBook(hackathon.getRuleBook());
        snapshot.setExpiredSubscriptionsDate(hackathon.getExpiredSubscriptionsDate());
        snapshot.setMaxTeamDimension(hackathon.getMaxTeamDimension());
        snapshot.setMoneyPrice(hackathon.getMoneyPrize());
        snapshot.setReservation(hackathon.getReservation());

        if (hackathon.getMentorsList() != null) {
            snapshot.setMentorsList(
                    new ArrayList<>(
                            hackathon.getMentorsList()
                                    .stream()
                                    .map(User::getId)
                                    .toList()
                    )
            );
        } else {
            snapshot.setMentorsList(new ArrayList<>());
        }
        if (hackathon.getJudge() != null) {
            snapshot.setJudge(hackathon.getJudge().getId());
        }
        return new HackathonBuilderMemento(snapshot);
    }

    /**
     * Ripristina lo stato dello snapshot all'interno del builder fornito.
     *
     * @param builder il builder il cui stato interno verrà popolato dallo snapshot; non deve essere nullo
     */
    public void restoreInto(HackathonBuilder builder) {

        Hackathon hackathon = builder.getProduct();

        if (hackathon == null) {
            builder.reset();
            hackathon = builder.getProduct();
        }

        if (snapshot.getName() != null)
            hackathon.setName(snapshot.getName());

        if (snapshot.getRuleBook() != null)
            hackathon.setRuleBook(snapshot.getRuleBook());

        if (snapshot.getExpiredSubscriptionsDate() != null)
            hackathon.setExpiredSubscriptionsDate(snapshot.getExpiredSubscriptionsDate());

        if (snapshot.getMaxTeamDimension() != null)
            hackathon.setMaxTeamDimension(snapshot.getMaxTeamDimension());

        if (snapshot.getMoneyPrice() != null)
            hackathon.setMoneyPrize(snapshot.getMoneyPrice());

        if (snapshot.getReservation() != null)
            hackathon.setReservation(snapshot.getReservation());
    }

    /**
     * Restituisce lo snapshot che rappresenta lo stato salvato di questo memento.
     *
     * @return il {@link HackathonSnapshot} contenente lo stato del memento
     */
    @Override
    public HackathonSnapshot getState() {
        return snapshot;
    }
}