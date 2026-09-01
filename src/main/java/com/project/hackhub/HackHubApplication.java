/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub;
 

import com.project.hackhub.observer.EventListener;
import com.project.hackhub.observer.EventManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * Classe principale di avvio dell'applicazione Spring Boot HackHub.
 * Gestisce il bootstrap dell'applicazione e l'inizializzazione dei listener del pattern Observer.
 */
@SpringBootApplication
public class HackHubApplication {

	/**
	 * Metodo principale (entry point) dell'applicazione.
	 * Avvia il contesto di Spring Boot e richiama l'inizializzazione dei listener.
	 *
	 * @param args gli argomenti della riga di comando passati all'avvio
	 */
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(HackHubApplication.class, args);
		initializeListeners(context);
	}

	/**
	 * Recupera automaticamente tutti i bean di tipo {@link EventListener} dal contesto di Spring
	 * e li registra all'interno dell'istanza singleton di {@link EventManager}.
	 *
	 * @param context il contesto applicativo configurabile di Spring
	 */
	private static void initializeListeners(ConfigurableApplicationContext context) {
		EventManager eventManager = EventManager.getInstance();

		Map<String, EventListener> listeners = context.getBeansOfType(EventListener.class);
		for (EventListener listener : listeners.values()) {
			eventManager.addListenerToList(listener);
		}
	}
}