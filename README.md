# Filmorate - Схема Базы Данных

## Описание проекта

**Filmorate** — это база данных для управления информацией о фильмах, пользователях и их взаимодействиях. Система позволяет пользователям просматривать фильмы, добавлять их в избранное через лайки, управлять списком друзей и открывать новые фильмы через рекомендации друзей.

---
## Таблицы базы
<img width="1560" height="504" alt="Untitled" src="https://github.com/user-attachments/assets/a3aeeb03-f69e-4dc6-9a26-4cde87ee4e5e" />

### Нормализация

База данных разработана в соответствии с **3-й нормальной формой (3NF)**, что обеспечивает:

-  Минимизацию избыточности данных
-  Эффективное хранение информации
-  Целостность данных через внешние ключи
-  Упрощение обновления и удаления записей

---

## Описание Таблиц

 1. **rating** — Справочник рейтингов
Таблица содержит допустимые MPA-рейтинги для фильмов (G, PG, PG-13, R, NC-17 и т.д.).

 2. **genres** — Справочник жанров
Таблица содержит список всех возможных жанров фильмов.

 3. **users** — Информация о пользователях
Основная таблица для хранения профилей пользователей.

 4. **films** — Информация о фильмах
Основная таблица для хранения данных о фильмах.

 5. **friends** — Список друзей пользователей
Таблица хранит информацию о дружеских связях между пользователями.

 6. **likes** — Отметки "нравится" пользователей
Таблица содержит информацию о фильмах, которые понравились пользователям.

 7. **film_genres** — Связь фильмов и жанров
Таблица связи (связь многие-ко-многим) для распределения жанров по фильмам.

---

## Примеры SQL-запросов

### 1. Получение информации о фильме с рейтингом и жанрами

```sql
SELECT 
    f.film_id, 
    f.name, 
    f.description, 
    f.release_date, 
    f.duration, 
    r.name_rating AS rating, 
    STRING_AGG(g.genre_name, ', ') AS genres
FROM films f
LEFT JOIN rating r ON f.mpa_rating = r.rating_id
LEFT JOIN film_genres fg ON f.film_id = fg.film_id
LEFT JOIN genres g ON fg.genre_id = g.genre_id
WHERE f.film_id = 1
GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, r.name_rating;

---

### 2. Получение списка друзей пользователя

SELECT 
    u.user_id, 
    u.user_name, 
    u.email
FROM friends f
JOIN users u ON f.friend_id = u.user_id
WHERE f.user_id = 1;

---

### 3. Топ популярных фильмов

SELECT 
    f.film_id, 
    f.name, 
    COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.film_id = l.film_id
GROUP BY f.film_id, f.name
ORDER BY likes_count DESC
LIMIT 10;

---

### 4. Добавление нового фильма

INSERT INTO films (name, description, release_date, duration, mpa_rating)
VALUES (
    'Новый фильм', 
    'Описание нового фильма', 
    '2024-01-01', 
    120, 
    3
);

---

### 5. Добавление лайка к фильму

INSERT INTO likes (user_id, film_id)
VALUES (2, 1);

---

### 6. Добавление друга

INSERT INTO friends (user_id, friend_id)
VALUES (1, 3);

---

### 7. Получение общих друзей двух пользователей

SELECT 
    u.user_id, 
    u.user_name
FROM friends f1
JOIN friends f2 ON f1.friend_id = f2.friend_id
JOIN users u ON f1.friend_id = u.user_id
WHERE f1.user_id = 1 
  AND f2.user_id = 2;

---

### 8. Поиск фильмов по жанру

SELECT 
    f.film_id,
    f.name,
    f.description,
    f.release_date
FROM films f
JOIN film_genres fg ON f.film_id = fg.film_id
JOIN genres g ON fg.genre_id = g.genre_id
WHERE g.genre_name = 'Драма';

---

### 9. Получение всех пользователей, лайкнувших фильм

SELECT 
    u.user_id,
    u.user_name,
    u.email
FROM likes l
JOIN users u ON l.user_id = u.user_id
WHERE l.film_id = 1;

---

### 10. Удаление лайка

DELETE FROM likes
WHERE film_id = 1 AND user_id = 2;

---

### 11. Удаление друга

DELETE FROM friends
WHERE user_id = 1 AND friend_id = 3;

---

### 12. Получение рекомендаций фильмов от друзей
SELECT DISTINCT
    f.film_id,
    f.name,
    f.description,
    COUNT(l.user_id) AS friends_who_liked
FROM films f
JOIN likes l ON f.film_id = l.film_id
JOIN friends fr ON l.user_id = fr.friend_id
WHERE fr.user_id = 1
  AND f.film_id NOT IN (
      SELECT l2.film_id 
      FROM likes l2 
      WHERE l2.user_id = 1
  )
GROUP BY f.film_id, f.name, f.description
ORDER BY friends_who_liked DESC;

###Ссылка на базу данных
https://dbdiagram.io/d/6921615e228c5bbc1a009d38
