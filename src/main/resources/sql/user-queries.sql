-- User SQL Queries

-- Add user
INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)

-- Remove user
DELETE FROM users WHERE id = ?

-- Update user
UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?

-- Get user by ID
SELECT id, email, login, name, birthday FROM users WHERE id = ?

-- Get all users
SELECT id, email, login, name, birthday FROM users

-- Load user friends
SELECT CASE 
    WHEN user_id1 = ? THEN user_id2 
    ELSE user_id1 
END as friend_id
FROM friendships 
WHERE (user_id1 = ? OR user_id2 = ?) 
AND status_id = 2

-- Add friend
INSERT INTO friendships (user_id1, user_id2, status_id, initiated_by)
VALUES (?, ?, 1, ?)
ON CONFLICT (user_id1, user_id2) DO UPDATE SET
status_id = 1, initiated_by = ?

-- Confirm friendship
UPDATE friendships SET status_id = 2 WHERE user_id1 = ? AND user_id2 = ?

-- Remove friend
DELETE FROM friendships WHERE user_id1 = ? AND user_id2 = ?

-- Get friends (one-sided):
-- return users that current user initiated as friends (any status),
-- plus confirmed friendships regardless of initiator
SELECT u.id, u.email, u.login, u.name, u.birthday
FROM users u
JOIN friendships f ON (
    (f.user_id1 = ? AND u.id = f.user_id2) OR
    (f.user_id2 = ? AND u.id = f.user_id1 AND f.status_id = 2)
)
WHERE u.id != ?

-- Get common friends (one-sided + confirmed)
WITH user1_friends AS (
    SELECT CASE WHEN f.user_id1 = ? THEN f.user_id2 ELSE f.user_id1 END as friend_id
    FROM friendships f
    WHERE (f.user_id1 = ? OR (f.user_id2 = ? AND f.status_id = 2))
),
user2_friends AS (
    SELECT CASE WHEN f.user_id1 = ? THEN f.user_id2 ELSE f.user_id1 END as friend_id
    FROM friendships f
    WHERE (f.user_id1 = ? OR (f.user_id2 = ? AND f.status_id = 2))
)
SELECT u.id, u.email, u.login, u.name, u.birthday
FROM users u
JOIN user1_friends uf1 ON u.id = uf1.friend_id
JOIN user2_friends uf2 ON u.id = uf2.friend_id
