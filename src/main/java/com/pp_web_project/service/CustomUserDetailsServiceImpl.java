package com.pp_web_project.service;

import com.pp_web_project.domain.Admin;
import com.pp_web_project.domain.Users;
import com.pp_web_project.repository.AdminRepository;
import com.pp_web_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        // ✅ 1. Admin 테이블 조회
        Optional<Admin> adminOpt = adminRepository.findByUserId(userId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            log.info("관리자 로그인: {}", admin.getUserId());

            return User.withUsername(admin.getUserId())
                    .password(admin.getPassword())
                    .roles("ADMIN")  // ✅ 관리자 권한 부여
                    .build();
        }

        // ✅ 2. User 테이블 조회
        Optional<Users> userOpt = userRepository.findByUserId(userId);
        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            log.info("일반 사용자 로그인: {}", user.getUserId());

            return User.withUsername(user.getUserId())
                    .password(user.getPassword())
                    .roles("USER")  // ✅ 일반 사용자 권한 부여
                    .build();
        }

        // ✅ 3. 존재하지 않는 사용자
        log.warn("아이디 없음: {}", userId);
        throw new UsernameNotFoundException("USER_NOT_FOUND"); // ❗ 예외 메시지 통일
    }
}
