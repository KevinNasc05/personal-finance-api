package com.project.finance.api.mappers;

import com.project.finance.api.dto.request.UserRequestDto;
import com.project.finance.api.dto.response.UserResponseDto;
import com.project.finance.api.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDto dto) {
        return new User(dto.username(), dto.password());
    }

    public UserResponseDto toResponse(User user) {
        return new UserResponseDto(
                user.getUsername(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

}
