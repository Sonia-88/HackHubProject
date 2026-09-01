/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.service;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.UserStateType;
import com.project.hackhub.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servizio per la gestione dello stato degli utenti all'interno dei vari hackathon e la gestione degli inviti.
 */
@Service
@AllArgsConstructor
public final class UserStateService {

    private final UserRepository userRepository;

    /**
     * Sets the given state on the given user and adds or removes the reservation of the Hackathon as needed
     *
     * @param user      the user which state is to update.
     * @param toAdd     {@code true} if the reservation needs to be added to the list of the user, {@code false} if it does not
     * @param hackathon the hackathon
     * @param state     the userState to set
     * @throws IllegalArgumentException if any of the parameters are null
     * @author Sonia Bevilacqua
     */
    public void changeUserState(User user, boolean toAdd, Hackathon hackathon, UserStateType state) {

        if (user == null) throw new IllegalArgumentException("user cannot be null");
        if (hackathon == null) throw new IllegalArgumentException("hackathon cannot be null");
        if (state == null) throw new IllegalArgumentException("state cannot be null");

        if (toAdd)
            user.setState(hackathon.getReservation(), state);
        else {
            user.removeReservation(hackathon.getReservation());
        }
        userRepository.save(user);
    }

    /**
     * Aggiunge un invito all'utente e salva le modifiche nel repository.
     *
     * @param user l'utente destinatario
     * @param invitation l'invito da aggiungere
     */
    public void addInvitation(User user, Invitation invitation) {

        user.addInvitation(invitation);
        userRepository.save(user);

    }

    /**
     * Retrieves the state of the given user for a specific Hackathon.
     *
     * @param user      the user
     * @param hackathon the hackathon
     * @return the UserStateType, or null/DEFAULT_STATE if not registered
     */

    public UserStateType getUserStateInHackathon(User user, Hackathon hackathon) {
        if (user == null) throw new IllegalArgumentException("user cannot be null");
        if (hackathon == null) throw new IllegalArgumentException("hackathon cannot be null");

        return user.getRawStateType(hackathon);
    }

}