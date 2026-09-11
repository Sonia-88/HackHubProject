/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.cli;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.project.hackhub.dto.HackathonCreationResponse;
import com.project.hackhub.dto.HackathonDTO;
import com.project.hackhub.dto.PersonalDataDTO;
import com.project.hackhub.handler.*;
import com.project.hackhub.model.hackathon.*;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Invitation;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.InvitationRepository;
import com.project.hackhub.repository.SubmissionRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.service.NotificationService;
import com.project.hackhub.service.calendar.Slot;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Componente principale per la gestione dell'interfaccia a riga di comando (CLI) di HackHub.
 * Fornisce tutti i comandi necessari agli utenti per interagire con il sistema in base ai
 * propri ruoli (organizzatori, mentor, giudici, partecipanti).
 *
 * @author Sonia Bevilacqua
 */
@ShellComponent
@RequiredArgsConstructor
public class HackHubCliCommands {

    private final TransactionTemplate transactionTemplate;
    private final AccountHandler accountHandler;
    private final AuthenticationHandler authenticationHandler;
    private final RequestOrganizerPermitHandler permitHandler;
    private final HackathonCreationHandler hackathonCreationHandler;
    private final HackathonHandler hackathonHandler;
    private final StaffHandler staffHandler;
    private final TeamHandler teamHandler;
    private final InvitationHandler invitationHandler;
    private final InvitationReplyHandler invitationReplyHandler;
    private final TeamPartecipationHandler teamPartecipationHandler;
    private final TeamLeaderChoiceHandler teamLeaderChoiceHandler;
    private final AidRequestHandler aidRequestHandler;
    private final SubmissionHandler submissionHandler;
    private final InfractionHandler infractionHandler;
    private final GradeHandler gradeHandler;
    private final WinnerChoiceHandler winnerChoiceHandler;
    private final PrizeHandler prizeHandler;
    private final HackathonRepository hackathonRepository;
    private final InvitationRepository invitationRepository;
    private final ParticipationHandler participationHandler;
    private final NotificationService notificationService;
    private final SubmissionRepository submissionRepository;
    private final TeamRepository teamRepository;

    private static UUID loggedInUserId = null;
    private static String loggedInUsernameStr = null;

    private boolean showSqlEnabled = false;

    /**
     * Stampa a video il menu principale con l'elenco completo dei comandi disponibili,
     * organizzati per fase di sviluppo dell'evento, indicando anche lo stato del login
     * e lo stato di visualizzazione dei log SQL.
     *
     * @return Il menu principale formattato.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"menu", "start", "m"}, value = "Mostra il menu principale completo")
    public String showMenu() {

        String statusUtente = (loggedInUsernameStr != null)
                ? " User: " + loggedInUsernameStr
                : " Effettuare il login (comando 2)";

        String statusSql = showSqlEnabled
                ? " Log SQL: ATTIVI (Comando 31 per spegnerli)"
                : " Log SQL: DISATTIVATI (Comando 31 per accenderli)";


        return "\n=======================================================\n" +
                " HACKHUB CLI\n" +
                "-------------------------------------------------------\n" +
                " Digitare il numero o scrivere il comando.\n" +
                "-------------------------------------------------------\n" +
                " ** SEMPLICE VISITATORE **\n" +
                "  0. show-hackathons       (Mostra tutti gli eventi e info)\n\n" +
                " ** ACCOUNT E SICUREZZA ** \n" +
                "  1. register-user         (Registra utente)\n" +
                "  2. login                 (Ottieni token)\n" +
                "  3. request-permit        (Richiedi permessi organizer)\n" +
                "  4. logoff                (Disconnetti utente attuale)\n\n" +
                " ** GESTIONE HACKATHON **\n" +
                "  5. create-hackathon      (crea evento e assegna staff)\n" +
                "  6. delete-hackathon      (Elimina hackathon)\n\n" +
                " ** GESTIONE DELLO STAFF] **\n" +
                "  7. add-mentor            (Aggiungi mentor)\n" +
                "  8. remove-mentor         (Rimuovi mentor)\n" +
                "  9. change-staff-role     (Cambia ruolo staff)\n\n" +
                " ** TEAM E PARTECIPAZIONE **\n" +
                " 10. create-team           (Crea team)\n" +
                " 11. invite-user           (Invita membro)\n" +
                " 12. accept-invitation     (Accetta invito)\n" +
                " 13. decline-invitation    (Rifiuta invito)\n" +
                " 14. change-leader         (Passa il ruolo di leader - Solo Leader)\n" +
                " 15. leave-team            (Abbandona il team)\n" +
                " 16. submit-work           (Sottomissione finale)\n" +
                " 17. unsubscribe-team      (Ritira squadra - Solo Leader)\n\n"+
                " ** MENTORING E SOTTOMISSIONI **\n" +
                " 18. request-aid           (Richiesta di supporto)\n" +
                " 19. show-aid-requests     (Vedi richieste - Mentor)\n"+
                " 20. propose-call          (Proponi una videochiamata - Mentor)\n\n"+
                " ** GIURIA E PENALITÀ **\n" +
                " 21. report-infraction     (Segnala team)\n" +
                " 22. penalize-team         (Sottrai punti)\n" +
                " 23. expel-team            (Espulsione)\n" +
                " 24. grade-submission      (Valuta progetto)\n\n" +
                " ** CHIUSURA **\n" +
                " 25. proclaim-winner       (Dichiara vincitore)\n" +
                " 26. claim-prize           (Ritira premio)\n\n" +
                " ** Messaggistica **\n" +
                " 27. show-invitations      (Mostra e gestisci inviti)\n" +
                " 28. show-notifications    (Mostra le notifiche ricevute)\n\n"+
                " ** Funzionalità per TEST **\n" +
                " 29. show-sql-command      (Mostra o nascondi i comandi SQL si Hibernate)\n"+
                " 30. change-hackaton-state (solo per test - qualsiasi utente loggato)\n" +
                "=======================================================\n" +
                statusUtente + "\n" + statusSql + "\n" +
                "=======================================================\n" +
                "Digitare \"m\" (oppure \"menu\" o \"start\")per visualizzare questo menu. \n" +
                "=======================================================\n";
    }

    /**
     * Visualizza l'elenco di tutti gli hackathon registrati nel sistema e i rispettivi
     * dettagli pubblici. Questa operazione è accessibile a qualsiasi utente, anche non loggato.
     *
     * @return Una stringa formattata contenente le informazioni di tutti gli hackathon.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"show-hackathons", "0"}, value = "Mostra l'elenco di tutti gli hackathon disponibili e i loro dettagli")
    public String showHackathons() {

        return transactionTemplate.execute(status -> {
            try {
                List<Hackathon> allHackathons = hackathonRepository.findAll();

                if (allHackathons.isEmpty()) {
                    return "Non ci sono hackathon nel sistema al momento.";
                }

                StringBuilder sb = new StringBuilder();
                sb.append("\n=== HACKATHON DISPONIBILI ===\n");

                for (Hackathon h : allHackathons) {
                    sb.append("---------------------------------------------------\n");
                    sb.append("NOME          : ").append(h.getName() != null ? h.getName() : "Bozza in lavorazione").append("\n");
                    sb.append("STATO         : ").append(h.getStateType() != null ? h.getStateType() : "BOZZA (Dati Incompleti)").append("\n");

                    if (h.getReservation() != null) {
                        if (h.getReservation().getTimeInterval() != null) {
                            sb.append("DATA INIZIO   : ").append(h.getReservation().getTimeInterval().startDate()).append("\n");
                            sb.append("DATA FINE     : ").append(h.getReservation().getTimeInterval().endDate()).append("\n");
                        }
                        if (h.getReservation().getLocation() != null) {
                            sb.append("LUOGO         : ").append(h.getReservation().getLocation().getName())
                                    .append(" (").append(h.getReservation().getLocation().getProvince()).append(") - ")
                                    .append(h.getReservation().getLocation().getAddress()).append("\n");
                        }
                    } else {
                        sb.append("DATE E LUOGO  : Da definire\n");
                    }

                    sb.append("SCAD. ISCRIZ. : ").append(h.getExpiredSubscriptionsDate() != null ? h.getExpiredSubscriptionsDate() : "Da definire").append("\n");
                    sb.append("MAX MEMBRI    : ").append(h.getMaxTeamDimension() != null ? h.getMaxTeamDimension() : "Da definire").append("\n");

                    if (h.getMoneyPrize() != null) {
                        sb.append("PREMIO        : ").append(h.getMoneyPrize().getQuantity()).append(" ").append(h.getMoneyPrize().getCurrency()).append("\n");
                    } else {
                        sb.append("PREMIO        : Da definire\n");
                    }

                    sb.append("REGOLE        : ").append(h.getRuleBook() != null ? h.getRuleBook() : "Da definire").append("\n");
                }
                sb.append("---------------------------------------------------\n");

                return sb.toString();

            } catch (Exception e) {
                return "Errore durante il recupero degli hackathon: " + e.getMessage();
            }
        });
    }

    /**
     * Consente la registrazione di un nuovo utente fornendo i dati anagrafici e di accesso.
     *
     * @param name       Il nome dell'utente.
     * @param surname    Il cognome dell'utente.
     * @param email      L'indirizzo email dell'utente.
     * @param fiscalCode Il codice fiscale dell'utente.
     * @param password   La password scelta per l'account.
     * @return L'esito della registrazione.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"register-user", "1"}, value = "Registra un nuovo utente")
    public String registerUser(
            @ShellOption(defaultValue = ShellOption.NULL) String name,
            @ShellOption(defaultValue = ShellOption.NULL) String surname,
            @ShellOption(defaultValue = ShellOption.NULL) String email,
            @ShellOption(defaultValue = ShellOption.NULL) String fiscalCode,
            @ShellOption(defaultValue = ShellOption.NULL) String password) {

        if (name == null || surname == null || email == null || fiscalCode == null || password == null) {
            return "=== GUIDA ALLA REGISTRAZIONE ===\n" +
                    "Puoi registrarti digitando '1' (oppure 'register-user') in due modi:\n\n" +
                    "1. INSERIMENTO RAPIDO (l'ordine è obbligatorio):\n" +
                    "   Digita i dati separati da uno spazio nel seguente ordine:\n" +
                    "   > 1 <Nome> <Cognome> <Email> <CodiceFiscale> <Password>\n" +
                    "   Esempio: 1 Alice Rossi alice@email.com ALCRSS99... segreta123\n\n" +
                    "2. INSERIMENTO CON ETICHETTE (l'ordine non conta):\n" +
                    "   Usa i nomi dei campi per inserire i dati come preferisci:\n" +
                    "   > 1 --name Alice --surname Rossi --email alice@email.com --fiscalCode ALCRSS99... --password segreta123";
        }

        try {
            PersonalDataDTO dto = new PersonalDataDTO(name, surname, fiscalCode, null, email, password);
            accountHandler.createAccount(dto);
            return "Utente registrato con successo.";
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    /**
     * Gestisce l'autenticazione dell'utente tramite username e password, salvando
     * lo stato della sessione per le operazioni successive.
     *
     * @param username Il nome utente.
     * @param password La password dell'utente.
     * @return Un messaggio che conferma l'avvenuto login o indica l'errore riscontrato.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"login", "2"}, value = "Effettua il login")
    public String login(
            @ShellOption(defaultValue = ShellOption.NULL) String username,
            @ShellOption(defaultValue = ShellOption.NULL) String password) {

        if (username == null || password == null) {
            return "=== GUIDA AL LOGIN ===\n" +
                    "Puoi effettuare l'accesso digitando '2' (oppure 'login') in due modi:\n\n" +
                    "1. INSERIMENTO RAPIDO (l'ordine è obbligatorio):\n" +
                    "   Digita i dati separati da uno spazio nel seguente ordine:\n" +
                    "   > 2 <Username> <Password>\n" +
                    "   Esempio: 2 Alice password\n\n" +
                    "2. INSERIMENTO CON ETICHETTE (l'ordine non conta):\n" +
                    "   Usa i nomi dei campi per inserire i dati come preferisci:\n" +
                    "   > 2 --username Alice --password password";
        }

        try {
            AuthResponse response = authenticationHandler.authenticateUser(new LoginDTO(username, password));
            loggedInUserId = accountHandler.getUserIdByUserName(username);
            loggedInUsernameStr = username;
            return "Login effettuato. Bentornato! (Token generato correttamente)";
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    /**
     * Invia una richiesta per ottenere i permessi di Organizzatore fornendo
     * il percorso a un documento di identità o certificazione.
     *
     * @param documentPath Il percorso del documento richiesto per la qualifica.
     * @return L'esito della richiesta.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"request-permit", "3"}, value = "Richiedi i permessi da Organizzatore")
    public String requestPermit(
            @ShellOption(defaultValue = ShellOption.NULL) String documentPath) {

        if (loggedInUserId == null) {
            return "ERRORE: Devi prima effettuare il login (Comando 2) per richiedere i permessi!";
        }

        if (documentPath == null) {
            return "=== GUIDA alla RICHIESTA PERMESSI ===\n" +
                    "Per diventare Organizzatore devi inviare un documento.\n\n" +
                    "1. INSERIMENTO RAPIDO:\n" +
                    "   > 3 <PercorsoDocumento>\n" +
                    "   Esempio: 3 /documenti/id.pdf\n\n" +
                    "2. INSERIMENTO CON ETICHETTA:\n" +
                    "   > 3 --documentPath /documenti/id.pdf";
        }

        try {
            FileTemplate fileTemplate = new FileTemplate();
            fileTemplate.setFileName(documentPath);
            permitHandler.requestPermission(loggedInUserId, fileTemplate);
            return "Richiesta inviata! I permessi da Organizzatore sono stati assegnati con successo.";
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    /**
     * Disconnette l'utente attualmente loggato, resettando lo stato della sessione.
     *
     * @return Messaggio di conferma del logout.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"logoff", "logout","4"}, value = "Disconnette l'utente attuale")
    public String logoff() {
        if (loggedInUserId == null) {
            return "Nessun utente è attualmente connesso.";
        }

        String utenteUscente = loggedInUsernameStr;

        loggedInUserId = null;
        loggedInUsernameStr = null;

        return "SUCCESSO: Arrivederci " + utenteUscente + "! Ti sei disconnesso correttamente.";
    }


    /**
     * Crea un nuovo hackathon o aggiorna una bozza esistente in base ai parametri forniti.
     * I dati parziali vengono salvati finché la configurazione non risulta completa.
     *
     * @param name                     Nome dell'evento.
     * @param ruleBook                 Regolamento dell'hackathon.
     * @param expiredSubscriptionsDate Data di scadenza per le iscrizioni (YYYY-MM-DD).
     * @param maxTeamDimension         Numero massimo di membri per team.
     * @param prizeAmount              Ammontare del premio in denaro.
     * @param currency                 Valuta del premio.
     * @param startDate                Data di inizio (YYYY-MM-DD).
     * @param endDate                  Data di fine (YYYY-MM-DD).
     * @param locationName             Nome della sede fisica o piattaforma virtuale.
     * @param province                 Provincia.
     * @param cap                      Codice di Avviamento Postale.
     * @param address                  Indirizzo.
     * @param judgeEmail               Email dell'utente designato come giudice.
     * @param mentorEmail              Email dell'utente designato come mentor iniziale.
     * @return L'esito della creazione (conferma di creazione o salvataggio bozza).
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"create-hackathon", "5"}, value = "Crea o aggiorna la bozza di un Hackathon")
    public String createHackathon(
            @ShellOption(defaultValue = ShellOption.NULL) String name,
            @ShellOption(defaultValue = ShellOption.NULL) String ruleBook,
            @ShellOption(defaultValue = ShellOption.NULL) String expiredSubscriptionsDate,
            @ShellOption(defaultValue = ShellOption.NULL) Integer maxTeamDimension,
            @ShellOption(defaultValue = ShellOption.NULL) Double prizeAmount,
            @ShellOption(defaultValue = ShellOption.NULL) String currency,
            @ShellOption(defaultValue = ShellOption.NULL) String startDate,
            @ShellOption(defaultValue = ShellOption.NULL) String endDate,
            @ShellOption(defaultValue = ShellOption.NULL) String locationName,
            @ShellOption(defaultValue = ShellOption.NULL) String province,
            @ShellOption(defaultValue = ShellOption.NULL) Integer cap,
            @ShellOption(defaultValue = ShellOption.NULL) String address,
            @ShellOption(defaultValue = ShellOption.NULL) String judgeEmail,
            @ShellOption(defaultValue = ShellOption.NULL) String mentorEmail) {

        if (loggedInUserId == null) {
            return "ERRORE: Devi prima effettuare il login (Comando 2) per creare un hackathon!";
        }


        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        String annoMeseDinamico = nextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));


        if (name == null && ruleBook == null && expiredSubscriptionsDate == null &&
                maxTeamDimension == null && prizeAmount == null && currency == null && startDate == null &&
                endDate == null && locationName == null && province == null && cap == null &&
                address == null && judgeEmail == null && mentorEmail == null) {
            return "=== GUIDA ALLA CREAZIONE HACKATHON ===\n" +
                    "Per creare un evento devi fornire TUTTI i dettagli. Usa le etichette:\n\n" +
                    "> 4 --name \"<Nome>\" --ruleBook \"<Regole>\" --expiredSubscriptionsDate <YYYY-MM-DD> " +
                    "--maxTeamDimension <Numero> --prizeAmount <Importo> --currency <Valuta> --startDate <YYYY-MM-DD> " +
                    "--endDate <YYYY-MM-DD> --locationName \"<NomeLuogo>\" --province \"<Provincia>\" --cap <CAP> " +
                    "--address \"<Indirizzo>\" --judgeEmail <Email> --mentorEmail <Email>\n\n" +
                    "ESEMPIO ESTESO DA INCOLLARE:\n" +
                    "4 --name \"Hackathon 2026\" --ruleBook \"Regole\" --expiredSubscriptionsDate \""+annoMeseDinamico+"-01\" " +
                    "--maxTeamDimension 4 --prizeAmount 1500.00 --currency \"EUR\" --startDate \""+annoMeseDinamico+"-10\" --endDate \""+annoMeseDinamico+"-15\" " +
                    "--locationName \"Polo Informatico\" --province \"RM\" --cap 00100 --address \"Via Roma 1\" " +
                    "--judgeEmail carlo@email.com --mentorEmail bob@email.com\n"+
                    "ESEMPIO RAPIDO (POSIZIONALE) DA INCOLLARE:\n" +
                    "4 \"Hackathon 2026\" \"Regole\" "+annoMeseDinamico+"-01 4 1500.00 \"EUR\" "+annoMeseDinamico+"-10 "+annoMeseDinamico+"-15 \"Polo Informatico\" \"RM\" 00100 \"Via Roma 1\" \"carlo@email.com\" \"bob@email.com\""+
                    "\n=== \n" +
                    "Puoi inserire tutti i dati subito, oppure inserirli in più passaggi.\n" +
                    "Il sistema salverà una bozza finché non avrai fornito tutte le informazioni necessarie.\n" +
                    "Esempio Passo 1 (Solo nome e regole): " +
                    "> 4 --name \"Hackathon Estivo\" --ruleBook \"Regolamento base\"\n" +
                    "Esempio Passo 2 (Aggiungi le date): " +
                    "> 4 --startDate \""+annoMeseDinamico+"-01\" --endDate \""+annoMeseDinamico+"-05\" --expiredSubscriptionsDate \""+annoMeseDinamico+"-20\"\n\n" +
                    "Usa le solite etichette per aggiungere i parametri mancanti alla tua bozza."
                    ;
        }

        try {
            UUID judgeId = null;
            if (judgeEmail != null && !judgeEmail.isEmpty()) {
                judgeId = accountHandler.getUserIdByEmail(judgeEmail);
                if (judgeId == null) return "ERRORE: Nessun utente registrato con l'email del Giudice (" + judgeEmail + ")";
            }

            List<UUID> mentors = new ArrayList<>();
            if (mentorEmail != null && !mentorEmail.isEmpty()) {
                UUID mentorId = accountHandler.getUserIdByEmail(mentorEmail);
                if (mentorId == null) return "ERRORE: Nessun utente registrato con l'email del Mentor (" + mentorEmail + ")";
                mentors.add(mentorId);
            }

            LocalDate expiredDate = expiredSubscriptionsDate != null ? LocalDate.parse(expiredSubscriptionsDate) : null;
            LocalDate start = startDate != null ? LocalDate.parse(startDate) : null;
            LocalDate end = endDate != null ? LocalDate.parse(endDate) : null;

            LocalDate today = LocalDate.now();

            if (expiredDate != null && !expiredDate.isAfter(today)) {
                return "ERRORE DATE: La data di chiusura iscrizioni (" + expiredDate + ") deve essere successiva a oggi.";
            }
            if (start != null && !start.isAfter(today)) {
                return "ERRORE DATE: La data di inizio dell'Hackathon (" + start + ") deve essere successiva a oggi.";
            }
            if (start != null && end != null && end.isBefore(start)) {
                return "ERRORE LOGICO: La data di fine (" + end + ") non può essere precedente alla data di inizio (" + start + ").";
            }
            if (start != null && expiredDate != null && start.isBefore(expiredDate)) {
                return "ERRORE LOGICO: L'Hackathon non può iniziare (" + start + ") prima che chiudano le iscrizioni (" + expiredDate + ").";
            }


            TimeInterval timeInterval = null;
            if (start != null && end != null) {
                timeInterval = new TimeInterval(start, end);
            }

            Location location = null;
            if (locationName != null || province != null || cap != null || address != null) {
                location = new Location(locationName, province, cap != null ? cap : 0, address);
            }

            Reservation reservation = null;
            if (location != null && timeInterval != null) {
                reservation = new Reservation(location, timeInterval);
            }

            Money moneyPrize = null;
            if (prizeAmount != null && currency != null) {
                moneyPrize = new Money(prizeAmount, currency);
            }


            HackathonDTO dto = new HackathonDTO(
                    name, ruleBook, expiredDate, maxTeamDimension,
                    mentors.isEmpty() ? null : mentors, moneyPrize, judgeId, reservation
            );


            HackathonCreationResponse response = hackathonCreationHandler.createHackathon(dto, loggedInUserId);

            if (response.created()) {
                return "SUCCESSO: Hackathon creato definitivamente! Messaggio dal server: " + response.message();
            } else {
                return "BOZZA SALVATA: " + response.message() + "\n" +
                        "I dati che hai inserito sono stati registrati nello snapshot. Lancia nuovamente il comando 4 per aggiungere i pezzi mancanti.";
            }
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    /**
     * Permette a un organizzatore di eliminare un hackathon esistente di sua competenza.
     * Se l'ID non viene fornito, viene visualizzata la lista degli hackathon organizzati
     * dall'utente loggato.
     *
     * @param hackathonId L'UUID dell'hackathon da eliminare (opzionale se si vuole vedere la lista).
     * @return L'esito dell'operazione di eliminazione o l'elenco degli hackathon.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"delete-hackathon", "6"}, value = "Elimina un Hackathon (solo Organizer)")
    public String deleteHackathon(
            @ShellOption(defaultValue = ShellOption.NULL) UUID hackathonId) {

        if (loggedInUserId == null) {
            return "ERRORE: Devi prima effettuare il login (Comando 2) per gestire gli hackathon!";
        }

        if (hackathonId == null) {
            List<Hackathon> allHackathons = hackathonRepository.findAll();
            StringBuilder sb = new StringBuilder();
            sb.append("\n=== I TUOI HACKATHON ORGANIZZATI ===\n");

            boolean found = false;
            for (Hackathon h : allHackathons) {
                if (h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    sb.append("- NOME : ").append(h.getName()).append("\n");
                    sb.append("  STATO: ").append(h.getStateType()).append("\n");
                    sb.append("  ID   : ").append(h.getId()).append("\n");
                    sb.append("---------------------------------------------------\n");
                    found = true;
                }
            }

            if (!found) {
                return "Non risultano Hackathon organizzati da te al momento.";
            }

            sb.append("\nPer eliminare un evento, copia il suo ID e lancia il comando:\n");
            sb.append("> 5 <ID_COPIATO>");
            return sb.toString();
        }

        try {
            hackathonHandler.deleteHackathon(loggedInUserId, hackathonId);
            return "SUCCESSO: Hackathon eliminato! Tutti i partecipanti e lo staff sono stati notificati.";
        } catch (Exception e) {
            return "Errore durante l'eliminazione: " + e.getMessage();
        }
    }

    /**
     * Aggiunge un utente designato con il ruolo di Mentor a uno specifico hackathon.
     *
     * @param hackathonName Nome dell'hackathon a cui aggiungere il mentor.
     * @param mentorEmail   L'indirizzo email del mentor.
     * @return Il risultato dell'assegnazione.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"add-mentor", "7"}, value = "Aggiunge un mentor all'hackathon")
    public String addMentor(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String mentorEmail) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login (Comando 2)!";

        if (hackathonName == null || mentorEmail == null) {
            List<Hackathon> allHackathons = hackathonRepository.findAll();
            StringBuilder sb = new StringBuilder("=== GUIDA: AGGIUNGI MENTOR ===\n");
            sb.append("> 6 --hackathonName \"<Nome>\" --mentorEmail \"<Email>\"\n");
            sb.append("Esempio: 6 --hackathonName \"Hackathon Invernale\" --mentorEmail \"bob@email.com\"\n\n");
            sb.append("I tuoi Hackathon attivi:\n");
            for (Hackathon h : allHackathons) {
                if (h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    sb.append("- ").append(h.getName()).append("\n");
                }
            }
            return sb.toString();
        }

        try {
            UUID mentorId = accountHandler.getUserIdByEmail(mentorEmail);
            UUID hackathonId = null;
            for (Hackathon h : hackathonRepository.findAll()) {
                if (h.getName().equalsIgnoreCase(hackathonName) && h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    hackathonId = h.getId();
                    break;
                }
            }
            if (hackathonId == null) return "ERRORE: Hackathon '" + hackathonName + "' non trovato.";

            staffHandler.addMentor(loggedInUserId, hackathonId, mentorId);
            return "SUCCESSO: Mentor " + mentorEmail + " aggiunto correttamente allo staff!";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Rimuove un mentor precedentemente assegnato a un hackathon.
     *
     * @param hackathonName Nome dell'hackathon.
     * @param mentorEmail   L'email del mentor da rimuovere.
     * @return L'esito della rimozione.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"remove-mentor", "8"}, value = "Rimuove un mentor dall'hackathon")
    public String removeMentor(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String mentorEmail) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login!";

        if (hackathonName == null || mentorEmail == null) {
            List<Hackathon> allHackathons = hackathonRepository.findAll();
            StringBuilder sb = new StringBuilder("=== GUIDA: RIMUOVI MENTOR ===\n");
            sb.append("> 7 --hackathonName \"<Nome>\" --mentorEmail \"<Email>\"\n");
            sb.append("Esempio: 7 --hackathonName \"Hackathon Invernale\" --mentorEmail \"bob@email.com\"\n\n");
            sb.append("I tuoi Hackathon attivi:\n");
            boolean found = false;
            for (Hackathon h : allHackathons) {
                if (h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    sb.append("- ").append(h.getName()).append("\n");
                    found = true;
                }
            }
            if (!found) {
                sb.append("Nessun Hackathon organizzato da te al momento.");
            }
            return sb.toString();
        }

        try {
            UUID mentorId = accountHandler.getUserIdByEmail(mentorEmail);
            UUID hackathonId = null;
            for (Hackathon h : hackathonRepository.findAll()) {
                if (h.getName().equalsIgnoreCase(hackathonName) && h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    hackathonId = h.getId();
                    break;
                }
            }
            if (hackathonId == null) return "ERRORE: Hackathon non trovato.";

            staffHandler.removeMentor(loggedInUserId, hackathonId, mentorId);
            return "SUCCESSO: Mentor " + mentorEmail + " rimosso dallo staff.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Modifica il ruolo di un utente all'interno dello staff di un determinato hackathon
     * (es. promozione a giudice o declassamento).
     *
     * @param hackathonName Il nome dell'hackathon.
     * @param targetEmail   L'email dell'utente interessato dalla modifica.
     * @param role          Il nuovo ruolo da assegnare (es. JUDGE, MENTOR).
     * @return Conferma dell'avvenuta modifica.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"change-staff-role", "9"}, value = "Modifica il ruolo dello staff (es. JUDGE, MENTOR)")
    public String changeStaffRole(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String targetEmail,
            @ShellOption(defaultValue = ShellOption.NULL) String role) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login!";

        if (hackathonName == null || targetEmail == null || role == null) {
            List<Hackathon> allHackathons = hackathonRepository.findAll();
            StringBuilder sb = new StringBuilder("=== GUIDA: CAMBIA RUOLO STAFF ===\n");
            sb.append("> 8 --hackathonName \"<Nome>\" --targetEmail \"<Email>\" --role \"<Ruolo>\"\n");
            sb.append("Esempio: 8 --hackathonName \"Hackathon Invernale\" --targetEmail \"carlo@email.com\" --role \"MENTOR\"\n\n");
            sb.append("I tuoi Hackathon attivi:\n");
            boolean found = false;
            for (Hackathon h : allHackathons) {
                if (h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    sb.append("- ").append(h.getName()).append("\n");
                    found = true;
                }
            }
            if (!found) {
                sb.append("Nessun Hackathon organizzato da te al momento.");
            }
            return sb.toString();
        }

        try {
            UUID targetId = accountHandler.getUserIdByEmail(targetEmail);
            UUID hackathonId = null;
            for (Hackathon h : hackathonRepository.findAll()) {
                if (h.getName().equalsIgnoreCase(hackathonName) && h.getCoordinator() != null && h.getCoordinator().getId().equals(loggedInUserId)) {
                    hackathonId = h.getId();
                    break;
                }
            }
            if (hackathonId == null) return "ERRORE: Hackathon non trovato.";

            staffHandler.changeStaffRole(loggedInUserId, hackathonId, targetId, role);
            return "SUCCESSO: Ruolo di " + targetEmail + " modificato in " + role;
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Registra un nuovo team per un evento hackathon, designando l'utente creatore
     * come Team Leader predefinito. L'hackathon deve trovarsi in fase di iscrizione.
     *
     * @param hackathonName Il nome dell'hackathon al quale iscriversi.
     * @param teamName      Il nome scelto per il nuovo team.
     * @return L'esito della creazione del team.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"create-team", "10"}, value = "Crea un nuovo team in un hackathon")
    public String createTeam(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String teamName) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login (Comando 2)!";

        if (hackathonName == null || teamName == null) {
            List<Hackathon> allHackathons = hackathonRepository.findAll();
            StringBuilder sb = new StringBuilder("=== GUIDA: CREA TEAM ===\n");
            sb.append("> 9 --hackathonName \"<NomeHackathon>\" --teamName \"<NomeTeam>\"\n");
            sb.append("Esempio: 9 --hackathonName \"Hackathon 2026\" --teamName \"Team2026\"\n\n");
            sb.append("Hackathon disponibili per l'iscrizione:\n");

            boolean found = false;
            for (Hackathon h : allHackathons) {
                if (h.getStateType() != null && h.getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
                    sb.append("- ").append(h.getName()).append("\n");
                    found = true;
                }
            }
            if (!found) {
                sb.append("Nessun Hackathon aperto alle iscrizioni in questo momento.");
            }
            return sb.toString();
        }

        try {
            UUID hackathonId = null;
            for (Hackathon h : hackathonRepository.findAll()) {
                if (h.getName().equalsIgnoreCase(hackathonName)) {
                    hackathonId = h.getId();
                    break;
                }
            }

            if (hackathonId == null) return "ERRORE: Hackathon '" + hackathonName + "' non trovato.";

            teamHandler.createTeam(loggedInUserId, hackathonId, teamName);

            return "SUCCESSO: Team '" + teamName + "' creato con successo nell'hackathon '" + hackathonName + "'!";
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    /**
     * Spedisce un invito ufficiale a un utente affinché si unisca a uno specifico team.
     *
     * @param teamName    Il nome del team mittente.
     * @param targetEmail L'email dell'utente destinatario dell'invito.
     * @return La conferma dell'invio dell'invito o un messaggio di errore.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"invite-user", "11"}, value = "Invita un utente nel team")
    public String inviteUser(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) String targetEmail) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login (Comando 2)!";
        if (teamName == null || targetEmail == null) return "Uso: 10 --teamName \"<NomeTeam>\" --targetEmail \"<Email>\"";

        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team '" + teamName + "' non trovato.";
            UUID targetId = accountHandler.getUserIdByEmail(targetEmail);

            invitationHandler.inviteUser(loggedInUserId, targetId, teamId);
            return "SUCCESSO: Invito inviato a " + targetEmail;
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Consente a un utente loggato di accettare un invito ricevuto per unirsi a un team.
     *
     * @param invitationId L'identificativo univoco (UUID) dell'invito da accettare.
     * @return L'esito dell'operazione.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"accept-invitation", "12"}, value = "Accetta un invito per unirti a un team")
    public String acceptInvitation(@ShellOption(defaultValue = ShellOption.NULL) UUID invitationId) {
        if (loggedInUserId == null) return "ERRORE: Effettua il login!";

        if (invitationId == null) {
            return " Devi specificare quale invito vuoi accettare!\n"
                    + showInvitations()
                    + "\nUso: 11 <ID_INVITO>";
        }

        try {
            invitationReplyHandler.acceptInvitation(loggedInUserId, invitationId);
            return "SUCCESSO: Invito accettato. Ora sei un membro ufficiale del team.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Consente a un utente loggato di rifiutare formalmente un invito in sospeso.
     *
     * @param invitationId L'identificativo univoco (UUID) dell'invito.
     * @return Messaggio di conferma.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"decline-invitation", "13"}, value = "Rifiuta un invito per unirti a un team")
    public String declineInvitation(@ShellOption(defaultValue = ShellOption.NULL) UUID invitationId) {
        if (loggedInUserId == null) return "ERRORE: Effettua il login!";

        if (invitationId == null) {
            return "Devi specificare quale invito vuoi rifiutare!\n"
                    + showInvitations()
                    + "\n Uso: 12 <ID_INVITO>";
        }

        try {
            invitationReplyHandler.declineInvitation(loggedInUserId, invitationId);
            return "SUCCESSO: Invito rifiutato correttamente.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Trasferisce il ruolo e i privilegi di Team Leader dall'utente attuale a un altro
     * membro del medesimo team.
     *
     * @param teamName       Il nome del team coinvolto.
     * @param newLeaderEmail L'email del membro che diventerà il nuovo Leader.
     * @return Conferma del passaggio di consegne.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"change-leader", "14"}, value = "Cede la leadership a un altro membro del team")
    public String changeLeader(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) String newLeaderEmail) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null || newLeaderEmail == null) return "Uso: 13 --teamName \"<NomeTeam>\" --newLeaderEmail \"<Email>\"";

        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";
            UUID newLeaderId = accountHandler.getUserIdByEmail(newLeaderEmail);

            teamLeaderChoiceHandler.chooseNewTeamLeader(newLeaderId, loggedInUserId, teamId);
            return "SUCCESSO: Leadership trasferita a " + newLeaderEmail;
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Permette a un membro del team (non leader) di abbandonare volontariamente la squadra.
     *
     * @param teamName Il nome del team da cui uscire.
     * @return Messaggio di conferma dell'avvenuta rimozione.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"leave-team", "15"}, value = "Abbandona il team")
    public String leaveTeam(@ShellOption(defaultValue = ShellOption.NULL) String teamName) {
        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null) return "Uso: 14 --teamName \"<NomeTeam>\"";
        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";
            teamPartecipationHandler.leaveTeam(loggedInUserId, teamId);
            return "SUCCESSO: Hai abbandonato il team.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Permette esclusivamente al Team Leader di ritirare l'intero team dall'hackathon,
     * annullandone di fatto l'iscrizione.
     *
     * @param teamName Il nome del team da cancellare.
     * @return Il risultato della cancellazione del team.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"unsubscribe-team", "17"}, value = "Ritira l'intero team dall'hackathon (Solo Team Leader)")
    public String unsubscribeTeam(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login (Comando 2)!";

        if (teamName == null) {
            return "=== GUIDA: RITIRA TEAM ===\n" +
                    "Questo comando elimina definitivamente l'iscrizione del tuo team dall'Hackathon.\n" +
                    "Solo il Team Leader può eseguire questa operazione.\n\n" +
                    "> 24 --teamName \"<NomeTeam>\"\n" +
                    "Esempio: 24 --teamName \"I Guerrieri\"";
        }

        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team '" + teamName + "' non trovato.";

            participationHandler.unsubscribeTeam(teamId,loggedInUserId);

            return "SUCCESSO: Il team '" + teamName + "' è stato ritirato definitivamente dall'Hackathon.";
        } catch (Exception e) {
            return "Errore durante il ritiro del team: " + e.getMessage();
        }
    }

    /**
     * Consente a un team di effettuare la sottomissione del proprio progetto finale
     * alla giuria. L'hackathon deve trovarsi in una fase compatibile con le sottomissioni.
     *
     * @param teamName Il nome del team autore.
     * @param filename Il nome del file inviato (es. "progetto.zip").
     * @return La conferma della corretta acquisizione del file.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"submit-work", "16"}, value = "Invia la sottomissione finale del team")
    public String submitWork(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) String filename) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null || filename == null) return "Uso: 16 --teamName \"<NomeTeam>\" --filename \"<NomeDelFile.zip>\"";
        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";
            SubmissionDTO dto = new SubmissionDTO(teamId, filename);

            submissionHandler.sendSubmission(loggedInUserId, dto);
            return "SUCCESSO: Progetto '" + filename + "' sottomesso con successo alla giuria.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Permette a un team di inviare una richiesta formale di supporto allo staff tecnico
     * o ai mentori, specificandone la natura e l'urgenza.
     *
     * @param teamName    Il nome del team richiedente.
     * @param type        La tipologia della richiesta (es. URGENT, SYSTEM_MALFUNCTION).
     * @param description Il testo descrittivo del problema o della richiesta.
     * @return L'esito della registrazione della richiesta di supporto.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"request-aid", "18"}, value = "Invia una richiesta di aiuto ai mentori")
    public String requestAid(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) String type,
            @ShellOption(defaultValue = ShellOption.NULL) String description) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null || type == null || description == null) {
            return "Uso: 15 --teamName \"<NomeTeam>\" --type \"<TIPO>\" --description \"<Descrizione>\"\n" +
                    "Tipi validi:\n"+
                    "CALL_PROPOSAL\t\t\t(Proposta di chiamata)\n"+
                    "URGENT\t\t\t\t\t(Urgente)\n"+
                    "SYSTEM_MALFUNCTION\t\t(Malfunzionamento del sistema)\n"+
                    "ACCEPTED\t\t\t\t(Accettato)\n"+
                    "REFUSED\t\t\t\t\t(Rifiutato)\n";
        }
        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";
            AidRequestType aidType = AidRequestType.valueOf(type.toUpperCase());
            AidRequestDTO dto = new AidRequestDTO(description, aidType, teamId);

            aidRequestHandler.sendAidRequest(loggedInUserId, dto);
            return "SUCCESSO: Richiesta di aiuto registrata. I mentori sono stati avvisati.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Raccoglie e visualizza tutte le richieste di supporto attive in uno specifico
     * hackathon. Funzione riservata esclusivamente ai Mentor o allo Staff di coordinamento.
     *
     * @param hackathonName Il nome dell'hackathon di riferimento.
     * @return La lista formattata delle richieste.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"show-aid-requests", "19"}, value = "Mostra le richieste di supporto di un Hackathon (Solo Staff)")
    public String showAidRequests(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login!";
        if (hackathonName == null) return "Uso: 27 --hackathonName \"<NomeHackathon>\"";

        return transactionTemplate.execute(status -> {
            try {
                UUID hackathonId = getHackathonIdByName(hackathonName);
                if (hackathonId == null) return "ERRORE: Hackathon non trovato.";

                List<AidRequest> requests = aidRequestHandler.getAllAidRequests(loggedInUserId, hackathonId);

                if (requests.isEmpty()) {
                    return "Nessuna richiesta di aiuto al momento per questo Hackathon.";
                }

                StringBuilder sb = new StringBuilder();
                sb.append("\n=== RICHIESTE DI SUPPORTO PER '").append(hackathonName).append("' ===\n");

                for (AidRequest req : requests) {
                    sb.append("---------------------------------------------------\n");
                    sb.append("TEAM         : ").append(req.getTeam().getName()).append("\n");
                    sb.append("TIPO         : ").append(req.getType()).append("\n");
                    sb.append("DESCRIZIONE  : ").append(req.getDescription() != null ? req.getDescription() : "Nessuna").append("\n");

                    if (req.getSlot() != null) {
                        sb.append("SLOT TEMPO   : ").append(req.getSlot().toString()).append("\n");
                    }
                }
                sb.append("---------------------------------------------------\n");

                return sb.toString();
            } catch (Exception e) {
                return "Errore: " + e.getMessage();
            }
        });
    }

    /**
     * Consente a un mentor di rispondere a una richiesta di supporto proponendo
     * uno slot temporale (inizio/fine) per una videoconferenza con il team.
     *
     * @param teamName  Il nome del team che ha richiesto supporto.
     * @param startTime Data e ora proposte per l'inizio della call (formato ISO-8601).
     * @param endTime   Data e ora proposte per il termine della call.
     * @return Esito della pianificazione della call.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"propose-call", "20"}, value = "Proponi una call di supporto a un team (Solo Mentor)")
    public String proposeCall(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) String startTime,
            @ShellOption(defaultValue = ShellOption.NULL) String endTime) {

        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login (Comando 2)!";

        if (teamName == null || startTime == null || endTime == null) {
            return "=== GUIDA: PROPONI UNA CALL AL TEAM ===\n" +
                    "Uso: 28 --teamName \"<NomeTeam>\" --startTime \"<YYYY-MM-DDTHH:MM>\" --endTime \"<YYYY-MM-DDTHH:MM>\"\n" +
                    "Esempio rapido per il 22 Agosto alle 15:00:\n" +
                    "> 28 --teamName \"I Guerrieri\" --startTime \"2026-08-22T15:00\" --endTime \"2026-08-22T16:00\"";
        }

        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team '" + teamName + "' non trovato.";

            java.time.LocalDateTime start = java.time.LocalDateTime.parse(startTime);
            java.time.LocalDateTime end = java.time.LocalDateTime.parse(endTime);

            Slot slot = new Slot(start, end);

            aidRequestHandler.proposeCall(loggedInUserId, slot, teamId);

            return "SUCCESSO: Proposta di call inviata al team '" + teamName + "' per il " + start.toLocalDate() +
                    " dalle " + start.toLocalTime() + " alle " + end.toLocalTime() + ".";
        } catch (java.time.format.DateTimeParseException e) {
            return "ERRORE FORMATO DATA: Usa il formato Anno-Mese-GiornoTOra:Minuti (es. 2026-08-22T15:00). Ricorda la 'T' in mezzo!";
        } catch (Exception e) {
            return "Errore durante la proposta della call: " + e.getMessage();
        }
    }


    /**
     * Permette ai membri dello staff di segnalare ufficialmente un'infrazione
     * commessa da un team in un evento in corso.
     *
     * @param teamName    Il nome del team trasgressore.
     * @param type        La tipologia sintetica dell'infrazione (es. PLAGIO).
     * @param description I dettagli dell'avvenimento.
     * @return Conferma di ricezione del rapporto d'infrazione.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"report-infraction", "21"}, value = "Segnala un'infrazione per un team")
    public String reportInfraction(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) String type,
            @ShellOption(defaultValue = ShellOption.NULL) String description) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null || type == null || description == null) {
            return "=== GUIDA: SEGNALA INFRAZIONE ===\n" +
                    "Uso: 17 --teamName \"<NomeTeam>\" --type \"<TipoInfrazione>\" --description \"<Dettagli>\"\n\n" +
                    " Tipi di infrazione suggeriti (puoi usare queste parole o inventarne altre):\n" +
                    " - PLAGIO         (Codice o progetto copiato)\n" +
                    " - RITARDO        (Consegna oltre i limiti di tempo)\n" +
                    " - SCORRETTEZZA   (Comportamento non sportivo)\n" +
                    " - REGOLAMENTO    (Violazione generale delle regole)\n\n" +
                    "Esempio rapido:\n" +
                    "> 17 --teamName \"Team2000\" --type \"PLAGIO\" --description \"Ha copiato il codice da internet\"";
        }

        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";
            InfractionDTO dto = new InfractionDTO(description, type, teamId);

            infractionHandler.reportInfraction(loggedInUserId, dto);
            return "SUCCESSO: Infrazione segnalata al coordinatore.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Consente a un giudice di sottrarre formalmente punti dal voto finale di un team
     * a causa di un'infrazione precedentemente segnalata.
     *
     * @param teamName Il nome del team da penalizzare.
     * @param points   Il quantitativo numerico (float) di penalità da infliggere.
     * @return L'esito della penalizzazione.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"penalize-team", "22"}, value = "Penalizza un team sottraendo punti - Judge")
    public String penalizeTeam(
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) Float points) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null || points == null) return "Uso: 18 --teamName \"<NomeTeam>\" --points <Numero>";
        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";

            infractionHandler.penalizeTeam(loggedInUserId, teamId, points);
            return "SUCCESSO: Team '" + teamName + "' penalizzato di " + points + " punti.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Espelle definitivamente un team da un hackathon a causa di gravi violazioni del regolamento.
     * L'operazione comporta l'annullamento della sottomissione e della partecipazione.
     *
     * @param teamName Il nome del team da espellere.
     * @return Il messaggio di conferma dell'espulsione e dell'invio delle notifiche.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"expel-team", "23"}, value = "Espelle un team dall'hackathon")
    public String expelTeam(@ShellOption(defaultValue = ShellOption.NULL) String teamName) {
        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (teamName == null) return "Uso: 19 --teamName \"<NomeTeam>\"";
        try {
            UUID teamId = getTeamIdByName(teamName);
            if (teamId == null) return "ERRORE: Team non trovato.";

            infractionHandler.expelTeam(teamId, loggedInUserId);
            return "SUCCESSO: Team espulso definitivamente dall'hackathon.";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }


    /**
     * Permette a un utente qualificato come Giudice di valutare il progetto finale di un team.
     * Se richiamato senza parametri finali, il comando funge da strumento di indagine mostrando
     * l'elenco di tutte le sottomissioni per evento e i voti attuali.
     *
     * @param hackathonName Il nome dell'hackathon valutato.
     * @param teamName      Il nome del team a cui assegnare il voto.
     * @param grade         Il punteggio numerico (float) da assegnare.
     * @param note          Un giudizio testuale a supporto del voto.
     * @return L'elenco esplorativo o il risultato dell'assegnazione del voto.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"grade-submission", "24"}, value = "Mostra le sottomissioni globali o valuta un progetto")
    public String gradeSubmission(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String teamName,
            @ShellOption(defaultValue = ShellOption.NULL) Float grade,
            @ShellOption(defaultValue = ShellOption.NULL) String note) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";

        if (teamName == null || grade == null || note == null) {
            return transactionTemplate.execute(status -> {
                try {
                    if (hackathonName == null) {
                        List<Submission> allSubs = submissionRepository.findAll();
                        if (allSubs.isEmpty()) return "📭 Nessuna sottomissione presente nel sistema al momento.";

                        StringBuilder sb = new StringBuilder();
                        sb.append("\n=== ELENCO GLOBALE SOTTOMISSIONI ===\n");
                        Map<String, List<Submission>> groupedSubs = allSubs.stream()
                                .collect(Collectors.groupingBy(s -> s.getHackathon().getName()));

                        for (Map.Entry<String, List<Submission>> entry : groupedSubs.entrySet()) {
                            sb.append("\nHACKATHON: [").append(entry.getKey()).append("]\n");
                            for (Submission sub : entry.getValue()) {
                                String currentGrade = sub.getGrade() != null ? sub.getGrade().toString() : "Da valutare";
                                sb.append("   TEAM: ").append(sub.getTeam().getName())
                                        .append("  |  VOTO: ").append(currentGrade).append("\n");
                            }
                        }
                        sb.append("\n---------------------------------------------------\n");
                        sb.append("💡 Per valutare un team, usa questo formato:\n");
                        sb.append("> 20 \"NomeHackathon\" \"NomeTeam\" 8.5 \"Ottimo lavoro!\"\n");
                        return sb.toString();
                    }
                    return "Formato incompleto. Uso corretto:\n> 20 \"" + hackathonName + "\" \"<NomeTeam>\" <Voto> \"<Giudizio>\"";
                } catch (Exception e) {
                    return "Errore di lettura: " + e.getMessage();
                }
            });
        }

        UUID targetSubId;
        try {
            targetSubId = transactionTemplate.execute(status -> {
                UUID hackId = getHackathonIdByName(hackathonName);
                if (hackId == null) throw new IllegalArgumentException("Hackathon '" + hackathonName + "' non trovato.");

                UUID tId = getTeamIdByName(teamName);
                if (tId == null) throw new IllegalArgumentException("Team '" + teamName + "' non trovato.");

                Submission sub = submissionRepository.findAll().stream()
                        .filter(s -> s.getTeam().getId().equals(tId))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Il team '" + teamName + "' non ha consegnato alcun progetto."));

                return sub.getIds();
            });
        } catch (Exception e) {
            return "ERRORE: " + e.getMessage();
        }

        try {
            GradeDTO dto = new GradeDTO(grade, note);
            gradeHandler.gradeSubmission(loggedInUserId, targetSubId, dto);
            return "SUCCESSO: Il team '" + teamName + "' è stato valutato con " + grade + "!";
        } catch (Exception e) {
            return "Errore durante la valutazione: " + e.getMessage();
        }
    }

    /**
     * Mostra la classifica ordinata dei team di un hackathon in base ai voti della giuria.
     * Se richiamato con i parametri completi, ufficializza la vittoria del team specificato
     * e chiude l'evento.
     *
     * @param hackathonName Il nome dell'hackathon da processare.
     * @param teamName      Il nome del team da insignire come vincitore.
     * @return La classifica esplorativa o l'esito della chiusura dell'evento.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"proclaim-winner", "25"}, value = "Mostra la classifica o proclama il team vincitore")
    public String proclaimWinner(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String teamName) {

        if (loggedInUserId == null) return "ERRORE: Effettua il login!";

        if (hackathonName == null || teamName == null) {
            return transactionTemplate.execute(status -> {
                try {
                    List<Team> allTeams = teamRepository.findAll();
                    if (allTeams.isEmpty()) return "Nessun team presente nel sistema.";

                    StringBuilder sb = new StringBuilder();
                    sb.append("\n=== CLASSIFICA TEAM PER HACKATHON ===\n");

                    Map<String, List<Team>> groupedTeams = allTeams.stream()
                            .collect(Collectors.groupingBy(t -> t.getHackathon().getName()));

                    for (Map.Entry<String, List<Team>> entry : groupedTeams.entrySet()) {
                        sb.append("\nHACKATHON: [").append(entry.getKey()).append("]\n");

                        List<Team> sortedTeams = entry.getValue().stream()
                                .sorted((t1, t2) -> {
                                    Float g1 = t1.getGrade() != null ? t1.getGrade() : -1.0f;
                                    Float g2 = t2.getGrade() != null ? t2.getGrade() : -1.0f;
                                    return Float.compare(g2, g1);
                                })
                                .toList();

                        int pos = 1;
                        for (Team t : sortedTeams) {
                            String gradeStr = t.getGrade() != null ? t.getGrade().toString() : "Nessun voto / Da valutare";
                            sb.append("   ").append(pos).append("° posto - TEAM: ").append(t.getName())
                                    .append("  |  VOTO: ").append(gradeStr).append("\n");
                            pos++;
                        }
                    }
                    sb.append("\n---------------------------------------------------\n");
                    sb.append("Per proclamare il vincitore e chiudere l'Hackathon usa:\n");
                    sb.append("> 21 \"<NomeHackathon>\" \"<NomeTeam>\"\n");

                    return sb.toString();
                } catch (Exception e) {
                    return "Errore durante la lettura delle classifiche: " + e.getMessage();
                }
            });
        }

        UUID targetHackathonId;
        UUID targetTeamId;

        try {
            targetHackathonId = transactionTemplate.execute(status -> {
                UUID hId = getHackathonIdByName(hackathonName);
                if (hId == null) throw new IllegalArgumentException("Hackathon '" + hackathonName + "' non trovato.");
                return hId;
            });

            targetTeamId = transactionTemplate.execute(status -> {
                UUID tId = getTeamIdByName(teamName);
                if (tId == null) throw new IllegalArgumentException("Team '" + teamName + "' non trovato.");
                return tId;
            });
        } catch (Exception e) {
            return "ERRORE: " + e.getMessage();
        }

        try {
            winnerChoiceHandler.proclaimWinner(targetTeamId, loggedInUserId, targetHackathonId);
            return "SUCCESSO 🏆: Il team '" + teamName + "' è stato proclamato VINCITORE e l'Hackathon è CONCLUSO!";
        } catch (Exception e) {
            return "Errore durante la proclamazione: " + e.getMessage();
        }
    }

    /**
     * Riscattazione della quota parte di premio da parte di un membro di un team risultato vincitore
     * a hackathon concluso.
     *
     * @param hackathonName Il nome dell'hackathon vinto.
     * @return Conferma di erogazione del premio o notifica di errore.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"claim-prize", "26"}, value = "Ritira il premio (solo team vincitore)")
    public String claimPrize(@ShellOption(defaultValue = ShellOption.NULL) String hackathonName) {
        if (loggedInUserId == null) return "ERRORE: Effettua il login!";
        if (hackathonName == null) return "Uso: 22 --hackathonName \"<NomeHackathon>\"";
        try {
            UUID hackathonId = getHackathonIdByName(hackathonName);
            if (hackathonId == null) return "ERRORE: Hackathon non trovato.";

            prizeHandler.claimPrize(loggedInUserId, hackathonId);
            return "SUCCESSO: Premio ritirato. La tua quota è stata accreditata!";
        } catch (Exception e) { return "Errore: " + e.getMessage(); }
    }

    /**
     * Funge da 'casella della posta', restituendo all'utente loggato tutti gli inviti
     * ricevuti da vari team e non ancora processati (in stato pending).
     *
     * @return Elenco degli inviti in sospeso, con istruzioni per accettarli o rifiutarli.
     * @author Cosmina Androne
     */
    @ShellMethod(key = {"show-invitations", "27"}, value = "Mostra i tuoi inviti in sospeso (La tua casella messaggi)")
    public String showInvitations() {
        if (loggedInUserId == null) return "ERRORE: Devi prima effettuare il login (Comando 2)!";

        return transactionTemplate.execute(status -> {
            try {
                List<Invitation> allInvitations = invitationRepository.findAll();

                List<Invitation> myPendingInvitations = allInvitations.stream()
                        .filter(inv -> inv.getAddressee() != null && inv.getAddressee().getId().equals(loggedInUserId))
                        .filter(Invitation::isPending)
                        .collect(Collectors.toList());

                if (myPendingInvitations.isEmpty()) {
                    return "Nessun nuovo invito in sospeso nella tua casella.";
                }

                StringBuilder sb = new StringBuilder("\n HAI " + myPendingInvitations.size() + " INVITO/I IN SOSPESO:\n");
                sb.append("--------------------------------------------------\n");
                for (Invitation inv : myPendingInvitations) {
                    sb.append("ID Invito     : ").append(inv.getId()).append("\n");

                    if (inv.getSender() != null) {
                        sb.append("Team Mittente : ").append(inv.getSender().getName()).append("\n");
                    }

                    sb.append("\n-> Per ACCETTARE digita : 11 ").append(inv.getId()).append("\n");
                    sb.append("-> Per RIFIUTARE digita : 12 ").append(inv.getId()).append("\n");
                    sb.append("--------------------------------------------------\n");
                }
                return sb.toString();

            } catch (Exception e) {
                return "Errore durante la lettura degli inviti: " + e.getMessage();
            }
        });
    }

    /**
     * Stampa a schermo la lista delle notifiche di sistema accumulate dall'utente loggato
     * (es. aggiornamenti sull'evento, sanzioni, cambiamenti di ruolo).
     *
     * @return La 'bacheca' formattata delle notifiche.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"show-notifications", "28"}, value = "Mostra la tua bacheca delle notifiche")
    public String showNotifications() {

        if (loggedInUserId == null) {
            return "ERRORE: Devi prima effettuare il login (Comando 2) per vedere le tue notifiche!";
        }

        try {
            List<com.project.hackhub.model.hackathon.Notification> notifications = notificationService.getUserNotifications(loggedInUserId);

            if (notifications == null || notifications.isEmpty()) {
                return "Nessuna nuova notifica. La tua bacheca è vuota.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("\n=== LE TUE NOTIFICHE (").append(notifications.size()).append(") ===\n");

            for (int i = 0; i < notifications.size(); i++) {
                sb.append("[").append(i + 1).append("] ").append(notifications.get(i).getMessage()).append("\n");
            }
            sb.append("==================================\n");

            return sb.toString();

        } catch (Exception e) {
            return "Errore durante il recupero delle notifiche: " + e.getMessage();
        }
    }

    /**
     * Comando di utility ad uso esclusivo dello sviluppatore.
     * Forza il passaggio manuale di stato dell'evento saltando le temporizzazioni.
     *
     * @param hackathonName Il nome dell'hackathon.
     * @param newState      Il nuovo stato (es. ONGOING, APPRAISAL).
     * @return Il risultato della forzatura di stato.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"change-hackaton-state", "30"}, value = "Forza lo stato di un Hackathon (Admin/Testing)")
    public String setHackathonState(
            @ShellOption(defaultValue = ShellOption.NULL) String hackathonName,
            @ShellOption(defaultValue = ShellOption.NULL) String newState) {

        if (loggedInUserId == null) return "ERRORE: Effettua prima il login!";

        return transactionTemplate.execute(status -> {
            try {
                if (hackathonName == null || newState == null) {
                    List<Hackathon> hackathons = hackathonRepository.findAll();
                    StringBuilder sb = new StringBuilder();
                    sb.append("=== GESTIONE STATI HACKATHON (solo per test ) ===\n\n");
                    sb.append("== ================================================================================================= ==\n");
                    sb.append("== Questo comando è solo per test.                                                                   == \n");
                    sb.append("== per lanciarlo è sufficiente essere loggati con qualsiasi utente.                                  ==\n");
                    sb.append("== Permette di cambiare stato all'Hackathon.                                                         ==\n");
                    sb.append("== In ogni caso SubscriptionTimerHandler cambia lo stato alle scadenze dell'hackathon in automatico. ==\n");
                    sb.append("== ================================================================================================= ==\n");
                    sb.append("\n");
                    sb.append("Eventi attuali nel sistema:\n");
                    for (Hackathon h : hackathons) {
                        sb.append(" [").append(h.getName()).append("] -> Stato: ")
                                .append(h.getStateType()).append("\n");
                    }
                    sb.append("\nStati disponibili:\n");
                    sb.append(" 1. SUBSCRIPTION_PHASE (o SUBSCRIPTION)\n");
                    sb.append(" 2. ONGOING\n");
                    sb.append(" 3. APPRAISAL\n");
                    sb.append(" 4. CONCLUDED\n");
                    sb.append("---------------------------------------------------\n");
                    sb.append("Uso: 30 \"<NomeHackathon>\" <NuovoStato>\n");
                    sb.append("Esempio: > 30 \"Hackathon 2026 2\" APPRAISAL\n");
                    return sb.toString();
                }

                UUID hackathonId = getHackathonIdByName(hackathonName);
                if (hackathonId == null) return "ERRORE: Hackathon '" + hackathonName + "' non trovato.";

                Hackathon hackathon = hackathonRepository.findById(hackathonId)
                        .orElseThrow(() -> new IllegalArgumentException("Hackathon non trovato"));

                HackathonStateType targetState;
                String normalized = newState.trim().toUpperCase();

                switch (normalized) {
                    case "SUBSCRIPTION", "SUBSCRIPTION_PHASE" -> targetState = HackathonStateType.SUBSCRIPTION_PHASE;
                    case "ONGOING" -> targetState = HackathonStateType.ONGOING;
                    case "APPRAISAL" -> targetState = HackathonStateType.APPRAISAL;
                    case "CONCLUDED" -> targetState = HackathonStateType.CONCLUDED;
                    default -> {
                        return "ERRORE: Stato '" + newState + "' non valido! Usa: SUBSCRIPTION_PHASE, ONGOING, APPRAISAL o CONCLUDED.";
                    }
                }

                HackathonStateType oldState = hackathon.getStateType();
                hackathon.setStateType(targetState);
                hackathonRepository.save(hackathon);

                return "SUCCESSO: Hackathon '" + hackathonName + "' spostato da ["
                        + oldState + "] a [" + targetState + "]!";

            } catch (Exception e) {
                return "Errore durante il cambio di stato: " + e.getMessage();
            }
        });
    }



    /**
     * Altro comando di utility tecnica (developer). Agisce sui log nativi di Hibernate,
     * consentendo di nascondere il fastidioso traffico SQL durante l'uso normale,
     * riabilitandolo solo quando necessario per scopi di tracciamento o debugging.
     *
     * @return Notifica dello stato di visibilità SQL appena applicato.
     * @author Sonia Bevilacqua
     */
    @ShellMethod(key = {"show-sql-command", "29"}, value = "Accende o spegne i log delle query SQL in tempo reale")
    public String toggleSqlLogs() {

        Logger sqlLogger = (Logger) LoggerFactory.getLogger("org.hibernate.SQL");

        if (showSqlEnabled) {
            sqlLogger.setLevel(Level.ERROR);
            showSqlEnabled = false;
            return "LOG SQL DISATTIVATI: La console ora è pulita.";
        } else {
            sqlLogger.setLevel(Level.DEBUG);
            showSqlEnabled = true;
            return "LOG SQL ATTIVATI: Vedrai tutte le query del database.";
        }
    }

    // ====================================================================
    // METODI
    // ====================================================================

    /**
     * Metodo helper di decodifica che rintraccia in sicurezza l'ID di un Hackathon.
     * Evita allo sviluppatore di esporre input di ID (UUID a 36 caratteri) all'utente CLI.
     *
     * @param name Nome formale dell'evento in archivio.
     * @return L'UUID corrispondente, o null se non localizzato.
     * @author Cosmina Androne
     */
    private UUID getHackathonIdByName(String name) {
        if (name == null) return null;
        for (Hackathon h : hackathonRepository.findAll()) {
            if (h.getName().equalsIgnoreCase(name)) {
                return h.getId();
            }
        }
        return null;
    }

    /**
     * Metodo helper di decodifica che rintraccia in sicurezza l'ID di un Team, proteggendo
     * contestualmente l'accesso transazionale per prevenire errori Hibernate di tipo Lazy.
     *
     * @param name Nome formale della squadra partecipante.
     * @return L'UUID corrispondente, o null se il team non esiste.
     * @author Sonia Bevilacqua
     */
    private UUID getTeamIdByName(String name) {
        if (name == null) return null;

        return transactionTemplate.execute(status -> {
            for (Hackathon h : hackathonRepository.findAll()) {
                if (h.getTeamsList() != null) {
                    for (Team t : h.getTeamsList()) {
                        if (t.getName() != null && t.getName().equalsIgnoreCase(name)) {
                            return t.getId();
                        }
                    }
                }
            }
            return null;
        });
    }
}