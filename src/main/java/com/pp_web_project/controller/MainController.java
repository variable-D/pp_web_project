package com.pp_web_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/main/index")  // ✅ 로그인 성공 후 이동할 경로 매핑
    public String index() {
        return "html/main/index"; // ✅ `resources/templates/html/main/index.html`을 반환
    }
}
