package com.pp_web_project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;

@Controller
public class RedirectController {

    @GetMapping("/redirect")
    public RedirectView redirectUserBasedOnRole(Authentication authentication) {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return new RedirectView("/admin");  // ✅ 관리자 페이지로 이동
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                return new RedirectView("/user");  // ✅ 일반 사용자는 `/main/index`로 이동
            }
        }
        return new RedirectView("/");  // ✅ 기본 홈 페이지로 이동
    }
}
