-- Inserting test users
INSERT INTO users (name, date_of_birth, password)
VALUES
    ('John Doe', '15.05.1990', '$2a$10$Q3hjfy5NxMqWJ4iAOV3Ba.40tuCmh5uTEtVZO49.ZKBnw29hMeeRS'), -- password: password123
    ('Jane Smith', '22.03.1985', '$2a$10$XURPShQ5uN.kURHdrm0N3u96/aRxusCRelD66LiB3G0bHr3r09lny'), -- password: password123
    ('Alice Johnson', '10.11.1995', '$2a$10$XURPShQ5uN.kURHdrm0N3u96/aRxusCRelD66LiB3G0bHr3r09lny'); -- password: password123

-- Inserting accounts for test users
INSERT INTO accounts (user_id, balance, initial_deposit)
VALUES
    (1, 1000.00, 1000.00),
    (2, 5000.00, 5000.00),
    (3, 2500.00, 2500.00);

-- Inserting emails for test users
INSERT INTO email_data (user_id, email)
VALUES
    (1, 'john.doe@example.com'),
    (2, 'jane.smith@example.com'),
    (3, 'alice.johnson@example.com');

-- Inserting phones for test users
INSERT INTO phone_data (user_id, phone)
VALUES
    (1, '71234567890'),
    (2, '79876543210'),
    (3, '72345678901');