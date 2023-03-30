package org.brainstorm.repository;

import org.brainstorm.model.Session;
import org.brainstorm.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session,Long> {
}
