package com.pp_web_project.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j  // ✅ 로그 추가
@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5; // 최대 로그인 실패 횟수
    private static final long BLOCK_TIME = 5 * 60 * 1000; // 10분 (밀리초 단위)

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Long> blockTimestamps = new ConcurrentHashMap<>();

    /**
     * 로그인 실패 시 실패 횟수를 증가시키고 차단 여부를 결정
     */
    public void loginFailed(String userId) {
        if (isBlocked(userId)) {
            log.warn("[LOGIN FAILED IGNORED] User: {} is already blocked.", userId);
            return; // ✅ 차단된 경우 실패 횟수를 증가시키지 않음
        }

        int attempts = attemptsCache.getOrDefault(userId, 0) + 1;
        attemptsCache.put(userId, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            blockTimestamps.put(userId, System.currentTimeMillis());
            log.info("[USER BLOCKED] User: {}", userId);
        }

        log.info("[LOGIN FAILED] User: {}, Fail Count: {}", userId, attempts);
    }

    /**
     * 로그인 성공 시 실패 횟수를 초기화
     */
    public void loginSucceeded(String userId) {
        attemptsCache.remove(userId);
        blockTimestamps.remove(userId);
        log.info("[LOGIN SUCCESS] User: {}, Reset Fail Count", userId); // ✅ 성공 시 초기화 로그 추가
    }

    /**
     * 로그인 실패 횟수 조회 (UI에서 표시하기 위함)
     */
    public int getFailedAttempts(String userId) {
        int failCount = attemptsCache.getOrDefault(userId, 0);
        log.info("[GET FAIL COUNT] User: {}, Fail Count: {}", userId, failCount); // ✅ 현재 실패 횟수 조회 로그 추가
        return failCount;
    }

    /**
     * 사용자가 차단되었는지 확인 (자동 차단 해제 포함)
     */
    public boolean isBlocked(String userId) {
        if (!blockTimestamps.containsKey(userId)) {
            return false;
        }

        long blockTime = blockTimestamps.get(userId);
        if (System.currentTimeMillis() - blockTime > BLOCK_TIME) {
            attemptsCache.remove(userId);
            blockTimestamps.remove(userId);
            log.info("[UNBLOCKED] User: {}", userId);
            return false;
        }

        log.info("[BLOCKED] User: {}", userId);
        return true; // ✅ 차단된 경우 true 반환
    }


    /**
     * 사용자의 차단이 해제될 때까지 남은 시간을 분 단위로 반환
     */
    public long getRemainingBlockTime(String userId) {
        if (!isBlocked(userId)) {
            return 0;
        }
        long blockTime = blockTimestamps.get(userId);
        long elapsedTime = System.currentTimeMillis() - blockTime;
        long remainingTime = BLOCK_TIME - elapsedTime;
        return remainingTime / (60 * 1000); // 밀리초 -> 분 변환
    }
}
