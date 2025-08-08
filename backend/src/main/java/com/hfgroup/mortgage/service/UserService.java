package com.hfgroup.mortgage.service;

import com.hfgroup.mortgage.Repository.UserRepository;
import com.hfgroup.mortgage.dto.request.UserRegistrationDTO;
import com.hfgroup.mortgage.dto.response.UserResponseDTO;
import com.hfgroup.mortgage.exception.UserAlreadyExistsException;
import com.hfgroup.mortgage.exception.UserNotFoundException;
import com.hfgroup.mortgage.model.User;
import com.hfgroup.mortgage.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        user.setRoles(registrationDTO.getRoles());
        
        User savedUser = userRepository.save(user);
        
        return convertToResponseDTO(savedUser);
    }
    
    public UserResponseDTO getUserById(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        return convertToResponseDTO(user);
    }
    
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public UserResponseDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        
        return convertToResponseDTO(user);
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return userRepository.findById(userPrincipal.getId())
                    .orElseThrow(() -> new UserNotFoundException("Current user not found"));
        }
        throw new UserNotFoundException("No authenticated user found");
    }

    private UserResponseDTO convertToResponseDTO(User user) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setRoles(user.getRoles());
        responseDTO.setCreatedAt(user.getCreatedAt());
        return responseDTO;
    }
}
