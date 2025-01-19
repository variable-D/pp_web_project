package com.pp_web_project.service.handler;

import com.pp_web_project.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String userId = request.getParameter("userId"); // 로그인 폼에서 userId 가져오기
        loginAttemptService.loginSucceeded(userId); // 로그인 성공 시 실패 횟수 초기화

        response.sendRedirect("/main/index"); // ✅ 변경된 경로
    }
}

