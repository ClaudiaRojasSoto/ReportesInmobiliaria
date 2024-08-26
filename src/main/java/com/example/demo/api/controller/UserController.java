package com.example.demo.api.controller;

import com.example.demo.api.dto.UserDTO;
import com.example.demo.api.service.UserService;
import com.example.demo.api.service.RoleService; // Importar RoleService
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers().stream()
            .map(user -> {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(user.getId());
                userDTO.setNombre(user.getNombre());
                userDTO.setApellido(user.getApellido());
                userDTO.setUsername(user.getUsername());
                userDTO.setEmail(user.getEmail());
                userDTO.setRoles(user.getRoles().stream()
                    .map(Role::getNombre)
                    .collect(Collectors.toSet()));
                return userDTO;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setNombre(user.getNombre());
            userDTO.setApellido(user.getApellido());
            userDTO.setUsername(user.getUsername());
            userDTO.setEmail(user.getEmail());
            userDTO.setRoles(user.getRoles().stream()
                .map(Role::getNombre)
                .collect(Collectors.toSet()));
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setNombre(userDTO.getNombre());
            user.setApellido(userDTO.getApellido());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());

            Set<Role> updatedRoles = userDTO.getRoles().stream()
                                              .map(roleName -> roleService.findByNombre(roleName)
                                                                          .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                                              .collect(Collectors.toSet());
            user.setRoles(updatedRoles);

            userService.saveUser(user);

            return ResponseEntity.ok("User updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.findUserById(id);
        if (userOptional.isPresent()) {
            userService.deleteUserById(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
