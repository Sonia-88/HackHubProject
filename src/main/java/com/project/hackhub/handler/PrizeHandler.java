/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */
 
 package com.project.hackhub.handler;
 

import com.project.hackhub.model.hackathon.Hackathon;
import com.project.hackhub.model.hackathon.Money;
import com.project.hackhub.model.hackathon.state.HackathonStateType;
import com.project.hackhub.model.team.Team;
import com.project.hackhub.model.user.User;
import com.project.hackhub.repository.HackathonRepository;
import com.project.hackhub.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Gestisce il riscatto e la distribuzione dei premi in denaro per i membri dei team vincitori.
 */
@Component
@RequiredArgsConstructor
public class PrizeHandler {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;

    /**
     * Riscatta il premio in denaro per un membro del team vincitore, dividendolo equamente
     * e simulando la transazione con un sistema di pagamento esterno.
     *
     * @param userId l'identificatore dell'utente che richiede il riscatto
     * @param hackathonId l'identificatore dell'hackathon
     * @author Cosmina Androne
     */
    @Transactional
    public void claimPrize(UUID userId, UUID hackathonId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Hackathon hackathon = hackathonRepository.findById(hackathonId)
                .orElseThrow(() -> new IllegalArgumentException("Hackathon not found"));

        if (hackathon.getState().getStateType() != HackathonStateType.CONCLUDED) {
            throw new IllegalStateException("Hackathon has not concluded yet");
        }

        Team winner = hackathon.getWinner();
        if (winner == null) {
            throw new IllegalStateException("No winning team proclaimed for this hackathon");
        }

        if (!winner.getTeamMembersList().contains(user)) {
            throw new UnsupportedOperationException("Only members of the winning team can collect the prize");
        }

        Money totalPrize = hackathon.getMoneyPrize();
        double totalAmount = totalPrize.getQuantity();
        int teamSize = winner.getTeamMembersList().size();

        BigDecimal amountPerMember = BigDecimal.valueOf(totalAmount)
                .divide(BigDecimal.valueOf(teamSize), 2, RoundingMode.HALF_UP);

        boolean paymentSuccessful = externalPaymentService(user, amountPerMember);

        if (!paymentSuccessful) {
            throw new RuntimeException("Payment failed. Inserted data is not valid");
        }
    }

    /**
     * Simula l'interazione con un sistema di pagamento esterno.
     */
    private boolean externalPaymentService(User user, BigDecimal amount) {
        return true;
    }
}