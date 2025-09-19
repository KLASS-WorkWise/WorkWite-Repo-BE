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

    // Lấy log theo trạng thái (SUCCESS/FAIL)
    List<SystemLog> findByStatus(String status);

    // Tìm kiếm log theo ngày và trạng thái
    @Query("SELECT l FROM SystemLog l WHERE (:actor IS NULL OR l.username = :actor) AND (:status IS NULL OR l.status = :status) AND (:start IS NULL OR l.timestamp >= :start) AND (:end IS NULL OR l.timestamp <= :end)")
    List<SystemLog> searchLogs(@Param("actor") String actor,
        @Param("status") String status,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end);
}
