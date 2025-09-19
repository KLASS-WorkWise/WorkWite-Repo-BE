package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    // Lấy log theo khoảng thời gian
    List<SystemLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Lấy log theo actor
    List<SystemLog> findByActor(String actor);

    // Lấy log theo level
    List<SystemLog> findByLevel(String level);

    // Tìm kiếm log theo action
    List<SystemLog> findByAction(String action);

    // Tìm kiếm log theo nhiều tiêu chí
    @Query("SELECT l FROM SystemLog l WHERE (:actor IS NULL OR l.actor = :actor) AND (:level IS NULL OR l.level = :level) AND (:action IS NULL OR l.action = :action) AND (:start IS NULL OR l.timestamp >= :start) AND (:end IS NULL OR l.timestamp <= :end)")
    List<SystemLog> searchLogs(@Param("actor") String actor,
            @Param("level") String level,
            @Param("action") String action,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
