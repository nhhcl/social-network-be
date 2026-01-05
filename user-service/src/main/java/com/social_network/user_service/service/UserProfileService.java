package com.social_network.user_service.service;

import com.social_network.user_service.entity.UserProfile;
import com.social_network.user_service.kafka.dto.CreateUserRequest;
import com.social_network.user_service.repository.UserProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void createUserProfile(CreateUserRequest request) {
        log.info("Creating user profile for accountId: {}", request.getAccountId());

        if (userProfileRepository.existsByAccountId(request.getAccountId())) {
            log.warn("User profile already exists for accountId: {}", request.getAccountId());
            return;
        }
        UserProfile userProfile = UserProfile.builder()
                .accountId(request.getAccountId())
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();
        userProfileRepository.save(userProfile);
        log.info("✅ Created UserProfile for accountId={}", request.getAccountId());
    }

}
