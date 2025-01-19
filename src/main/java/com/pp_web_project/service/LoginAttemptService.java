package com.pp_web_project.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5; // 최대 로그인 실패 횟수
    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();

    public void loginFailed(String userId) {
        int attempts = attemptsCache.getOrDefault(userId, 0);
        attempts++;
        attemptsCache.put(userId, attempts);
    }

    public void loginSucceeded(String userId) {
        attemptsCache.remove(userId); // 로그인 성공 시 실패 횟수 초기화
    }

    public boolean isBlocked(String userId) {
        return attemptsCache.getOrDefault(userId, 0) >= MAX_ATTEMPTS;
    }
}

