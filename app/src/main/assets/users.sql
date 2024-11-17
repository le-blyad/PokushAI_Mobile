CREATE TABLE users (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT UNIQUE,
    password TEXT,
    image BLOB,
    number TEXT UNIQUE,
    email TEXT UNIQUE
);

INSERT INTO users (username, password, image, number, email) VALUES
('user1', 'useruser1', NULL, '+7 (111)-111-11-11', 'user1@users.com');
