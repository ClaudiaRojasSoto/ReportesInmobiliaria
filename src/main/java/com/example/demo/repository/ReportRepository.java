package com.example.demo.repository;

import com.example.demo.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProjectId(Long projectId);
    List<Report> findByUserId(Long userId);
    List<Report> findByProjectIdAndUserId(Long projectId, Long userId);

}
