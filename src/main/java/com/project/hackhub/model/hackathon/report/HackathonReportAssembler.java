/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.model.hackathon.report;


import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.user.User;
import com.project.hackhub.model.user.state.Permission;

/**
 * Classe che costruisce differenti viste di report in base ai permessi dell'utente.
 */

public class HackathonReportAssembler {

    /**
     * Costruisce un report pubblico contenente esclusivamente i dati visibili a tutti.     *
     * @param data dati aggregati del report
     * @return vista del report pubblico
     * @author Sonia Bevilacqua
     */
    public PublicReport buildPublic(ReportData data) {
        return new PublicReport(
                data.getName(),
                data.getRuleBook(),
                data.getReservation(),
                data.getExpiredSubscriptionsDate(),
                data.getState(),
                data.getMoneyPrice(),
                data.getMaxTeamDimension(),
                data.getWinner(),
                data.getTeamsGrades()
        );
    }

    /**
     * Costruisce un report dettagliato per i membri di un team iscritto a un determinato hackathon.
     * @param data dati aggregati del report
     * @return vista del report dettagliato
     * @author Cosmina Androne
     */
    public DetailedReport buildDetailed(ReportData data) {
        return new DetailedReport(
                data.getName(),
                data.getRuleBook(),
                data.getReservation(),
                data.getExpiredSubscriptionsDate(),
                data.getState(),
                data.getMoneyPrice(),
                data.getMaxTeamDimension(),
                data.getWinner(),
                data.getCoordinator(),
                data.getJudge(),
                data.getMentorsList()
        );
    }

    /**
     * Costruisce un report per lo staff con accesso completo ai dati amministrativi.
     * @param data dati aggregati del report
     * @return vista del report per lo staff
     * @author Sonia Bevilacqua
     */
    public StaffReport buildStaff(ReportData data) {
        return new StaffReport(
                data.getName(),
                data.getRuleBook(),
                data.getReservation(),
                data.getState(),
                data.getMoneyPrice(),
                data.getMaxTeamDimension(),
                data.getWinner(),
                data.getTeamsList(),
                data.getMentorsList(),
                data.getCoordinator(),
                data.getJudge(),
                data.getTeamsGrades(),
                data.getAidRequests(),
                data.getExpiredSubscriptionsDate(),
                data.getInfractions()
        );
    }

    /**
     * Costruisce il report appropriato in base ai permessi dell'utente.
     * Se l'utente è nullo o non possiede il permesso TEAM, restituisce un report pubblico.
     * Se l'utente possiede il permesso TEAM, restituisce un report dettagliato.
     * Se l'utente possiede il permesso STAFF, restituisce un report con accesso amministrativo completo.
     * @param data dati aggregati del report
     * @param h contesto dell'hackathon per la verifica dei permessi
     * @param u utente richiedente (può essere nullo)
     * @return vista del report corrispondente al livello di accesso dell'utente
     * @author Cosmina Androne
     */
    public Report build(ReportData data, Hackathon h, User u) {
        if (u == null)
            return buildPublic(data);
        if (u.hasPermission(Permission.STAFF_PERMISSION, h))
            return buildStaff(data);
        if (u.hasPermission(Permission.TEAM_PERMISSION, h))
            return buildDetailed(data);
        //registered user that is not part of the hackathon
        return buildPublic(data);
    }
}