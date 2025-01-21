package com.pp_web_project.security;

import com.pp_web_project.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String userId = request.getParameter("userId");

        if (userId != null) {
            loginAttemptService.loginFailed(userId);
            int failCount = loginAttemptService.getFailedAttempts(userId);

            log.info("[LOGIN FAILURE HANDLER] User: {}, Fail Count: {}", userId, failCount); // ✅ 로그 추가

            String encodedUserId = URLEncoder.encode(userId, StandardCharsets.UTF_8);
            response.sendRedirect("/login?error=true&userId=" + encodedUserId + "&failCount=" + failCount);
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
