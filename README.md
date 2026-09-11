# HackHub

Un sistema software per la gestione, l'organizzazione e la partecipazione agli Hackathon. La piattaforma facilita l'interazione tra organizzatori, partecipanti e membri dello staff (giudici e mentori) attraverso un'infrastruttura ibrida che espone API REST e, sul lato server, un'interfaccia a riga di comando (CLI).

## Funzionalità Principali

Il sistema è progettato attorno a diversi ruoli operativi, ciascuno con permessi e flussi di lavoro specificati da rigorosi diagrammi di sequenza e macchine a stati (es. transizioni delle fasi dell'Hackathon).

### Gestione Utenti (Utente Registrato)
* **Gestione Profilo:** Modifica delle informazioni personali ed eliminazione dell'account.
* **Permessi:** Richiesta di elevazione dei privilegi per diventare Organizzatore tramite sottomissione di documentazione.
* **Team Networking:** Ricezione e gestione degli inviti (accettazione o rifiuto) per unirsi a un team esistente.

### Gestione Team e Partecipazione (Team Leader e Membri)
* **Creazione Team:** Registrazione di un nuovo team per un Hackathon in fase di "iscrizione", con assegnazione automatica del ruolo di Team Leader al creatore.
* **Gestione Formazione:** Rimozione di membri dal team e disiscrizione dell'intero team dall'evento.
* **Richieste di Supporto:** Creazione e modifica di richieste di supporto indirizzate allo staff dell'evento (operazione consentita solo ad Hackathon "in corso").

### Gestione Hackathon (Organizzatore)
* **Creazione e Configurazione:** Setup di nuovi eventi Hackathon.
* **Modifica Parametri:** Modifica dei parametri dell'evento (es. montepremi, dimensione massima dei team, regolamento, scadenza iscrizioni). Il sistema notifica automaticamente mentori, giudici e team iscritti in caso di modifiche strutturali.

## Stack Tecnologico
* **Linguaggio:** Java (JDK 23 consigliato)
* **Build Tool:** Gradle (v8.10)
* **Framework:** Spring Boot (inclusivo di Tomcat embedded e SpringDoc/OpenAPI per la documentazione REST)
* **Database:** H2 Database (relazionale in-memory/su file locale)
* **Modellazione e Design:** Visual Paradigm (Specifiche dei Casi d'Uso, Diagrammi di Sequenza)

#

**Sonia Bevilacqua e Cosmina Androne**
