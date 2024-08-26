package com.example.demo.api.controller;

import com.example.demo.api.dto.UserDTO;
import com.example.demo.api.dto.UserRegistrationDTO;
import com.example.demo.api.service.UserService;
import com.example.demo.api.service.RoleService; 
import com.example.demo.api.util.JWTUtil;
import com.example.demo.entity.User;
import com.example.demo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private RoleService roleService; 

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationDTO userDTO) {
        if (!userService.existsByUsername(userDTO.getUsername())) {
            User user = new User();
            user.setNombre(userDTO.getNombre());
            user.setApellido(userDTO.getApellido());
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());

            Role userRole = roleService.findByNombre("USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));
            user.setRoles(Collections.singleton(userRole));
            
            userService.saveUser(user);
            return ResponseEntity.ok("User registered successfully!");
        } else {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
    }





    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String username = authentication.getName();
        List<String> roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        String token = jwtUtil.generateToken(username, roles);
        return ResponseEntity.ok(token);
    }

}

class LoginRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
