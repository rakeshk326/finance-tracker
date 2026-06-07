package com.personal.finance_tracker.dto.user;

import lombok.Data;
import java.util.UUID;

@Data
public class LoginResponseDTO {

    private UUID id;
    private String email;
    private String token;
}
