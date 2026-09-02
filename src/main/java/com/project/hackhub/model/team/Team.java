/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.model.team;
 

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entità JPA che rappresenta una squadra (team) partecipante a un hackathon.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
public class Team {

    private String name;

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "hackathon_id")
    @JsonBackReference
    private Hackathon hackathon;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitation> invitationList = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "team_members",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> teamMembersList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "team_leader_id")
    private User teamLeader;

    @Column(name = "pending_call_proposal")
    private boolean hasPendingCallProposal;

    private Float grade = (float) 0.00;

    private String writtenEvaluation;

    /**
     * Costruisce un nuovo team con nome, hackathon di riferimento e team leader.
     *
     * @param name il nome del team
     * @param hackathon l'hackathon di riferimento
     * @param teamLeader l'utente designato come leader
     */
    public Team(@NonNull String name, @NonNull Hackathon hackathon, @NonNull User teamLeader) {
        this.name = name;
        this.hackathon = hackathon;
        this.teamLeader = teamLeader;
    }

    /**
     * Adds a {@link User} to the team members list.
     *
     * @param u the user to add; cannot be null
     * @throws IllegalArgumentException if the user is null
     * @throws IllegalStateException if the user is already in the team
     * or if the maximum team size has been reached
     *
     * @author Sonia Bevilacqua
     */
    public void addTeamMember(User u) {
        if (u == null)
            throw new IllegalArgumentException("User cannot be null.");
        System.out.println("DEBUG -> Team: " + this.name);
        System.out.println("DEBUG -> Current members: " + teamMembersList.size());
        System.out.println("DEBUG -> Hackathon limit: " + (hackathon != null ? hackathon.getMaxTeamDimension() : "NULL"));

        if (teamMembersList.contains(u))
            throw new IllegalStateException("User already present in team.");

        if (hackathon != null &&
                teamMembersList.size() >= hackathon.getMaxTeamDimension())
            throw new IllegalStateException("Maximum team size reached.");

        teamMembersList.add(u);
    }

    /**
     * Removes a {@link User} from the team members list.
     *
     * @param u the user to remove; cannot be null
     * @throws IllegalArgumentException if the user is null
     * @throws IllegalStateException if the user is not in the team
     * or if attempting to remove the team leader
     *
     * @author Sonia Bevilacqua
     */
    public void removeTeamMember(User u) {
        if (u == null)
            throw new IllegalArgumentException("Invalid user.");

        if (!teamMembersList.contains(u))
            throw new IllegalStateException("User not present in team.");

        if (u.equals(teamLeader))
            throw new IllegalStateException("Cannot remove the team leader.");

        teamMembersList.remove(u);
    }


    /**
     * Removes an {@link Invitation} from the team invitations list.
     *
     * @param i the invitation to remove; cannot be null
     * @throws IllegalArgumentException if the invitation is null
     * @author Sonia Bevilacqua
     */
    public void removeInvitationFromList(Invitation i) {
        if (i == null)
            throw new IllegalArgumentException("Invitation cannot be null.");

        invitationList.remove(i);
    }

    /**
     * Adds an {@link Invitation} to the team invitations list.
     *
     * @param i the invitation to add; cannot be null
     * @throws IllegalArgumentException if the invitation is null
     * @throws IllegalStateException if the invitation is already in the list
     *
     * @author Sonia Bevilacqua
     */
    public void addInvitation(Invitation i) {
        if (i == null)
            throw new IllegalArgumentException("Invitation cannot be null.");

        if (invitationList.contains(i))
            throw new IllegalStateException("Invitation already present.");

        invitationList.add(i);
    }

    /**
     * Sets the {@link User} as the team leader.
     *
     * @param leader the user who will become leader; cannot be null
     * @throws IllegalArgumentException if the leader is null
     * @throws IllegalStateException if the user is not already a team member
     *
     * @author Sonia Bevilacqua
     */
    public void setTeamLeader(User leader) {
        if (leader == null)
            throw new IllegalArgumentException("Invalid leader.");

        if (!teamMembersList.contains(leader))
            throw new IllegalStateException("The leader must be a team member.");

        this.teamLeader = leader;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(id, team.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}