-- Film SQL Queries

-- Add film
INSERT INTO films (name, description, release_date, duration_min, mpa_rating_id) VALUES (?, ?, ?, ?, ?)

-- Remove film
DELETE FROM films WHERE id = ?

-- Update film
UPDATE films SET name = ?, description = ?, release_date = ?, duration_min = ?, mpa_rating_id = ? WHERE id = ?

-- Get film by ID
SELECT f.id, f.name, f.description, f.release_date, f.duration_min,
       m.id as mpa_id, m.code as mpa_code, m.description as mpa_description
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
WHERE f.id = ?

-- Get all films
SELECT f.id, f.name, f.description, f.release_date, f.duration_min,
       m.id as mpa_id, m.code as mpa_code, m.description as mpa_description
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id

-- Load film genres
SELECT g.id, g.name, g.display_name
FROM film_genres fg
JOIN genres g ON fg.genre_id = g.id
WHERE fg.film_id = ?

-- Load film likes
SELECT user_id FROM film_likes WHERE film_id = ?

-- Load genres for films (batch, placeholders to be injected)
SELECT fg.film_id, g.name
FROM film_genres fg
JOIN genres g ON g.id = fg.genre_id
WHERE fg.film_id IN (%s)

-- Load likes for films (batch, placeholders to be injected)
SELECT film_id, user_id FROM film_likes WHERE film_id IN (%s)

-- Save film genres
INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)

-- Update film genres (delete old)
DELETE FROM film_genres WHERE film_id = ?

-- Get MPA rating ID
SELECT id FROM mpa_ratings WHERE code = ?

-- Get genre ID
SELECT id FROM genres WHERE name = ?

-- Add like
INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)

-- Remove like
DELETE FROM film_likes WHERE film_id = ? AND user_id = ?

-- Get top films by likes
SELECT f.id, f.name, f.description, f.release_date, f.duration_min,
       m.id as mpa_id, m.code as mpa_code, m.description as mpa_description,
       COUNT(fl.user_id) as likes_count
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id, f.name, f.description, f.release_date, f.duration_min, m.id, m.code, m.description
ORDER BY likes_count DESC
LIMIT ?

-- Get liked films by user
SELECT f.id, f.name, f.description, f.release_date, f.duration_min,
       m.id as mpa_id, m.code as mpa_code, m.description as mpa_description
FROM films f
LEFT JOIN mpa_ratings m ON f.mpa_rating_id = m.id
JOIN film_likes fl ON f.id = fl.film_id
WHERE fl.user_id = ?
