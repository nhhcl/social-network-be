package com.social_network.user_service.kafka.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    private Long accountId;
    private String fullName;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
}
