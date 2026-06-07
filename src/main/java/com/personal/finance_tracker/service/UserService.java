package com.personal.finance_tracker.service;

import com.personal.finance_tracker.dto.user.SignupRequestDTO;
import com.personal.finance_tracker.dto.user.SignupResponseDTO;

public interface UserService {
    SignupResponseDTO createUser(SignupRequestDTO dto);
}
