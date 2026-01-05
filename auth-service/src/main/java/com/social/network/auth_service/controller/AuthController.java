package com.social.network.auth_service.controller;

import com.social.network.auth_service.dto.request.LoginRequest;
import com.social.network.auth_service.dto.request.RegisterRequest;
import com.social.network.auth_service.dto.response.BaseResponse;
import com.social.network.auth_service.dto.response.LoginResponse;
import com.social.network.auth_service.entity.AccountEntity;
import com.social.network.auth_service.service.AuthService;
import com.social.network.auth_service.utils.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<?>> login(
            @RequestBody LoginRequest request
            ) {
        LoginResponse data = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(ResponseUtil.success(data,"Login successful"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<BaseResponse<Map<String, String>>> refreshToken(@RequestBody Map<String, String> request) {
        String accessToken = authService.refreshAccessToken(request.get("refreshToken"));
        Map<String, String> data = Map.of("accessToken", accessToken);
        return ResponseEntity.ok(ResponseUtil.success(data, "Refresh token successful"));
    }

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<Map<String, String>>> registerUser(@RequestBody RegisterRequest request) {
        AccountEntity user = authService.register(request);
        Map<String, String> data = Map.of("username", user.getUsername());
        return ResponseEntity.ok(ResponseUtil.success(data, "User registered successfully"));
    }
}

