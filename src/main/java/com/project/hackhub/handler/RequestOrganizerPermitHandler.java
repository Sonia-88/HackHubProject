/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.model.team.FileTemplate;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Gestisce la richiesta da parte di un utente di ottenere i permessi per organizzare hackathon.
 */
@Component
@AllArgsConstructor
public class RequestOrganizerPermitHandler {

    private final UserRepository userRepository;

    /**
     * Elabora la richiesta di permesso di organizzatore validando il template di file caricato.
     *
     * @param user l'identificatore dell'utente richiedente
     * @param f il template di file caricato a supporto della richiesta
     * @author Cosmina Androne
     */
    @Transactional
    public void requestPermission(UUID user, FileTemplate f) {

        User user1 = userRepository.findById(user).orElseThrow(
                () -> new IllegalArgumentException("user cannot be null")
        );

        if(checkTemplateValidity(f)) {
            user1.setOrganizer(true);
            userRepository.save(user1);
        }
        else
            throw new IllegalArgumentException("permit as organizer cannot be given");
    }

    /**
     * Simula la validità del template di file.
     */
    private boolean checkTemplateValidity(FileTemplate f) {
        return true;
    }
}