/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.dto.PersonalDataDTO;
import com.project.hackhub.model.user.PersonalData;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gestisce le operazioni relative agli account utente, inclusi la registrazione,
 * l'aggiornamento dei dati personali, la cancellazione e il recupero degli identificativi.
 */
@Component
@RequiredArgsConstructor
public class AccountHandler {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository utenteRepository;

    /**
     * Crea un nuovo account utente a partire dai dati anagrafici forniti.
     *
     * @param personalDataDto i dati personali del nuovo utente
     * @throws IllegalArgumentException se i dati non sono validi o l'email risulta già registrata
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void createAccount(PersonalDataDTO personalDataDto) {

        if (personalDataDto.email() == null || personalDataDto.email().isBlank()) {
            throw new IllegalArgumentException("Email richiesta");
        }
        if (personalDataDto.userName() == null || personalDataDto.userName().isBlank()) {
            throw new IllegalArgumentException("Name richiesto");
        }

        if (utenteRepository.existsByPersonalData_Email(personalDataDto.email())) {
            throw new IllegalArgumentException("Email già registrato");
        }

        PersonalData personalData = new PersonalData(
                personalDataDto.userName(),
                personalDataDto.userSurname(),
                personalDataDto.fiscalCode(),
                personalDataDto.address(),
                personalDataDto.email()
        );

        User newUser = new User(personalData, passwordEncoder.encode(personalDataDto.password()));
        utenteRepository.save(newUser);
    }

    /**
     * Modifica i dati personali di un utente esistente.
     *
     * @param nuovaPersonalData l'identificativo dell'utente da modificare
     * @param nuovaPersonalData i nuovi dati personali da impostare
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void updateAccount(UUID userId, PersonalData nuovaPersonalData) {
        User utente = utenteRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!nuovaPersonalData.getEmail().equals(utente.getPersonalData().getEmail()))
            throw new IllegalArgumentException("associated email cannot change");
        utente.setPersonalData(nuovaPersonalData);
        utenteRepository.save(utente);
    }

    /**
     * Elimina un account utente dal sistema.
     *
     * @param userId l'identificativo dell'utente da eliminare
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void deleteAccount(UUID userId) {
        if (!utenteRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }

        utenteRepository.deleteById(userId);
    }

    /**
     * Recupera l'identificativo univoco dell'utente associato a una determinata email.
     *
     * @param email l'indirizzo email da ricercare
     * @return l'identificativo UUID dell'utente
     */
    public UUID getUserIdByEmail(String email) {
        return utenteRepository.findByPersonalData_Email(email)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con questa email: " + email))
                .getId();
    }

    /**
     * Recupera l'identificativo univoco dell'utente associato a un determinato username.
     *
     * @param userName lo username da ricercare
     * @return l'identificativo UUID dell'utente
     */
    public UUID getUserIdByUserName(String userName) {
        return utenteRepository.findByPersonalData_UserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con questo username: " + userName))
                .getId();
    }
}