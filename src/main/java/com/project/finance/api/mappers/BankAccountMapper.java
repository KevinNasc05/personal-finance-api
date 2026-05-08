package com.project.finance.api.mappers;

import com.project.finance.api.dto.response.BankAccountResponseDto;
import com.project.finance.api.model.BankAccount;
import org.springframework.stereotype.Component;

@Component
public class BankAccountMapper {

    public BankAccountResponseDto toResponse(BankAccount account) {
        return new BankAccountResponseDto(
                account.getBalance(),
                account.getUpdatedAt(),
                account.getCreatedAt()
        );
    }

}
