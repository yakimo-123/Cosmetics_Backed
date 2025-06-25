package org.cosmetic.com.config;

import lombok.RequiredArgsConstructor;
import org.cosmetic.com.security.AuthEntryPointJwt;
import org.cosmetic.com.security.CustomAccessDeniedHandler;
import org.cosmetic.com.security.jwt.AuthTokenFilter;
import org.cosmetic.com.security.oath2.CustomOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Các endpoint công khai (mọi method, chủ yếu là GET)
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/brands/**",
            "/api/products/**",
            "/api/categories/**",
            "/api/images/**"
    };
    private static final String[] ADMIN_GET_ENDPOINTS = {
            "/api/carts",
    };
    // Các endpoint yêu cầu ADMIN cho POST/PUT/DELETE
    private static final String[] ADMIN_POST_ENDPOINTS = {
            "/api/brands/**",
            "/api/categories/**"
    };
    private static final String[] ADMIN_PUT_ENDPOINTS = {
            "/api/brands/**",
            "/api/categories/**"
    };
    private static final String[] ADMIN_DELETE_ENDPOINTS = {
            "/api/brands/**",
            "/api/categories/**"
    };
    private final AuthEntryPointJwt authEntryPointJwt;
    private final AuthTokenFilter authTokenFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    // Define the white-listed URLs that do not require authentication
    String[] WHITE_LIST_URL = {
            "/api/auth/**",
            "/api/orders/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(authorize -> authorize
                        // Public GET endpoints
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()
                        // Public endpoints (mọi method)
                        .requestMatchers(WHITE_LIST_URL).permitAll()

                        // Admin-only GET/POST/PUT/DELETE
                        .requestMatchers(HttpMethod.GET, ADMIN_GET_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, ADMIN_POST_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, ADMIN_PUT_ENDPOINTS).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, ADMIN_DELETE_ENDPOINTS).hasRole("ADMIN")

                        // Mặc định: tất cả các route còn lại yêu cầu xác thực
                        .anyRequest().authenticated()
                )
                .oauth2Login(oath2 -> oath2.
                        successHandler(customOAuth2SuccessHandler)
                )
                .exceptionHandling(ex -> ex.
                        authenticationEntryPoint(authEntryPointJwt)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(
                        authTokenFilter,
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:8080", "http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // Quan trọng nếu dùng cookie hoặc token

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
