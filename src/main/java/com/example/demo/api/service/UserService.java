package com.example.demo.api.service;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> userRole = roleRepository.findById(1L);
        Set<Role> roles = new HashSet<>();
        userRole.ifPresent(roles::add);
        user.setRoles(roles);

        return userRepository.save(user);
    }
    
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateUserRoles(Long id, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Role> roles = new HashSet<>();
        for (String roleName : roleNames) {
        	Role role = roleRepository.findByNombre(roleName)
        	        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
            roles.add(role);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }
    
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean isAdmin(String username) {
        Optional<User> user = findByUsername(username);
        if (user.isPresent()) {
            return user.get().getRoles().stream().anyMatch(role -> role.getNombre().equalsIgnoreCase("ADMIN"));
        }
        return false;
    }

 }

