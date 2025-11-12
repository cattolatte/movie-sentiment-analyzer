-- Insert a movie to start with
INSERT INTO movies (title) VALUES ('Inception');

-- Insert reviews linked to the movie (which has movie_id = 1)
INSERT INTO reviews (movie_id, review, sentiment) VALUES
(1, 'I loved this movie, it was amazing!', 'Positive'),
(1, 'Terrible acting and a boring plot.', 'Negative');