-- Creating users table
CREATE TABLE users (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    date_of_birth VARCHAR(10) NOT NULL CHECK (date_of_birth ~ '^[0-3][0-9]\.[0-1][0-9]\.[1-2][0-9]{3}$'),
    password VARCHAR(255) NOT NULL
);

-- Creating accounts table
CREATE TABLE accounts (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL CHECK (balance >= 0),
    initial_deposit DECIMAL(19, 2) NOT NULL CHECK (initial_deposit >= 0),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Creating email_data table
CREATE TABLE email_data (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Creating phone_data table
CREATE TABLE phone_data (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id BIGINT NOT NULL,
    phone VARCHAR(11) NOT NULL UNIQUE CHECK (phone ~ '^7[0-9]{10}$'),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);