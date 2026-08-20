package com.syncria.module.user.service;

import com.syncria.module.user.dto.UserResponseDTO;
import com.syncria.module.user.entity.User;
import com.syncria.module.user.repository.UserRepository;
import com.syncria.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toResponseDTO(user);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return toResponseDTO(user);
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getCompanyId(),
                user.getCreatedAt()
        );
    }
}
