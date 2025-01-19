package com.pp_web_project.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class AdminController {
    @GetMapping("/login")
    public String login() {
        log.info("Login page requested");
        return "html/login/login"; // ✅ `resources/templates/login/login.html`을 반환
    }
}

