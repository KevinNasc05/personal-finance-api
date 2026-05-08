CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE','INACTIVE'))
);

CREATE TABLE bank_accounts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    balance DECIMAL(14,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bank_account_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE categories (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(40) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE transactions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    bank_account_id UUID NOT NULL,
    description TEXT,
    amount DECIMAL(14,2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    category_id BIGINT NOT NULL,
    reversed_transaction_id BIGINT,
    date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transaction_bank_account FOREIGN KEY (bank_account_id) REFERENCES bank_accounts(id),
    CONSTRAINT fk_transaction_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_transaction_reversed FOREIGN KEY (reversed_transaction_id) REFERENCES transactions(id),
    CONSTRAINT chk_transaction_type CHECK (type IN ('INCOME','EXPENSE'))
);

CREATE UNIQUE INDEX uq_category_user_name ON categories (user_id, LOWER(name));
CREATE INDEX idx_transaction_account_date ON transactions (bank_account_id, date);
