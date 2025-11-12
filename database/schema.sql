-- Create the 'movies' table first
CREATE TABLE IF NOT EXISTS movies (
movie_id INT AUTO_INCREMENT PRIMARY KEY,
title VARCHAR(255) NOT NULL UNIQUE
);

-- Update the 'reviews' table to link to a movie
CREATE TABLE IF NOT EXISTS reviews (
id INT AUTO_INCREMENT PRIMARY KEY,
movie_id INT NOT NULL,
review TEXT NOT NULL,
sentiment VARCHAR(16),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
FOREIGN KEY (movie_id) REFERENCES movies(movie_id) ON DELETE CASCADE
);