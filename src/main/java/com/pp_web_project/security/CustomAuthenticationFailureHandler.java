package com.pp_web_project.security;

import com.pp_web_project.service.LoginAttemptService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String userId = request.getParameter("userId");

        HttpSession session = request.getSession();
        session.removeAttribute("failCount"); // ✅ 이전 실패 카운트 제거
        session.removeAttribute("blocked"); // ✅ 차단 메시지 제거
        session.removeAttribute("hideFailCount"); // ✅ 이전 숨김 설정 제거
        session.removeAttribute("errorMessage"); // ✅ 기존 메시지 제거

        if ("USER_NOT_FOUND".equals(exception.getMessage())) {
            session.setAttribute("errorMessage", "아이디 또는 비밀번호가 존재하지 않습니다.");
            session.setAttribute("hideFailCount", true);  // ✅ 실패 횟수 숨김
        } else if ("BLOCKED".equals(exception.getMessage())) {
            long remainingTime = loginAttemptService.getRemainingBlockTime(userId);
            session.setAttribute("errorMessage", "계정이 차단되었습니다. " + remainingTime + "분 후 다시 시도하세요.");
            session.setAttribute("blocked", true);
            session.setAttribute("hideFailCount", true);  // ✅ 실패 횟수 숨김
        } else if ("WRONG_PASSWORD".equals(exception.getMessage())) {
            int failCount = loginAttemptService.getFailedAttempts(userId);
            session.setAttribute("errorMessage", "아이디 또는 비밀번호가 일치하지 않습니다. (" + failCount + "/5) 5회 실패 시 계정이 5분간 차단됩니다.");
            session.setAttribute("failCount", failCount);
            session.setAttribute("hideFailCount", false);  // ✅ 실패 횟수 표시
        }

        response.sendRedirect("/login");
    }
}
