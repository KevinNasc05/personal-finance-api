package com.project.finance.api.service.query;

import com.project.finance.api.exceptions.NotFoundException;
import com.project.finance.api.model.User;
import com.project.finance.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow( () -> new NotFoundException("User not found") );
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow( () -> new NotFoundException("User not found") );
    }

}
