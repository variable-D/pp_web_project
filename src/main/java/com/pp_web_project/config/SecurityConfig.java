package com.pp_web_project.config;

import com.pp_web_project.security.CustomAuthenticationFailureHandler;
import com.pp_web_project.security.CustomAuthenticationProvider;
import com.pp_web_project.security.CustomAuthenticationSuccessHandler;
import com.pp_web_project.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final LoginAttemptService loginAttemptService;
    private final CustomAuthenticationFailureHandler authenticationFailureHandler;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**").permitAll()  // ✅ 로그인 없이 접근 허용
                        .anyRequest().authenticated()  // ✅ 나머지 모든 요청은 로그인해야 접근 가능
                )
                .formLogin(form -> form
                        .loginPage("/login")  // ✅ 로그인 페이지 설정
                        .loginProcessingUrl("/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .failureHandler(authenticationFailureHandler)
                        .successHandler(authenticationSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                // ✅ 세션 관리 설정 추가
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // 세션 고정 공격 방지
                        .invalidSessionUrl("/login?sessionExpired=true") // 세션 만료 시 이동할 페이지
                        .sessionConcurrency(concurrency -> concurrency // 동시 로그인 관리
                                .maximumSessions(10) // 동시에 허용할 최대 세션 수
                                .expiredUrl("/login?sessionExpired=true") // 세션 만료 시 이동할 페이지
                        )
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ CustomAuthenticationProvider를 @Bean으로 등록
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, passwordEncoder(), loginAttemptService);
    }

    // ✅ AuthenticationManager에서 ObjectProvider를 사용하여 순환 참조 방지
    @Bean
    public AuthenticationManager authenticationManager(ObjectProvider<AuthenticationProvider> provider) {
        return new ProviderManager(List.of(provider.getIfAvailable(() -> authenticationProvider())));
    }
}
