package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationTool;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private static final String PROGRAM_LEVEL = "UserService";

    @Autowired
    public UserService(UserStorage userStorage, FilmStorage filmStorage) { // Изменить конструктор
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void delete(Long userId) {
        String message;

        if (userId == null || userId < 1) {
            message = String.format(
                    "%s : Попытка удалить user по ID = %s",
                    PROGRAM_LEVEL, String.valueOf(userId)
            );
            log.warn(message);
            throw new ValidationException(message);
        }

        if (userStorage.delete(userId) == false) {
            message = String.format(
                    "%s : User с ID = %s не найден в приложении",
                    PROGRAM_LEVEL, String.valueOf(userId));
            log.warn(message);
            throw new NotFoundException(message);
        }

        message = String.format(
                "%s : User ID %s успешно удален",
                PROGRAM_LEVEL, String.valueOf(userId));
        log.info(message);
    }

    public List<User> getAllUsers() {
        return List.copyOf(userStorage.getAllUsers());
    }

    public User getUserById(Long id) {
        ValidationTool.checkForNull(id, PROGRAM_LEVEL, "User не может быть получен по ID = null");

        User user = userStorage.getUserById(id);

        log.info(PROGRAM_LEVEL + ": Объект user успешно найден по ID");
        return user;
    }

    public User create(User user) {
        ValidationTool.userCheck(user, PROGRAM_LEVEL);

        String validUserName = user.getName();
        if (validUserName == null || validUserName.isBlank()) {
            validUserName = user.getLogin();
        }

        User validUser = new User(
                user.getId(),
                validUserName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        return userStorage.create(validUser);
    }

    public User update(User user) {
        ValidationTool.userCheck(user, PROGRAM_LEVEL);

        ValidationTool.checkId(user.getId(), PROGRAM_LEVEL, "user не может быть обновлен, некорректный id:"
                + user.getId());

        getUserById(user.getId());

        String validUserName = user.getName();
        if (validUserName == null || validUserName.isBlank()) {
            validUserName = user.getLogin();
        }

        User validUser = new User(
                user.getId(),
                validUserName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        return userStorage.update(validUser);
    }

    public void addFriend(Long userId, Long friendId) {
        ValidationTool.checkId(userId, friendId, PROGRAM_LEVEL, "Запрос на добавление друга, ID некорректен");

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIdsSet = userStorage.getUserFriendsIdsById(userId);
        if (!(userFriendsIdsSet.contains(friendId))) {
            userStorage.addFriend(userId, friendId);
            log.info("Друг успешно добавлен");
        } else {
            log.info("Друг был добавлен ранее");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        ValidationTool.checkId(userId, friendId, PROGRAM_LEVEL, "Друг не может быть удален ID = null");

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        if (userFriendsIds.contains(friendId)) {
            userStorage.removeFriend(userId, friendId);
            log.info("Друг успешно удален");
        } else {
            log.info("друг не может быть удален - отсутствует в списке друзей");
        }
    }

    public List<User> getAllFriendsById(Long userId) {
        ValidationTool.checkId(userId, PROGRAM_LEVEL, "Cписок друзей не может быть получен по некорректному ID:"
                + userId);

        userStorage.getUserById(userId);

        List<User> users = userStorage.getAllFriendsById(userId);
        log.info("Список всех друзей пользователя успешно создан");
        return List.copyOf(users);
    }

    public List<User> getAllCommonFriendsByIds(Long userId, Long anotherUserId) {
        ValidationTool.checkId(userId, anotherUserId, PROGRAM_LEVEL, "Cписок общих друзей " +
                "не может быть получен, ID некорректен");

        userStorage.getUserById(userId);
        userStorage.getUserById(anotherUserId);

        Set<Long> setOfUserFriendsIds = userStorage.getUserFriendsIdsById(userId);
        Set<Long> setOfAnotherUserFriendsIds = userStorage.getUserFriendsIdsById(anotherUserId);

        if (setOfUserFriendsIds == null || setOfAnotherUserFriendsIds == null) {
            log.warn("UserService: Не удалось получить объекты User по ID - не найдены в приложении");
            throw new NotFoundException("UserService: объекты User не найдены в приложении");
        }

        Set<Long> resultOfIntersection = setOfUserFriendsIds.stream()
                .filter(setOfAnotherUserFriendsIds::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = userStorage.getUsersByIdSet(resultOfIntersection);
        log.info("Список всех общих друзей пользователей успешно создан");
        return List.copyOf(commonFriends);
    }

    public boolean validUserId(Long userId) {
        return userStorage.validUserId(userId);
    public List<Film> getRecommendations(Long userId) {
        ValidationTool.checkId(userId, PROGRAM_LEVEL, "Рекомендации не могут быть получены по некорректному ID");

        // Получаем пользователя для проверки его существования
        userStorage.getUserById(userId);

        // Получаем лайки текущего пользователя
        List<Long> userLikedFilms = userStorage.getLikedFilmsByUserId(userId);

        if (userLikedFilms.isEmpty()) {
            log.info("У пользователя {} нет лайков, возвращаем пустые рекомендации", userId);
            return List.of();
        }

        // Получаем всех пользователей
        List<User> allUsers = userStorage.getAllUsers();

        // Ищем пользователя с максимальным пересечением лайков
        Long mostSimilarUserId = findMostSimilarUser(userId, userLikedFilms, allUsers);

        if (mostSimilarUserId == null) {
            log.info("Для пользователя {} не найден пользователь с похожими вкусами", userId);
            return List.of();
        }

        // Получаем лайки похожего пользователя
        List<Long> similarUserLikedFilms = userStorage.getLikedFilmsByUserId(mostSimilarUserId);

        // Находим фильмы, которые понравились похожему пользователю, но не текущему
        List<Long> recommendedFilmIds = similarUserLikedFilms.stream()
                .filter(filmId -> !userLikedFilms.contains(filmId))
                .collect(Collectors.toList());

        if (recommendedFilmIds.isEmpty()) {
            log.info("Для пользователя {} нет рекомендаций", userId);
            return List.of();
        }

        // Получаем объекты фильмов
        List<Film> recommendedFilms = filmStorage.getFilmsByIds(recommendedFilmIds);

        log.info("Для пользователя {} найдено {} рекомендаций", userId, recommendedFilms.size());
        return recommendedFilms;
    }

    private Long findMostSimilarUser(Long userId, List<Long> userLikedFilms, List<User> allUsers) {
        Long mostSimilarUserId = null;
        int maxCommonLikes = 0;

        Set<Long> userLikedSet = new HashSet<>(userLikedFilms);

        for (User otherUser : allUsers) {
            if (otherUser.getId().equals(userId)) {
                continue; // Пропускаем текущего пользователя
            }

            List<Long> otherUserLikedFilms = userStorage.getLikedFilmsByUserId(otherUser.getId());
            if (otherUserLikedFilms.isEmpty()) {
                continue;
            }

            Set<Long> otherUserLikedSet = new HashSet<>(otherUserLikedFilms);

            // Находим пересечение лайков
            Set<Long> intersection = new HashSet<>(userLikedSet);
            intersection.retainAll(otherUserLikedSet);

            if (intersection.size() > maxCommonLikes) {
                maxCommonLikes = intersection.size();
                mostSimilarUserId = otherUser.getId();
            }
        }

        return mostSimilarUserId;
    }
}