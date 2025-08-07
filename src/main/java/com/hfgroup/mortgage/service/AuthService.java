package com.hfgroup.mortgage.service;

import com.hfgroup.mortgage.dto.request.LoginRequestDTO;
import com.hfgroup.mortgage.dto.response.LoginResponseDTO;
import com.hfgroup.mortgage.exception.AuthenticationException;
import com.hfgroup.mortgage.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            
            String authorities = authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");

            return new LoginResponseDTO(jwt, "Bearer", loginRequest.getUsername(), authorities);
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", loginRequest.getUsername());
            throw new AuthenticationException("Invalid username or password");
        } catch (Exception e) {
            log.error("Authentication error for user: {}", loginRequest.getUsername(), e);
            throw new AuthenticationException("Authentication failed", e);
        }
    }
}
