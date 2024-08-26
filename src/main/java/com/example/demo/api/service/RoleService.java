package com.example.demo.api.service;

import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Optional<Role> findByNombre(String nombre) {
        return roleRepository.findByNombre(nombre);
    }

    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }
    
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }
    
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

}

