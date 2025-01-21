package com.pp_web_project.controller;

import com.pp_web_project.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginAttemptService loginAttemptService;

    @GetMapping("/login")
    public String loginPage(HttpServletRequest request, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ✅ 로그인된 사용자가 "/login"으로 접근하면 "/main/index"로 리다이렉트
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return "redirect:/main/index";
        }

        // ✅ 로그인 실패 횟수 가져오기
        String userId = request.getParameter("userId");
        String failCountParam = request.getParameter("failCount");

        int failCount = (failCountParam != null) ? Integer.parseInt(failCountParam) : 0;
        model.addAttribute("failCount", failCount);

        if (userId != null && loginAttemptService.isBlocked(userId)) {
            long remainingTime = loginAttemptService.getRemainingBlockTime(userId);
            model.addAttribute("blocked", true);
            model.addAttribute("remainingTime", remainingTime);
        } else {
            model.addAttribute("blocked", false);
        }

        return "html/login/login";
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
