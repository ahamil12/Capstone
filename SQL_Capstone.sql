USE tv_shows;

-- Drop existing tables if they exist to avoid conflicts
DROP TABLE IF EXISTS users_shows;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS shows;

-- Create Users Table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'user' -- 'user' or 'admin'
);

-- Create Shows Table
CREATE TABLE shows (
    show_id INT AUTO_INCREMENT PRIMARY KEY,
    show_name VARCHAR(100) NOT NULL,
    genre VARCHAR(50) NOT NULL,
    director VARCHAR(100) NOT NULL,
    total_episodes INT NOT NULL,
    current_episode INT DEFAULT 1, -- Add the current_episode column
    status VARCHAR(50) DEFAULT 'ongoing',
    rating DOUBLE DEFAULT 0.0, -- Average rating for the show
    rating_count INT DEFAULT 0 -- Number of ratings for the show
);

-- Create Users_Shows Table for tracking user-specific show data
CREATE TABLE users_shows (
    user_show_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    show_id INT,
    current_episode INT DEFAULT 1,
    user_rating DOUBLE DEFAULT 0.0,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (show_id) REFERENCES shows(show_id)
);

-- Insert Data into Users Table
INSERT INTO users (name, username, password, email, role) VALUES
('Admin User', 'admin', 'adminpass', 'admin@example.com', 'admin'),
('Alice Johnson', 'alicej', 'password123', 'alice@example.com', 'user'),
('Bob Smith', 'bobsmith', 'securepass', 'bob@example.com', 'user'),
('Charlie Brown', 'charlieb', 'mypassword', 'charlie@example.com', 'user'),
('David Wilson', 'davidw', 'pass123', 'david@example.com', 'user'),
('Eve Davis', 'eved', 'evesecret', 'eve@example.com', 'user'),
('Frank Miller', 'frankm', 'miller123', 'frank@example.com', 'user');

-- Insert Data into Shows Table
INSERT INTO shows (show_name, genre, director, total_episodes, current_episode, status) VALUES
('Breaking Bad', 'Crime', 'Vince Gilligan', 62, 1, 'ongoing'),
('Game of Thrones', 'Adventure', 'David Benioff', 73, 1, 'completed'),
('Friends', 'Comedy', 'David Crane', 236, 1, 'completed'),
('Stranger Things', 'Thriller', 'The Duffer Brothers', 34, 1, 'ongoing'),
('The Office', 'Comedy', 'Greg Daniels', 201, 1, 'completed'),
('Attack on Titan', 'Action', 'Hajime Isayama', 88, 1, 'ongoing'),
('The Mandalorian', 'Sci-Fi', 'Jon Favreau', 24, 1, 'ongoing'),
('The Witcher', 'Fantasy', 'Lauren Schmidt', 24, 1, 'ongoing'),
('Dark', 'Sci-Fi', 'Baran bo Odar', 26, 1, 'completed'),
('Sherlock', 'Crime', 'Steven Moffat', 13, 1, 'completed');

-- Example user-specific show tracking data
INSERT INTO users_shows (user_id, show_id, current_episode, user_rating)
VALUES
(1, 1, 1, 0.0),
(1, 2, 1, 0.0),
(2, 3, 1, 0.0),
(2, 4, 1, 0.0),
(3, 5, 1, 0.0),
(3, 6, 1, 0.0),
(4, 7, 1, 0.0),
(4, 8, 1, 0.0),
(5, 9, 1, 0.0),
(5, 10, 1, 0.0);



