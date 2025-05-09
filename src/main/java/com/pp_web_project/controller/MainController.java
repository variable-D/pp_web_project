package com.pp_web_project.controller;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/admin")  // ✅ 관리자 메인 페이지
    public String adminIndex(Model model) {
        return getAdminPage(model);
    }

    @GetMapping("/user")  // ✅ 일반 사용자 메인 페이지
    public String userIndex(Model model) {
        return getUserPage(model);
    }

    @GetMapping("/")  // ✅ 루트 URL 처리
    public RedirectView handleRoot(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new RedirectView("/login");  // ✅ 로그인되지 않았으면 로그인 페이지로 이동
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return new RedirectView("/admin");  // ✅ 관리자라면 `/admin`으로 이동
            } else if (authority.getAuthority().equals("ROLE_USER")) {
                return new RedirectView("/user");  // ✅ 일반 사용자라면 `/user`로 이동
            }
        }
        return new RedirectView("/login");  // ✅ 혹시라도 권한이 없으면 다시 로그인 페이지로
    }

    private String getAdminPage(Model model) {
        List<Map<String, String>> menuList = new ArrayList<>();
        String logo = "eSIM";

        menuList.add(Map.of("name", "정산 실적 조회", "url", "/admin/sales/performance")); // ✅ `/sales/performance`로 구체화
        menuList.add(Map.of("name", "판매 실적 조회", "url", "/admin/sales/retail")); // ✅ `/sales/retail`로 명확하게 정리

        menuList.add(Map.of("name", "Joytel 재고 현황", "url", "/admin/joytel/products/inventory")); // ✅ inventory가 더 자연스러움
        menuList.add(Map.of("name", "Joytel 유효 기간 10일 이하 상품 목록", "url", "/admin/joytel/products/expiring")); // ✅ products 리소스 명시
        menuList.add(Map.of("name", "Joytel 판매 완료 상품 목록", "url", "/admin/joytel/products/sold")); // ✅ products 리소스 명시
        menuList.add(Map.of("name", "SK 판매 완료 상품 목록", "url", "/admin/sk/products/sold")); // ✅ products 리소스 명시
        menuList.add(Map.of("name", "SK eSIM 상품 상세정보 조회", "url","/admin/sk/products/eSIMDetails")); // ✅ products 리소스 명시

        model.addAttribute("logo", logo);
        model.addAttribute("menuList", menuList);
        return "admin/main/index";  // ✅ Spring Boot는 `templates/` 기준으로 찾음
    }

    private String getUserPage(Model model) {
        List<Map<String, String>> menuList = new ArrayList<>();
        String logo = "eSIM";

        menuList.add(Map.of("name", "정산 실적 조회", "url", "/user/sales/performance")); // ✅ `/sales/performance`로 구체화
        menuList.add(Map.of("name", "판매 실적 조회", "url", "/user/sales/retail")); // ✅ `/sales/retail`로 명확하게 정리
        ;

        model.addAttribute("logo", logo);
        model.addAttribute("menuList", menuList);
        return "user/main/index";  // ✅ Spring Boot는 `templates/` 기준으로 찾음
    }
}
