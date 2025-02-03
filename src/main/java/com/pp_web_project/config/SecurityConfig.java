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
                        .requestMatchers("/admin/**").hasRole("ADMIN")  // ✅ 관리자만 접근 가능
                        .requestMatchers("/user/**").hasRole("USER")    // ✅ 일반 사용자만 접근 가능
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/redirect", true)  // ✅ 로그인 성공 후 리디렉트 (ROLE_ADMIN → /admin, ROLE_USER → /user)
                        .failureHandler(authenticationFailureHandler)  // ✅ 로그인 실패 핸들러는 유지
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .invalidSessionUrl("/login?sessionExpired=true")
                        .sessionConcurrency(concurrency -> concurrency
                                .maximumSessions(10)
                                .expiredUrl("/login?sessionExpired=true")
                        )
                )
                // ✅ CSRF 예외 처리 추가
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        "/login",  // 로그인 요청
                        "/admin/joytel/products/expiring/update",  // PATCH 요청을 허용할 엔드포인트
                        "/admin/sk/products/sold/update"  // GET 요청을 허용할 엔드포인트
                ));


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
