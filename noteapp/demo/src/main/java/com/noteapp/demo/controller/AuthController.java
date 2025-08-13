package com.noteapp.demo.controller;

import com.noteapp.demo.dto.AuthResponse;
import com.noteapp.demo.dto.LoginRequest;
import com.noteapp.demo.dto.RegisterRequest;
import com.noteapp.demo.model.User;
import com.noteapp.demo.repository.UserRepository;
import com.noteapp.demo.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            return ResponseEntity.badRequest().build();
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtil.generateToken(request.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }


}
