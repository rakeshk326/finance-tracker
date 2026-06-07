package com.personal.finance_tracker.controller;

import com.personal.finance_tracker.dto.user.LoginRequestDTO;
import com.personal.finance_tracker.dto.user.LoginResponseDTO;
import com.personal.finance_tracker.dto.user.SignupRequestDTO;
import com.personal.finance_tracker.dto.user.SignupResponseDTO;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.mapper.UserMapper;
import com.personal.finance_tracker.repository.UserRepository;
import com.personal.finance_tracker.service.UserService;
import com.personal.finance_tracker.utils.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDTO> createUser(@RequestBody @Valid SignupRequestDTO signupRequestDTO) {
        return ResponseEntity.ok(userService.createUser(signupRequestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()));
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtils.generateToken(user);

        LoginResponseDTO response = userMapper.toLoginResponseDTO(user);
        response.setToken(token);

        return ResponseEntity.ok(response);
    }

}