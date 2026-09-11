/*
 * Copyright (c) 2026 Sonia Bevilacqua e Cosmina Androne
 * SPDX-License-Identifier: MIT
 */

 package com.project.hackhub.repository;


import com.project.hackhub.model.hackathon.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository Spring Data JPA per la gestione dei task associati agli hackathon.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
}