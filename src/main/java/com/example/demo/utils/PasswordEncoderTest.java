package com.example.demo.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "100";//Para generar el password encriptado
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);
    }
}
