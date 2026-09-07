/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.dto.AidRequestDTO;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.AidRequest;
import com.project.hackhub.model.team.AidRequestType;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import com.project.hackhub.service.calendar.CalendarAdapter;
import com.project.hackhub.service.calendar.Slot;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Gestisce le richieste di supporto, l'interazione con il calendario esterno per gli slot
 * e le proposte di chiamata tra mentor e team durante lo svolgimento dell'hackathon.
 */
@Service
public class AidRequestHandler {

    private final CalendarAdapter calendarAdapter;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    public AidRequestHandler(CalendarAdapter calendarAdapter, HackathonRepository hackathonRepository, UserRepository userRepository, TeamRepository teamRepository) {
        this.calendarAdapter = calendarAdapter;
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Recupera la lista degli slot temporali disponibili per una proposta di chiamata di supporto.
     *
     * @param user l'identificatore dell'utente che richiede gli slot
     * @param hackathon l'identificatore dell'hackathon di riferimento
     * @return una lista di oggetti {@code Slot} disponibili
     * @throws IllegalStateException se lo stato dell'Hackathon non è ONGOING
     * @throws IllegalArgumentException se l'utente non viene trovato
     * @throws UnsupportedOperationException se l'utente non possiede i permessi necessari
     * @author Cosmina Androne
     */
    @Transactional
    public List<Slot> getAvailableSlots(UUID user, UUID hackathon){
        Hackathon h = this.hackathonRepository.findById(hackathon)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));
        if(h.getStateType() != HackathonStateType.ONGOING)
            throw new IllegalStateException("Hackathon is not IN_CORSO");
        User u = this.userRepository.findById(user)
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        if (!u.hasPermission(Permission.CAN_PROPOSE_CALL, h))
            throw new UnsupportedOperationException("User does not have required permission");
        return this.calendarAdapter.getAvailableSlots(h);
    }

    /**
     * Propone una chiamata a un team che ha inviato una richiesta di supporto per uno slot temporale specifico.
     *
     * @param mentor l'identificatore del mentor che avvia l'azione
     * @param slot lo slot temporale scelto per la chiamata proposta
     * @param team l'identificatore del team destinatario della proposta
     * @throws IllegalArgumentException se il team o l'utente non vengono trovati
     * @throws IllegalStateException se l'Hackathon non è in corso
     * @throws UnsupportedOperationException se l'utente manca dei permessi richiesti
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void proposeCall(UUID mentor, Slot slot, UUID team){
        Team t = this.hackathonRepository.findByTeamId(team)
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        if(t.getHackathon().getStateType() != HackathonStateType.ONGOING)
            throw new IllegalStateException("Hackathon is not IN_CORSO");
        User u = this.userRepository.findById(mentor)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(!u.hasPermission(Permission.CAN_PROPOSE_CALL, t.getHackathon()))
            throw new UnsupportedOperationException("User does not have required permission");
        if(t.isHasPendingCallProposal()) {
            t.getHackathon().removeAidRequestByTeam(t);
        }
        boolean removed = this.calendarAdapter.removeSlot(t.getHackathon(), slot);
        if(removed) {
            AidRequest a = new AidRequest(t, AidRequestType.CALL_PROPOSAL, slot);
            t.getHackathon().addAidRequest(a);
            t.setHasPendingCallProposal(true);
            hackathonRepository.save(t.getHackathon());
        }
        EventManager.getInstance().notify(EventType.PENDING_CALL_PROPOSAL, t.getTeamMembersList(), "a call proposal has been made for your team", t.getHackathon());
    }

    /**
     * Invia una richiesta di aiuto da parte del team leader per l'hackathon in corso.
     *
     * @param leader l'identificatore dell'utente leader che effettua la richiesta
     * @param dto il DTO contenente i dettagli della richiesta di supporto
     * @throws IllegalArgumentException se i dati o il team non sono validi
     * @throws IllegalStateException se l'hackathon non è nello stato ONGOING
     * @throws UnsupportedOperationException se l'utente non ha i permessi necessari
     * @author Cosmina Androne
     */
    @Transactional
    public void sendAidRequest(UUID leader, AidRequestDTO dto){
        if(dto == null) return;
        Team realTeam = this.teamRepository.findById(dto.team())
                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
        User u =  this.userRepository.findById(leader)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if(realTeam.getHackathon().getStateType() != HackathonStateType.ONGOING)
            throw new IllegalStateException("Hackathon is not ONGOING");
        if (!u.hasPermission(Permission.CAN_SEND_AID_REQUEST, realTeam.getHackathon()))
            throw new UnsupportedOperationException("User does not have required permission");
        if(!realTeam.getTeamLeader().equals(u))
            throw new IllegalArgumentException("This is not your team! Only the actual team leader can send an aid request");
        if (checkAidRequestData(dto, realTeam)) {
            AidRequest aidRequest = new AidRequest(realTeam, dto.type(), dto.description(), null);
            realTeam.getHackathon().addAidRequest(aidRequest);
            realTeam.setHasPendingCallProposal(true);
            hackathonRepository.save(realTeam.getHackathon());
        } else
            throw new IllegalStateException("Team already has a pending request or fields are incomplete");
    }

    /**
     * Valida i dati della richiesta di supporto e ne verifica l'unicità per il team.
     *
     * @param dto il DTO della richiesta da controllare
     * @param t il team per cui viene effettuata la richiesta
     * @return {@code true} se i campi sono completi e non esistono richieste pendenti, {@code false} altrimenti
     * @author Sonia Bevilacqua
     */
    private boolean checkAidRequestData(AidRequestDTO dto, Team t){
        if(dto.team() == null || dto.description().isBlank()
                || dto.type() == null)
            return false;
        if(t==null)
            return false;
        return !t.isHasPendingCallProposal();
    }

    /**
     * Elimina una richiesta di supporto attiva per uno specifico team in un hackathon.
     *
     * @param requesterId l'identificatore dell'utente che effettua la richiesta di eliminazione
     * @param hackathonId l'identificatore dell'hackathon
     * @param teamId l'identificatore del team
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void deleteAidRequest(UUID requesterId, UUID hackathonId, UUID teamId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        Team team = hackathon.getTeamsList().stream()
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Team does not participate in this hackathon"));

        if (!requester.hasPermission(Permission.CAN_HANDLE_AID_REQUEST, hackathon))
            throw new UnsupportedOperationException("User does not have required permission");

        AidRequest toRemove = hackathon.getAidRequests().stream()
                .filter(r -> r.getTeam().equals(team))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No support request active for this team"));

        if(!requester.equals(team.getTeamLeader()))
            throw new IllegalArgumentException("This is not your team! Only the actual team leader can delete the aid request");
        hackathon.getAidRequests().remove(toRemove);
        team.setHasPendingCallProposal(false);
        hackathonRepository.save(hackathon);
    }

    /**
     * Restituisce tutte le richieste di supporto associate a un determinato hackathon.
     *
     * @param viewerId l'identificatore dell'utente visualizzatore (mentor o coordinatore)
     * @param hackathonId l'identificatore dell'hackathon
     * @return la lista delle richieste di supporto {@code AidRequest}
     * @author Sonia Bevilacqua
     */
    @Transactional
    public List<AidRequest> getAllAidRequests(UUID viewerId, UUID hackathonId) {
        User viewer = userRepository.findById(viewerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));
        if(!viewer.hasPermission(Permission.STAFF_PERMISSION, hackathon))
            throw new UnsupportedOperationException("user lacks required permissions for the operation");

        if(hackathon.getStateType().equals(HackathonStateType.SUBSCRIPTION_PHASE)) {
            throw new UnsupportedOperationException("Operation cannot be performed if this state");
        }

        return hackathon.getAidRequests();
    }
}