-- create table roles (id, name)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT  AUTO_INCREMENT PRIMARY KEY,  -- nếu có bảng trong entity rồi thì kdl ở đây phải trùng theo để nó ko tạo mới còn chưa có thì nó tạo mới
    name VARCHAR(255) NOT NULL UNIQUE
);
-- create table users (id, username, password)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT  AUTO_INCREMENT PRIMARY KEY,  -- nếu có bảng trong entity rồi thì kdl ở đây phải trùng theo để nó ko tạo mới còn chưa có thì nó tạo mới
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
-- create table users_roles (user_id, role_id)
CREATE TABLE IF NOT EXISTS users_roles (
    user_id BIGINT  NOT NULL,   -- nếu có bảng trong entity rồi thì kdl ở đây phải trùng theo để nó ko tạo mới còn chưa có thì nó tạo mới
    role_id BIGINT  NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- I only insert if the role name does not exists, i use mysql database
INSERT INTO
    roles (name)
SELECT 'Administrators'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'Administrators'
    );

INSERT INTO
    roles (name)
SELECT 'Managers'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'Managers'
    );

INSERT INTO
    roles (name)
SELECT 'Users'
WHERE
    NOT EXISTS (
        SELECT 1
        FROM roles
        WHERE
            name = 'Users'
    );

INSERT INTO
    users (username, password)
SELECT 'hoangle191205@gmail.com', '123456789'   -- chưa có thằng hoangle191205@gamil.com vs password : 123456789 thì nó tạo cho mik
WHERE
    NOT EXISTS (
        SELECT 1
        FROM users
        WHERE
            username = 'hoangle191205@gmail.com'
    );

INSERT INTO
    users_roles (user_id, role_id)
SELECT u.id, r.id
FROM (
        SELECT id
        FROM users
        WHERE
            username = 'hoangle191205@gmail.com'
        LIMIT 1
    ) u, (
        SELECT id
        FROM roles
        WHERE
            name = 'Administrators'   -- và nó add thg hoàng vs quyền admin
        LIMIT 1
    ) r
WHERE
    NOT EXISTS (
        SELECT 1
        FROM users_roles
        WHERE
            user_id = u.id
            AND role_id = r.id
    );