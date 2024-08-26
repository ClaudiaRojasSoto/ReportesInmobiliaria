package com.example.demo.repository;

import com.example.demo.entity.RealEstateProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RealEstateProjectRepository extends JpaRepository<RealEstateProject, Long> {
}
