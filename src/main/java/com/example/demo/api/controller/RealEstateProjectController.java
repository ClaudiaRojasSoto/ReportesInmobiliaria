package com.example.demo.api.controller;

import com.example.demo.api.dto.RealEstateProjectDTO;
import com.example.demo.api.service.RealEstateProjectService;
import com.example.demo.entity.RealEstateProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class RealEstateProjectController {

    @Autowired
    private RealEstateProjectService projectService;

    @GetMapping
    public ResponseEntity<List<RealEstateProjectDTO>> getAllProjects() {
        List<RealEstateProjectDTO> projects = projectService.findAllProjects().stream()
            .map(project -> {
                RealEstateProjectDTO projectDTO = new RealEstateProjectDTO();
                projectDTO.setId(project.getId());
                projectDTO.setName(project.getName());
                projectDTO.setDescription(project.getDescription());
                return projectDTO;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RealEstateProjectDTO> getProjectById(@PathVariable Long id) {
        Optional<RealEstateProject> projectOptional = projectService.findProjectById(id);
        if (projectOptional.isPresent()) {
            RealEstateProject project = projectOptional.get();
            RealEstateProjectDTO projectDTO = new RealEstateProjectDTO();
            projectDTO.setId(project.getId());
            projectDTO.setName(project.getName());
            projectDTO.setDescription(project.getDescription());
            return ResponseEntity.ok(projectDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<RealEstateProjectDTO> createProject(@RequestBody RealEstateProjectDTO projectDTO) {
        RealEstateProject project = new RealEstateProject();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        RealEstateProject savedProject = projectService.saveProject(project);
        projectDTO.setId(savedProject.getId());
        return ResponseEntity.ok(projectDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RealEstateProjectDTO> updateProject(@PathVariable Long id, @RequestBody RealEstateProjectDTO projectDTO) {
        Optional<RealEstateProject> projectOptional = projectService.findProjectById(id);
        if (projectOptional.isPresent()) {
            RealEstateProject project = projectOptional.get();
            project.setName(projectDTO.getName());
            project.setDescription(projectDTO.getDescription());
            RealEstateProject updatedProject = projectService.saveProject(project);
            projectDTO.setId(updatedProject.getId());
            return ResponseEntity.ok(projectDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        Optional<RealEstateProject> projectOptional = projectService.findProjectById(id);
        if (projectOptional.isPresent()) {
            projectService.deleteProject(id);
            return ResponseEntity.ok("Project deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
