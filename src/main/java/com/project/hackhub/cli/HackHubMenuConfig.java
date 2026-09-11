/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.cli;
 

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.jline.PromptProvider;

/**
 * Classe di configurazione per la personalizzazione del prompt e della visualizzazione
 * dell'interfaccia a riga di comando (Spring Shell) dell'applicazione HackHub.
 */
@Configuration
public class HackHubMenuConfig {

    private final HackHubCliCommands cliCommands;
    private boolean isFirstPrompt = true;

    /**
     * Costruisce la configurazione del menu iniettando i comandi CLI tramite lazy loading.
     *
     * @param cliCommands i comandi CLI di HackHub
     */
    public HackHubMenuConfig(@Lazy HackHubCliCommands cliCommands) {
        this.cliCommands = cliCommands;
    }

    /**
     * Configura il provider del prompt testuale per la shell.
     * Mostra il menu principale completo in occasione del primo avvio,
     * impostando successivamente il prompt standard colorato di giallo.
     *
     * @return un {@code PromptProvider} che definisce l'aspetto del prompt della CLI
     */
    @Bean
    public PromptProvider myPromptProvider() {
        return () -> {
            if (isFirstPrompt) {
                isFirstPrompt = false;
                return new AttributedString(cliCommands.showMenu() + "\nhackhub:> ",
                        AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
            }

            return new AttributedString("hackhub:> ",
                    AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
        };
    }
}