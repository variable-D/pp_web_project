package com.pp_web_project;

import com.pp_web_project.security.CustomAuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest  // ✅ Spring 컨텍스트에서 테스트 실행
public class LoginTest {

    @Autowired  // ✅ Spring이 관리하는 authenticationProvider 자동 주입
    private CustomAuthenticationProvider authenticationProvider;

    @Test
    void testUserLoginBlockedAfterFiveFailedAttempts() {
        String userId = "testUser";

        // 5번 로그인 실패 시도
        for (int i = 0; i < 5; i++) {
            try {
                authenticationProvider.authenticate(
                        new UsernamePasswordAuthenticationToken(userId, "wrongPassword")
                );
            } catch (BadCredentialsException ignored) { }
        }

        // ✅ 6번째 시도 (차단되어야 함)
        Exception exception = assertThrows(BadCredentialsException.class, () -> {
            authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, "wrongPassword")
            );
        });

        assertEquals("계정이 잠겼습니다. 나중에 다시 시도하세요.", exception.getMessage());
    }
}
