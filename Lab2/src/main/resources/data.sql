INSERT INTO customers (first_name, last_name, email, created_at) VALUES
                                                                     ('Иван', 'Петров', 'ivan@example.com', NOW()),
                                                                     ('Мария', 'Смирнова', 'maria@example.com', NOW())
ON CONFLICT (email) DO NOTHING;

-- Создаём роли (если ещё не созданы)
INSERT INTO roles (name) VALUES
                             ('ROLE_USER'),
                             ('ROLE_ADMIN')
ON CONFLICT (name) DO NOTHING;

-- Создаём админа (опционально, для тестов)
INSERT INTO users (username, password) VALUES
    ('admin', '$2a$10$pxExsN1f/tMyw1aBdD7yvOrKYvb0dw22a/5yRcwILjbDSCjizv09G')
ON CONFLICT (username) DO NOTHING;

-- Связываем админа с ROLE_ADMIN
INSERT INTO users_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;