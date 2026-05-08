package com.project.finance.api.service.command;

import com.project.finance.api.exceptions.ConflictException;
import com.project.finance.api.model.User;
import com.project.finance.api.model.enums.UserStatus;
import com.project.finance.api.repository.UserRepository;
import com.project.finance.api.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final PasswordEncoder encoder;

    @Transactional
    public User save(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.createBankAccount();
        return userRepository.save(user);
    }

    @Transactional
    public void update(UUID id, User user) {
        User userFound = userQueryService.findById(id);

        if (userRepository.existsByUsername(user.getUsername()) && !userFound.getUsername().equals(user.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        userFound.setUsername(user.getUsername());
        userFound.setPassword(encoder.encode(user.getPassword()));
    }

    @Transactional
    public void updateStatus(UUID id, UserStatus status) {
        User userFound = userQueryService.findById(id);

        userFound.setStatus(status);
    }

}
