package com.pp_web_project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "bgfretail@@!"; // ✅ 여기에 원하는 비밀번호 입력
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println("원본 비밀번호: " + rawPassword);
        System.out.println("암호화된 비밀번호: " + encodedPassword);
    }
}
