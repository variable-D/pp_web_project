package com.pp_web_project.controller;

import com.pp_web_project.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginAttemptService loginAttemptService;

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model, HttpSession session) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅ 로그인된 사용자가 "/login"으로 접근하면 등급별 페이지로 리다이렉트
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                    return "redirect:/admin";
                } else if ("ROLE_USER".equals(authority.getAuthority())) {
                    return "redirect:/user";
                }
            }
            return "redirect:/"; // ✅ 등급이 없을 경우 기본 홈으로 리다이렉트
        }

        // ✅ 세션에서 오류 메시지 가져오기 (한 번 가져온 후 삭제하여 중복 표시 방지)
        String errorMessage = (String) session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage"); // ✅ 가져온 후 삭제
        }

        return "login/login"; // ✅ 로그인 페이지 반환
    }




    @GetMapping("/login-fail")
    public String loginFail(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String userId = request.getParameter("userId");

        if (userId != null) {
            int failCount = loginAttemptService.getFailedAttempts(userId);

            // ✅ FlashAttribute → URL Parameter 전달
            redirectAttributes.addAttribute("failCount", failCount);
            redirectAttributes.addAttribute("userId", userId);

            if (loginAttemptService.isBlocked(userId)) {
                long remainingTime = loginAttemptService.getRemainingBlockTime(userId);
                redirectAttributes.addAttribute("blocked", true);
                redirectAttributes.addAttribute("remainingTime", remainingTime);
            }
        } else {
            redirectAttributes.addAttribute("failCount", 0);
            redirectAttributes.addAttribute("blocked", false);
        }

        return "redirect:/login?error";  // ✅ URL 파라미터 유지
    }
}
