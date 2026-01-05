package com.social.network.auth_service.service;

import com.social.network.auth_service.entity.AccountEntity;
import com.social.network.auth_service.entity.RoleEntity;
import com.social.network.auth_service.entity.TokenEntity;
import com.social.network.auth_service.exception.BaseAppException;
import com.social.network.auth_service.exception.ErrorCode;
import com.social.network.auth_service.repository.TokenRepository;
import com.social.network.auth_service.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;

    public TokenEntity createToken(AccountEntity account, String tokenValue, String type, int minutesValid) {
        TokenEntity token = new TokenEntity();
        token.setAccount(account);
        token.setToken(tokenValue);
        token.setType(type);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(minutesValid));
        token.setRevoked(false);
        return tokenRepository.save(token);
    }


    public String refreshAccessToken(String refreshToken) {
        TokenEntity tokenEntity = tokenRepository.findByToken(refreshToken).orElseThrow(() -> new BaseAppException(ErrorCode.REFRESH_TOKEN_EXPIRED));
        if (tokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BaseAppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        AccountEntity account = tokenEntity.getAccount();
        List<String> roles = account.getRoles().stream()
                .map(RoleEntity::getName)
                .toList();
        return jwtUtil.generateAccessToken(account.getUsername(), roles);
    }

    public void revokeToken(String tokenValue) {
        tokenRepository.findByToken(tokenValue).ifPresent(t -> {
            t.setRevoked(true);
            tokenRepository.save(t);
        });
    }
}