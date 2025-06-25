package org.cosmetic.com.security.jwt;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.cosmetic.com.enums.Role;
import org.cosmetic.com.exception.AppException;
import org.cosmetic.com.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;


    public String generateToken(String username, Role role) {
        try {
            JWSSigner signer = new MACSigner(jwtSecret.getBytes());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .claim("role", role)
                    .claim("username", username)
                    .expirationTime(new Date(System.currentTimeMillis() + jwtExpiration))
                    .build();
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("Error generating JWT token: {}", e.getMessage());
            throw new AppException(ErrorCode.JWT_GENERATION_FAILED, e.getMessage());
        }
    }

    public String generateRefreshToken(String username, Role role) {
        try {
            JWSSigner signer = new MACSigner(jwtSecret.getBytes());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + refreshExpiration))
                    .claim("role", role)
                    .claim("type", "refresh") // thêm phân biệt token
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("Error generating refresh JWT token: {}", e.getMessage());
            throw new AppException(ErrorCode.JWT_GENERATION_FAILED, e.getMessage());
        }
    }

    public String getRoleFromToken(String token) throws ParseException {
        return parseToken(token).getStringClaim("role");
    }

    public String getUsernameFromToken(String token) {
        return parseToken(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
            boolean signatureValid = signedJWT.verify(verifier);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (!signatureValid) {
                throw new AppException(ErrorCode.JWT_SIGNATURE_INVALID);
            }
            if (expiration == null || expiration.before(new Date())) {
                throw new AppException(ErrorCode.JWT_EXPIRED);
            }
            return true;

        } catch (ParseException e) {
            throw new AppException(ErrorCode.JWT_MALFORMED, e.getMessage());
        } catch (JOSEException e) {
            throw new AppException(ErrorCode.JWT_SIGNATURE_INVALID, e.getMessage());
        }
    }


    public void logout(String token) {
        // In a stateless JWT implementation, logout is typically handled by client-side
        // logic (e.g., removing the token from local storage).
        // If you want to implement server-side token invalidation, you can maintain a blacklist.
        // This method can be left empty or implemented as needed.
    }

    public String getTokenFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    // Utility to reuse parsing logic with standardized error handling
    private JWTClaimsSet parseToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.JWT_MALFORMED, e.getMessage());
        }
    }

}