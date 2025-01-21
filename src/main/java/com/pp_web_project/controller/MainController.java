package com.pp_web_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @GetMapping("/main/index")  // ✅ 로그인 성공 후 이동할 경로 매핑
    public String index(Model model) {
        return getString(model);
    }


    @GetMapping("/")
    public String main( Model model) {

        return getString(model);
    }

    private String getString(Model model) {
        List<Map<String, String>> menuList = new ArrayList<>();
        String logo = "eSIM";

        menuList.add(Map.of("name", "실적 조회", "url", "/salesPerformance"));
        menuList.add(Map.of("name", "Joytel 상품 현황", "url", "/joytelProducts"));
        menuList.add(Map.of("name", "판매된 상품", "url", "/soldProducts"));



        model.addAttribute("logo", logo);
        model.addAttribute("menuList", menuList);
        return "html/main/index"; // ✅ `resources/templates/main/index.html`을 반환 (경로 수정)
    }
}
