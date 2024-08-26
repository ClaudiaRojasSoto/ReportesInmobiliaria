package com.example.demo.api.service;

import com.example.demo.entity.RealEstateProject;
import com.example.demo.repository.RealEstateProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RealEstateProjectService {

    @Autowired
    private RealEstateProjectRepository realEstateProjectRepository;

    public List<RealEstateProject> findAllProjects() {
        return realEstateProjectRepository.findAll();
    }

    public Optional<RealEstateProject> findProjectById(Long id) {
        return realEstateProjectRepository.findById(id);
    }

    public RealEstateProject saveProject(RealEstateProject project) {
        return realEstateProjectRepository.save(project);
    }

    public void deleteProject(Long id) {
        realEstateProjectRepository.deleteById(id);
    }
}
