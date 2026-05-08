package com.project.finance.api.model;

import com.project.finance.api.exceptions.BusinessException;
import com.project.finance.api.model.enums.UserStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Setter
    @Column(length = 50, unique = true, nullable = false)
    private String username;

    @Setter
    @Column(length = 255, nullable = false)
    private String password;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private BankAccount bankAccount;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Category> categories = new ArrayList<>();

    public User(String login, String password) {
        this.username = login;
        this.password = password;
    }

    public void createBankAccount() {
        if (this.bankAccount != null) {
            throw new BusinessException("User already has a bank account");
        }
        this.bankAccount = new BankAccount(this);
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        category.setUser(this);
    }

}
