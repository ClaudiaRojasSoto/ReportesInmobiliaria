package com.example.demo.api.controller;

import com.example.demo.api.dto.ReportDTO;
import com.example.demo.api.service.ReportService;
import com.example.demo.entity.Report;
import com.example.demo.entity.RealEstateProject;
import com.example.demo.entity.User;
import com.example.demo.api.service.RealEstateProjectService;
import com.example.demo.api.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @Autowired
    private RealEstateProjectService projectService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    
    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports() {
        if (!isAdmin()) {
            return ResponseEntity.status(403).build();
        }

        List<ReportDTO> reports = reportService.findAllReports().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<ReportDTO>> getReportsByProject(@PathVariable Long projectId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userService.findByUsername(username);

        logger.debug("Usuario autenticado: {}", username);

        if (!currentUser.isPresent()) {
            logger.error("Usuario no encontrado: {}", username);
            return ResponseEntity.status(403).build();
        }

        List<Report> reports;

        if (isAdmin()) {
            logger.debug("Usuario es admin, obteniendo todos los reportes para el proyecto: {}", projectId);
            reports = reportService.getReportsByProjectId(projectId);
        } else {
            logger.debug("Usuario no es admin, obteniendo reportes del usuario {} para el proyecto: {}", currentUser.get().getId(), projectId);
            reports = reportService.getReportsByProjectId(projectId)
                .stream()
                .filter(report -> report.getUser().getId().equals(currentUser.get().getId()))
                .collect(Collectors.toList());
        }

        if (reports.isEmpty()) {
            logger.warn("No se encontraron reportes para el proyecto: {} y usuario: {}", projectId, currentUser.get().getId());
        } else {
            logger.debug("Se encontraron {} reportes para el proyecto: {}", reports.size(), projectId);
        }

        List<ReportDTO> reportDTOs = reports.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return ResponseEntity.ok(reportDTOs);
    }



    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportDTO>> getReportsByUser(@PathVariable Long userId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userService.findByUsername(username);

        if (!currentUser.isPresent() || (!currentUser.get().getId().equals(userId) && !isAdmin())) {
            return ResponseEntity.status(403).build();
        }

        List<ReportDTO> reports = reportService.getReportsByUserId(userId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    @PostMapping
    public ResponseEntity<ReportDTO> createReport(@RequestBody ReportDTO reportDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userService.findByUsername(username);

        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }

        Report report = new Report();
        report.setTitle(reportDTO.getTitle());
        report.setContent(reportDTO.getContent());

        Optional<RealEstateProject> projectOptional = projectService.findProjectById(reportDTO.getProjectId());
        if (!projectOptional.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        report.setProject(projectOptional.get());
        report.setUser(userOptional.get());

        Report savedReport = reportService.saveReport(report);
        reportDTO.setId(savedReport.getId());
        reportDTO.setUserId(userOptional.get().getId());
        return ResponseEntity.ok(reportDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportDTO> updateReport(@PathVariable Long id, @RequestBody ReportDTO reportDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Report> reportOptional = reportService.findById(id);

        if (!reportOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Report report = reportOptional.get();
        if (!report.getUser().getUsername().equals(username) && !isAdmin()) {
            return ResponseEntity.status(403).build();
        }

        report.setTitle(reportDTO.getTitle());
        report.setContent(reportDTO.getContent());

        Optional<RealEstateProject> projectOptional = projectService.findProjectById(reportDTO.getProjectId());
        if (!projectOptional.isPresent()) {
            return ResponseEntity.badRequest().body(null);
        }
        report.setProject(projectOptional.get());

        Report updatedReport = reportService.saveReport(report);
        reportDTO.setId(updatedReport.getId());
        return ResponseEntity.ok(reportDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Report> reportOptional = reportService.findById(id);

        if (!reportOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Report report = reportOptional.get();
        if (!report.getUser().getUsername().equals(username) && !isAdmin()) {
            return ResponseEntity.status(403).build();
        }

        reportService.deleteReport(id);
        return ResponseEntity.ok("Report deleted successfully.");
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private ReportDTO convertToDTO(Report report) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setTitle(report.getTitle());
        reportDTO.setContent(report.getContent());
        reportDTO.setProjectId(report.getProject().getId());
        reportDTO.setUserId(report.getUser().getId());
        return reportDTO;
    }
}
