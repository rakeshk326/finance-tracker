package com.personal.finance_tracker.mapper;

import com.personal.finance_tracker.dto.user.LoginResponseDTO;
import com.personal.finance_tracker.dto.user.SignupRequestDTO;
import com.personal.finance_tracker.dto.user.SignupResponseDTO;
import com.personal.finance_tracker.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(SignupRequestDTO dto);

    @Mapping(target = "token", ignore = true)
    SignupResponseDTO toSignupResponseDTO(User user);

    @Mapping(target = "token", ignore = true)
    LoginResponseDTO toLoginResponseDTO(User user);
}