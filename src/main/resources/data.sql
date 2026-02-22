--INSERT INTO films(name, description, duration) VALUES('фильм', 'описание фильма', 150);

-- Справочник рейтингов MPA
MERGE INTO ratings (rating_id, name) KEY(rating_id) VALUES (1, 'G');
MERGE INTO ratings (rating_id, name) KEY(rating_id) VALUES (2, 'PG');
MERGE INTO ratings (rating_id, name) KEY(rating_id) VALUES (3, 'PG-13');
MERGE INTO ratings (rating_id, name) KEY(rating_id) VALUES (4, 'R');
MERGE INTO ratings (rating_id, name) KEY(rating_id) VALUES (5, 'NC-17');

-- Справочник жанров
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (1, 'Комедия');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (2, 'Драма');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (4, 'Триллер');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (5, 'Документальный');
MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES (6, 'Боевик');