package com.project.finance.api.security;

import com.project.finance.api.model.enums.UserStatus;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    @Getter
    private UUID id;
    private String username;
    private String password;
    @Getter
    private UserStatus status;

    public CustomUserDetails(UUID id, String username, String password, UserStatus status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

}
