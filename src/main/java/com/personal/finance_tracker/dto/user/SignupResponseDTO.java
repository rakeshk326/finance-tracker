package com.personal.finance_tracker.dto.user;

import lombok.Data;
import java.util.UUID;

@Data
public class SignupResponseDTO {
    private UUID id;
    private String name;
    private String mobile;
    private String email;
    private String token;
}