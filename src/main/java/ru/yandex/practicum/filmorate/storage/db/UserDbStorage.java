package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.db.util.SqlQueryLoader;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository("userDbStorage")
@Primary
@Slf4j
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;
    private final SqlQueryLoader sqlQueryLoader;
    
    @Override
    public User addUser(User user) {
        log.debug("Добавление пользователя в БД: {}", user);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Add user");
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        
        Long generatedId = keyHolder.getKey().longValue();
        user.setId(generatedId);
        
        log.debug("Пользователь добавлен в БД с ID: {}", generatedId);
        return user;
    }

    @Override
    public boolean removeUser(User user) {
        log.debug("Удаление пользователя из БД: {}", user);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Remove user");
        int rowsAffected = jdbcTemplate.update(sql, user.getId());
        
        boolean removed = rowsAffected > 0;
        log.debug("Пользователь удален из БД: {}", removed);
        return removed;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Обновление пользователя в БД: {}", user);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Update user");
        
        int rowsAffected = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        
        if (rowsAffected == 0) {
            throw new RuntimeException("Пользователь с ID " + user.getId() + " не найден");
        }
        
        log.debug("Пользователь обновлен в БД");
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        log.debug("Получение пользователя по ID: {}", id);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Get user by ID");
        
        try {
            User user = jdbcTemplate.queryForObject(sql, userRowMapper, id);
            if (user != null) {
                initializeUserCollections(user);
                loadUserFriends(user);
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            log.debug("Пользователь с ID {} не найден", id);
            return Optional.empty();
        }
    }


    public Optional<User> findUserById(long id) {
        return getUser(id);
    }

    @Override
    public Map<Long, User> getUsers() {
        log.debug("Получение всех пользователей из БД");
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Get all users");
        List<User> users = jdbcTemplate.query(sql, userRowMapper);
        users.forEach(this::initializeUserCollections);
        loadFriendsForUsers(users);
        
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        
        log.debug("Получено пользователей: {}", userMap.size());
        return userMap;
    }

    private void initializeUserCollections(User user) {
        user.setFriends(new HashSet<>());
    }

    private void loadUserFriends(User user) {
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Load user friends");
        
        List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, 
                user.getId(), user.getId(), user.getId());
        
        user.setFriends(new HashSet<>(friendIds));
        user.setFriendsCount(friendIds.size());
    }

    public void addFriend(Long userId, Long friendId) {
        log.debug("Добавление друга {} пользователю {}", friendId, userId);

        Long user1 = Math.min(userId, friendId);
        Long user2 = Math.max(userId, friendId);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Add friend");
        
        jdbcTemplate.update(sql, user1, user2, userId, userId);
    }

    public void confirmFriendship(Long userId, Long friendId) {
        log.debug("Подтверждение дружбы между пользователями {} и {}", userId, friendId);
        
        Long user1 = Math.min(userId, friendId);
        Long user2 = Math.max(userId, friendId);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Confirm friendship");
        jdbcTemplate.update(sql, user1, user2);
    }

    public void removeFriend(Long userId, Long friendId) {
        log.debug("Удаление дружбы между пользователями {} и {}", userId, friendId);
        
        Long user1 = Math.min(userId, friendId);
        Long user2 = Math.max(userId, friendId);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Remove friend");
        jdbcTemplate.update(sql, user1, user2);
    }

    public List<User> getFriends(Long userId) {
        log.debug("Получение друзей пользователя {}", userId);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Get friends");
        
        List<User> friends = jdbcTemplate.query(sql, userRowMapper, userId, userId, userId);
        friends.forEach(this::initializeUserCollections);
        loadFriendsForUsers(friends);
        
        return friends;
    }

    public List<User> getCommonFriends(Long userId1, Long userId2) {
        log.debug("Получение общих друзей пользователей {} и {}", userId1, userId2);
        
        String sql = sqlQueryLoader.getQuery("user-queries.sql", "Get common friends");
        
        List<User> commonFriends = jdbcTemplate.query(sql, userRowMapper, 
                userId1, userId1, userId1, userId2, userId2, userId2);
        commonFriends.forEach(this::initializeUserCollections);
        loadFriendsForUsers(commonFriends);
        
        return commonFriends;
    }

    private void loadFriendsForUsers(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        List<Long> ids = new ArrayList<>();
        for (User u : users) {
            if (u.getId() != null) {
                ids.add(u.getId());
            }
        }
        if (ids.isEmpty()) {
            return;
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sqlTemplate = sqlQueryLoader.getQuery("user-queries.sql", "Load friends for users (batch, placeholders to be injected)");
        String sql = String.format(sqlTemplate, placeholders);
        List<Object> params = new ArrayList<>(ids);
        Map<Long, Set<Long>> userIdToFriendIds = new HashMap<>();
        jdbcTemplate.query(sql, rs -> {
            long friendId = rs.getLong("friend_id");
            long userId = rs.getLong("user_id");
            userIdToFriendIds.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        }, params.toArray());
        for (User u : users) {
            Set<Long> friends = userIdToFriendIds.getOrDefault(u.getId(), new HashSet<>());
            u.setFriends(friends);
            u.setFriendsCount(friends.size());
        }
    }
}