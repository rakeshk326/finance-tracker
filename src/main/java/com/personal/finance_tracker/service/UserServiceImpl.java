package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.user.SignupRequestDTO;
import com.personal.finance_tracker.dto.user.SignupResponseDTO;
import com.personal.finance_tracker.entity.User;
import com.personal.finance_tracker.exception.ResourceAlreadyExistsException;
import com.personal.finance_tracker.mapper.UserMapper;
import com.personal.finance_tracker.repository.UserRepository;
import com.personal.finance_tracker.utils.JwtUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Override
    @Transactional
    public SignupResponseDTO createUser(SignupRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtils.generateToken(savedUser);

        SignupResponseDTO response = userMapper.toSignupResponseDTO(savedUser);
        response.setToken(token);
        return response;
    }
}