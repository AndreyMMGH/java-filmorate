# java-filmorate
Template repository for Filmorate project.
![Схема базы данных.](images/database.png)

**-- Вывод всех полей и записей из таблицы фильмы**<br>
SELECT *<br>
FROM films;

**-- Вывод всех полей записи под id=1**<br>
SELECT *<br>
FROM films<br>
WHERE id_film = 1;

**-- Вывод записи и всех полей из таблицы Фильмы по определенному жанру**<br>
SELECT f.*<br>
FROM films f<br>
JOIN film_genres fg ON f.id_film = fg.id_film<br>
JOIN genres g ON fg.id_genre = g.id_genre<br>
WHERE g.name = '*****';

**-- Вывод записей и всех полей из таблицы Фильмы по определенному рейтингу**<br>
SELECT f.*<br>
FROM films f<br>
JOIN ratings r ON f.id_rating = r.id_rating<br>
WHERE r.name = '*****';

**-- Вывод количества лайков по названию фильма**<br>
SELECT COUNT(*) AS like_count<br>
FROM film_likes fl<br>
JOIN films f ON fl.id_film = f.id_film<br>
WHERE f.name = '*****';

**-- Вывод записей и всех полей из таблицы Пользователи по названию фильма**<br>
SELECT u.*<br>
FROM users u<br>
JOIN film_likes fl ON u.id_user = fl.id_user<br>
JOIN films f ON fl.id_film = f.id_film<br>
WHERE f.name = '*****';

**-- Вывод записей и всех полей из таблицы Фильмы, которые пролайкал определенный пользователь**<br>
SELECT f.*<br>
FROM films f<br>
JOIN film_likes fl ON f.id_film = fl.id_film<br>
JOIN users u ON fl.id_user = u.id_user<br>
WHERE u.login = '*****';

**-- Вывод записей и всех полей из таблицы Фильмы + поле по количеству лайков с группировкой и сортировкой по убыванию и определенному количеству**<br>
SELECT f.*, <br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;COUNT(fl.id_film) AS like_count<br>
FROM films f<br>
LEFT JOIN film_likes fl ON f.id_film = fl.id_film<br>
GROUP BY f.id_film<br>
ORDER BY like_count DESC<br>
LIMIT ***;

**-- Вывод статуса дружбы пользователей**<br>
SELECT sf.name AS friendship_status<br>
FROM friends f<br>
JOIN status_friendship sf ON f.id_status_friendship = sf.id_status_friendship<br>
WHERE f.id_user = ***<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;AND f.id_friend_user = ***** 