package com.pp_web_project.security;

import com.pp_web_project.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();
        String password = authentication.getCredentials().toString();

        log.info("[AUTHENTICATION ATTEMPT] User: {}", userId);

        // ✅ 1. 사용자 정보 조회
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(userId);
        } catch (UsernameNotFoundException e) {
            log.warn("[LOGIN FAILED] User: {} - 존재하지 않는 아이디", userId);
            throw new BadCredentialsException("USER_NOT_FOUND");
        }

        // ✅ 2. 로그인 차단된 사용자 즉시 처리
        if (loginAttemptService.isBlocked(userId)) {
            long remainingTime = loginAttemptService.getRemainingBlockTime(userId);
            log.warn("[LOGIN BLOCKED] User: {} - {}분 후 가능", userId, remainingTime);
            throw new BadCredentialsException("BLOCKED");
        }

        // ✅ 3. 비밀번호 검증
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            log.warn("[LOGIN FAILED] User: {} - 비밀번호 불일치", userId);
            loginAttemptService.loginFailed(userId);
            throw new BadCredentialsException("WRONG_PASSWORD");
        }

        log.info("[LOGIN SUCCESS] User: {}", userId);
        loginAttemptService.loginSucceeded(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
