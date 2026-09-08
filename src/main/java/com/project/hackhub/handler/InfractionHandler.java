/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.handler;


import com.project.hackhub.dto.InfractionDTO;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Infraction;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;
import com.project.hackhub.observer.EventManager;
import com.project.hackhub.observer.EventType;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.SubmissionRepository;
import com.project.hackhub.repository.TeamRepository;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static com.project.hackhub.observer.EventType.INFRACTION;
import static com.project.hackhub.observer.EventType.PENALIZED_TEAM;

/**
 * Gestisce la segnalazione, la gestione, la penalizzazione, l'espulsione e la rimozione
 * delle infrazioni commesse dai team durante l'hackathon.
 */
@Component
@RequiredArgsConstructor
public class InfractionHandler {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final SubmissionRepository submissionRepository;

    /**
     * Elimina un rapporto di infrazione identificato tramite hackathonId e indice nella lista.
     *
     * @param deleterId l'identificatore dell'utente (organizzatore o mentor)
     * @param hackathonId l'identificatore dell'hackathon
     * @param infractionIndex la posizione dell'infrazione nella lista
     * @author Cosmina Androne
     */
    @Transactional
    public void deleteInfraction(UUID deleterId, UUID hackathonId, int infractionIndex) {
        User deleter = userRepository.findById(deleterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        if(!deleter.hasPermission(Permission.CAN_DELETE_INFRACTION, hackathon))
            throw new UnsupportedOperationException("User lacks required permission to delete an infraction.");

        if (infractionIndex < 0 || infractionIndex >= hackathon.getInfractions().size()) {
            throw new IllegalArgumentException("infraction index not valid");
        }

        if(hackathon.getStateType() != HackathonStateType.ONGOING) {
            throw new UnsupportedOperationException("L'operazione non può essere eseguita: l'Hackathon deve essere in stato ONGOING.");
        }

        hackathon.getInfractions().remove(infractionIndex);
        hackathonRepository.save(hackathon);
    }

    /**
     * Espelle un team da un Hackathon rimuovendolo e cancellando le relative sottomissioni.
     *
     * @param team l'identificatore del team da espellere
     * @param coordinator l'identificatore del coordinatore
     * @throws IllegalArgumentException se i parametri non esistono
     * @author Sonia Bevilacqua
     */
    @Transactional
    public void expelTeam(UUID team, UUID coordinator) {

        User coord = userRepository.findById(coordinator).orElseThrow(() -> new IllegalArgumentException("coordinator cannot be null"));
        Team t = teamRepository.findById(team).orElseThrow(() -> new IllegalArgumentException("team to expel cannot be null"));
        Hackathon h = t.getHackathon();

        boolean isOngoingOrAppraisal = (h.getStateType() == HackathonStateType.ONGOING
                || h.getStateType() == HackathonStateType.APPRAISAL);

        if(!coord.hasPermission(Permission.CAN_EXPEL_TEAM, h) || !isOngoingOrAppraisal) {
            throw new UnsupportedOperationException("Impossibile espellere il team. Assicurati di avere i permessi e che l'Hackathon sia ONGOING o APPRAISAL.");
        }

        List<User> teamMembers = t.getTeamMembersList();
        EventManager.getInstance().notify(EventType.EXPULSION_TEAM, teamMembers, "Il team con cui stavi partecipando all'hackathon " + h.getId() + " è stato espulso.",  h);

        h.removeInfractionByTeam(t);
        h.removeAidRequestByTeam(t);
        hackathonRepository.save(h);

        submissionRepository.deleteByTeam(t);

        teamRepository.delete(t);
    }

    /**
     * Permette al coordinatore di gestire e visualizzare le infrazioni di un team.
     *
     * @param coordinator l'identificatore del coordinatore
     * @param team l'identificatore del team
     * @return la lista delle infrazioni registrate per il team
     */
    @Transactional
    public List<Infraction> handleInfraction(UUID coordinator, UUID team) {

        User coord = userRepository.findById(coordinator).orElseThrow(
                () -> new IllegalArgumentException("coordinator cannot be null"));
        Team t = teamRepository.findById(team).orElseThrow(
                () -> new IllegalArgumentException("team to expel cannot be null"));
        Hackathon h = t.getHackathon();

        boolean isOngoingOrAppraisal = (h.getStateType() == HackathonStateType.ONGOING
                || h.getStateType() == HackathonStateType.APPRAISAL);

        if (!coord.hasPermission(Permission.CAN_MANAGE_INFRACTIONS, h) || !isOngoingOrAppraisal) {
            throw new UnsupportedOperationException("Impossibile gestire le infrazioni. Permessi insufficienti o stato Hackathon non valido.");
        }

        return hackathonRepository.findInfractionByTeam(h, t).orElseThrow(
                () -> new IllegalArgumentException("Nessuna infrazione trovata per questo team."));
    }

    /**
     * Penalizza un team sottraendo punti dal voto finale.
     *
     * @param coordinator l'identificatore del coordinatore
     * @param team l'identificatore del team da penalizzare
     * @param points i punti da sottrarre
     */
    @Transactional
    public void penalizeTeam(UUID coordinator, UUID team, float points) {

        User coord = userRepository.findById(coordinator).orElseThrow(
                () -> new IllegalArgumentException("coordinator cannot be null"));
        Team t = teamRepository.findById(team).orElseThrow(
                () -> new IllegalArgumentException("team to penalize cannot be null"));

        if(points <= 0 || points > 10) throw new IllegalArgumentException("Numero di punti da sottrarre non valido (deve essere tra 0 e 10).");

        Hackathon h = t.getHackathon();

        boolean isOngoingOrAppraisal = (h.getStateType() == HackathonStateType.ONGOING
                || h.getStateType() == HackathonStateType.APPRAISAL);

        if (!coord.hasPermission(Permission.CAN_PENALIZE_TEAM, h) || !isOngoingOrAppraisal) {
            throw new UnsupportedOperationException("cannot perform this action. Assicurati di essere il organizzatore e che l'Hackathon sia in corso (ONGOING o APPRAISAL).");
        }

        h.removeInfractionByTeam(t);
        hackathonRepository.save(h);

        Float currentGrade = t.getGrade();
        if (currentGrade == null) {
            currentGrade = 0.0f;
        }

        t.setGrade(currentGrade - points);
        teamRepository.save(t);
        EventManager.getInstance().notify(PENALIZED_TEAM, t.getTeamMembersList(), "Il team con cui stavi partecipando all'hackathon" + h.getId() + "è stato penalizzato.", h);
    }

    /**
     * Permette a un mentor di segnalare un'infrazione commessa da un team.
     *
     * @param mentor l'identificatore del mentor
     * @param dto il DTO contenente i dettagli dell'infrazione
     */
    @Transactional
    public void reportInfraction(UUID mentor, InfractionDTO dto) {

        User m = userRepository.findById(mentor).orElseThrow(
                () -> new IllegalArgumentException("Il mentore non può essere null"));
        if(dto == null) throw new IllegalArgumentException("dto non può essere null");
        if(!checkInfractionData(dto)) throw new IllegalArgumentException("dto non è valid");
        Team t = teamRepository.findById(dto.team()).orElseThrow(
                () -> new IllegalArgumentException("team non può essere null"));
        Hackathon h = t.getHackathon();

        if(!m.hasPermission(Permission.CAN_REPORT_INFRACTION, h) || h.getStateType() != HackathonStateType.ONGOING) {
            throw new IllegalArgumentException("Impossibile segnalare infrazione. L'utente non ha i permessi o l'Hackathon non è ONGOING.");
        }

        Infraction infraction = new Infraction(t, dto.description(), dto.type());
        h.addInfraction(infraction);
        hackathonRepository.save(h);
        EventManager.getInstance().notify(INFRACTION, List.of(h.getCoordinator()), "È stata segnalata un'infrazione per l'hackathon" + h.getId() + "!", h);
    }

    /**
     * Verifica la completezza e validità dei dati dell'infrazione nel DTO.
     */
    private boolean checkInfractionData(InfractionDTO dto) {

        if(dto.team() == null) return false;
        if(dto.description() == null || dto.description().isEmpty()) return false;
        return dto.type() != null;
    }
}