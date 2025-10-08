
INSERT INTO mpa_ratings (code, description)
SELECT 'G', 'Нет возрастных ограничений'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'G');

INSERT INTO mpa_ratings (code, description)
SELECT 'PG', 'Детям рекомендуется смотреть с родителями'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'PG');

INSERT INTO mpa_ratings (code, description)
SELECT 'PG-13', 'Детям до 13 лет просмотр не желателен'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'PG-13');

INSERT INTO mpa_ratings (code, description)
SELECT 'R', 'До 17 лет только в присутствии взрослого'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'R');

INSERT INTO mpa_ratings (code, description)
SELECT 'NC-17', 'До 18 лет просмотр запрещён'
WHERE NOT EXISTS (SELECT 1 FROM mpa_ratings WHERE code = 'NC-17');

INSERT INTO genres (name, display_name)
SELECT 'COMEDY', 'Комедия' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='COMEDY');
INSERT INTO genres (name, display_name)
SELECT 'DRAMA', 'Драма' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='DRAMA');
INSERT INTO genres (name, display_name)
SELECT 'CARTOON', 'Мультфильм' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='CARTOON');
INSERT INTO genres (name, display_name)
SELECT 'THRILLER', 'Триллер' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='THRILLER');
INSERT INTO genres (name, display_name)
SELECT 'DOCUMENTARY', 'Документальный' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='DOCUMENTARY');
INSERT INTO genres (name, display_name)
SELECT 'ACTION', 'Боевик' WHERE NOT EXISTS (SELECT 1 FROM genres WHERE name='ACTION');

INSERT INTO friend_statuses (id, code) SELECT 1, 'PENDING' WHERE NOT EXISTS (SELECT 1 FROM friend_statuses WHERE id=1);
INSERT INTO friend_statuses (id, code) SELECT 2, 'CONFIRMED' WHERE NOT EXISTS (SELECT 1 FROM friend_statuses WHERE id=2);
INSERT INTO friend_statuses (id, code) SELECT 3, 'DENIED' WHERE NOT EXISTS (SELECT 1 FROM friend_statuses WHERE id=3);

-- Sample users
INSERT INTO users (email, login, name, birthday)
SELECT 'alice@example.com','alice','Alice', DATE '1990-01-01'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='alice@example.com');

INSERT INTO users (email, login, name, birthday)
SELECT 'bob@example.com','bob','Bob', DATE '1988-05-12'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='bob@example.com');

INSERT INTO users (email, login, name, birthday)
SELECT 'carol@example.com','carol','Carol', DATE '1995-07-23'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email='carol@example.com');

-- Sample films
INSERT INTO films (name, description, release_date, duration_min, mpa_rating_id)
SELECT 'Inception','Mind-bending thriller', DATE '2010-07-16', 148,
       (SELECT id FROM mpa_ratings WHERE code='PG-13')
WHERE NOT EXISTS (SELECT 1 FROM films WHERE name='Inception');

INSERT INTO films (name, description, release_date, duration_min, mpa_rating_id)
SELECT 'Toy Story','Animated adventure', DATE '1995-11-22', 81,
       (SELECT id FROM mpa_ratings WHERE code='G')
WHERE NOT EXISTS (SELECT 1 FROM films WHERE name='Toy Story');

-- Link films to genres
INSERT INTO film_genres (film_id, genre_id)
SELECT f.id, g.id FROM films f, genres g
WHERE f.name='Inception' AND g.name IN ('THRILLER','ACTION')
AND NOT EXISTS (
    SELECT 1 FROM film_genres fg
    WHERE fg.film_id = f.id AND fg.genre_id = g.id
);

INSERT INTO film_genres (film_id, genre_id)
SELECT f.id, g.id FROM films f, genres g
WHERE f.name='Toy Story' AND g.name IN ('CARTOON','COMEDY')
AND NOT EXISTS (
    SELECT 1 FROM film_genres fg
    WHERE fg.film_id = f.id AND fg.genre_id = g.id
);

-- Likes
INSERT INTO film_likes (film_id, user_id)
SELECT (SELECT id FROM films WHERE name='Inception'), (SELECT id FROM users WHERE email='alice@example.com')
WHERE NOT EXISTS (
    SELECT 1 FROM film_likes WHERE film_id=(SELECT id FROM films WHERE name='Inception') AND user_id=(SELECT id FROM users WHERE email='alice@example.com')
);

INSERT INTO film_likes (film_id, user_id)
SELECT (SELECT id FROM films WHERE name='Toy Story'), (SELECT id FROM users WHERE email='bob@example.com')
WHERE NOT EXISTS (
    SELECT 1 FROM film_likes WHERE film_id=(SELECT id FROM films WHERE name='Toy Story') AND user_id=(SELECT id FROM users WHERE email='bob@example.com')
);

-- Friendships: Alice and Bob pending, Alice and Carol confirmed
INSERT INTO friendships (user_id1, user_id2, status_id, initiated_by)
SELECT LEAST(u1.id,u2.id), GREATEST(u1.id,u2.id), 1, u1.id
FROM users u1, users u2
WHERE u1.email='alice@example.com' AND u2.email='bob@example.com'
AND NOT EXISTS (
    SELECT 1 FROM friendships f WHERE f.user_id1=LEAST(u1.id,u2.id) AND f.user_id2=GREATEST(u1.id,u2.id)
);

INSERT INTO friendships (user_id1, user_id2, status_id, initiated_by)
SELECT LEAST(u1.id,u2.id), GREATEST(u1.id,u2.id), 2, u1.id
FROM users u1, users u2
WHERE u1.email='alice@example.com' AND u2.email='carol@example.com'
AND NOT EXISTS (
    SELECT 1 FROM friendships f WHERE f.user_id1=LEAST(u1.id,u2.id) AND f.user_id2=GREATEST(u1.id,u2.id)
);


