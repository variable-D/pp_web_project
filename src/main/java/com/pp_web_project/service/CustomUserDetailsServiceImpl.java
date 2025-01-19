package com.pp_web_project.service;

import com.pp_web_project.domain.Admin;
import com.pp_web_project.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + userId));

        log.info("Admin 객체: {}", admin);  // ✅ admin 객체가 정상적으로 로드되는지 확인
        log.info("아이디: {}", admin.getUserId());
        log.info("비밀번호: {}", admin.getPassword());

        return User.withUsername(admin.getUserId())
                .password(admin.getPassword())
                .roles("ADMIN")
                .build();
    }

}
