package org.brainstorm.repository;

import org.brainstorm.instant.Status;
import org.brainstorm.model.Session;
import org.brainstorm.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface SessionRepository extends JpaRepository<Session,Long> {
    @Modifying
    @Query("update Session s set s.status=?2 where s.id=?1")
    @Transactional
    void updateStatusById(Long id, Status status);
}
