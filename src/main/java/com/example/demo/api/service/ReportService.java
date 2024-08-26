package com.example.demo.api.service;

import com.example.demo.entity.Report;
import com.example.demo.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserService userService;

    public List<Report> getReportsByProjectId(Long projectId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        if (userService.isAdmin(username)) {
            return reportRepository.findByProjectId(projectId);
        } else {
            Long userId = userService.findByUsername(username).get().getId();
            return reportRepository.findByProjectIdAndUserId(projectId, userId);
        }
    }

    public List<Report> getReportsByUserId(Long userId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Long authenticatedUserId = userService.findByUsername(username).get().getId();

        if (!authenticatedUserId.equals(userId) && !userService.isAdmin(username)) {
            throw new SecurityException("Access denied");
        }

        return reportRepository.findByUserId(userId);
    }

    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    public Optional<Report> findById(Long id) {
        return reportRepository.findById(id);
    }

    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }

    public List<Report> findAllReports() {
        return reportRepository.findAll();
    }
}
