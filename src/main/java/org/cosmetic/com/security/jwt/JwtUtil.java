package org.cosmetic.com.security.jwt;


import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.cosmetic.com.exception.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(String username) {
        try {
            JWSSigner signer = new MACSigner(jwtSecret.getBytes());
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + jwtExpiration))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(jwtSecret.getBytes());
            boolean signatureValid = signedJWT.verify(verifier);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

            if (!signatureValid) {
                throw new JwtException("Invalid JWT signature");
            }

            if (expiration == null || expiration.before(new Date())) {
                throw new JwtException("JWT expired");
            }

            return true;

        } catch (ParseException e) {
            throw new JwtException("Malformed JWT token", e);
        } catch (JOSEException e) {
            throw new JwtException("Error verifying JWT signature", e);
        }
    }


    public String refreshToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            String username = signedJWT.getJWTClaimsSet().getSubject();
            return generateToken(username);
        } catch (ParseException e) {
            throw new RuntimeException("Error refreshing JWT", e);
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
}