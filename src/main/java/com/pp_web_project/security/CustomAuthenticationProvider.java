package com.pp_web_project.security;

import com.pp_web_project.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userId = authentication.getName();
        String password = authentication.getCredentials().toString();

        if (loginAttemptService.isBlocked(userId)) {
            throw new BadCredentialsException("계정이 잠겼습니다. 나중에 다시 시도하세요.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            loginAttemptService.loginFailed(userId);
            int failCount = loginAttemptService.getFailedAttempts(userId);
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다. (실패 횟수: " + failCount + ")");
        }

        loginAttemptService.loginSucceeded(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
