package org.cosmetic.com.service;

public interface AuthenticationService {

    String authenticate(String username, String password) throws IllegalArgumentException;
    boolean validateToken(String token);
}
