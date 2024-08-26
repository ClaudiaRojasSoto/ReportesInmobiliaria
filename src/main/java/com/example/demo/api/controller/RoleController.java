package com.example.demo.api.controller;

import com.example.demo.api.dto.RoleDTO;
import com.example.demo.api.service.RoleService;
import com.example.demo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        List<RoleDTO> roles = roleService.findAllRoles().stream()
            .map(role -> {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setNombre(role.getNombre());
                return roleDTO;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDTO) {
        Role role = new Role();
        role.setNombre(roleDTO.getNombre());
        Role savedRole = roleService.saveRole(role);
        roleDTO.setId(savedRole.getId());
        return ResponseEntity.ok(roleDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        Optional<Role> roleOptional = roleService.findById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setNombre(role.getNombre());
            return ResponseEntity.ok(roleDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> updateRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        Optional<Role> roleOptional = roleService.findById(id);
        if (roleOptional.isPresent()) {
            Role role = roleOptional.get();
            role.setNombre(roleDTO.getNombre());
            Role updatedRole = roleService.saveRole(role);
            roleDTO.setId(updatedRole.getId());
            return ResponseEntity.ok(roleDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        Optional<Role> roleOptional = roleService.findById(id);
        if (roleOptional.isPresent()) {
            roleService.deleteRole(id);
            return ResponseEntity.ok("Role deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
