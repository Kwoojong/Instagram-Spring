package com.numble.instagram.application.auth;

import com.numble.instagram.application.auth.token.RefreshToken;
import com.numble.instagram.application.auth.token.RefreshTokenProvider;
import com.numble.instagram.application.auth.token.RefreshTokenRepository;
import com.numble.instagram.application.auth.token.TokenProvider;
import com.numble.instagram.domain.user.User;
import com.numble.instagram.domain.user.repository.UserRepository;
import com.numble.instagram.dto.common.LoginDto;
import com.numble.instagram.exception.notfound.UserNotFoundException;
import com.numble.instagram.exception.unauthorized.PasswordMismatchException;
import com.numble.instagram.exception.unauthorized.RefreshTokenExpiredException;
import com.numble.instagram.exception.unauthorized.RefreshTokenNotExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenProvider refreshTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginDto login(String nickname, String password) {
        User loginUser = userRepository.findByNickname(nickname)
                .orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(password, loginUser.getPassword())) {
            throw new PasswordMismatchException();
        }

        Long userId = loginUser.getId();
        String accessToken = tokenProvider.createAccessToken(userId);
        RefreshToken refreshToken = refreshTokenProvider.createToken(userId);
        refreshTokenRepository.save(refreshToken);
        return LoginDto.create(accessToken, refreshToken.tokenValue());
    }

    @Transactional
    public LoginDto reissueToken(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findTokenByTokenValue(refreshTokenValue)
                .orElseThrow(RefreshTokenNotExistsException::new);

        validateExpired(refreshToken);

        Long userId = refreshToken.userId();
        String newAccessToken = tokenProvider.createAccessToken(userId);
        RefreshToken newRefreshToken = refreshTokenProvider.createToken(userId);

        refreshTokenRepository.delete(refreshTokenValue);
        refreshTokenRepository.save(newRefreshToken);
        return LoginDto.create(newAccessToken, newRefreshToken.tokenValue());
    }

    private void validateExpired(RefreshToken refreshToken) {
        if (refreshToken.isExpired()) {
            refreshTokenRepository.delete(refreshToken.tokenValue());
            throw new RefreshTokenExpiredException();
        }
    }
}
