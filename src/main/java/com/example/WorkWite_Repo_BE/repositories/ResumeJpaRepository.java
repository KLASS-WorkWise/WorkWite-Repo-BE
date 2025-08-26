package com.example.WorkWite_Repo_BE.repositories;

import com.example.WorkWite_Repo_BE.entities.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumeJpaRepository extends JpaRepository<Resume, Long> {
    //3 cái test thử
//    @Query("SELECT r FROM Resume r LEFT JOIN FETCH r.educations LEFT JOIN FETCH r.awards LEFT JOIN FETCH r.Activities WHERE r.id = :id")
//    Resume findResumeWithChildrenById(@Param("id") Long id);
    // join nhiều lỗi
    //+ sửa list thành set
    // tách query ra từng cái riêng trong repository khác nhau
    // rooif qua service lâ

    List<Resume> findByCandidateId(Long candidateId);

}
