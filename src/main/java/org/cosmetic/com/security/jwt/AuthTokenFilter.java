package org.cosmetic.com.security.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.cosmetic.com.security.CustomUserDetails;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final PathMatcher pathMatcher;

    String[] WHITE_LIST_URL = {
            "/api/auth/"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Skip filtering for whitelisted URLs
        String requestURI = request.getRequestURI();
        for (String pattern : WHITE_LIST_URL) {
            if (pathMatcher.match(pattern, requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        try {
            MDC.put("traceId", UUID.randomUUID().toString());

            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token = jwtUtil.getTokenFromHeader(authHeader);

            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                UserDetails userDetails = new CustomUserDetails(username, Role.valueOf(role));
                if (username != null) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    MDC.put("username", username);
                }
            }else{
                MDC.put("username", "anonymous");
            }
        } catch (AppException ex) {
            log.error("❌ AppException: {}", ex.getMessage(), ex);
            throw ex; // đã chuẩn
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid token: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.JWT_MALFORMED, "Invalid role in token");
        } catch (Exception e) {
            log.error("❌ Token parsing failed", e);
            throw new AppException(ErrorCode.JWT_MALFORMED, "Token parsing failed", e);
        }finally {
            filterChain.doFilter(request, response);
            MDC.clear();
        }
    }
}
