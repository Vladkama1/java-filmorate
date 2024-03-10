DROP TABLE IF EXISTS films_users;
DROP TABLE IF EXISTS films_directors;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS films_genres;
DROP TABLE IF EXISTS reviews_rate;
DROP TABLE IF EXISTS reviews;
DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS films;

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     varchar(100) not null,
    email    varchar(100) not null,
    login    varchar(100) not null,
    birthday date
);

CREATE TABLE IF NOT EXISTS friendships
(
    status   boolean DEFAULT FALSE,
    user1_id int references users (id) ON DELETE CASCADE,
    user2_id int references users (id) ON DELETE CASCADE,
    primary key (user1_id, user2_id)
);

CREATE TABLE IF NOT EXISTS mpa
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) not null
);

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(100) not null,
    description  varchar(500),
    release_date date,
    duration     int,
    mpa_id       int references mpa (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) not null
);
CREATE TABLE IF NOT EXISTS directors
(
    id   INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(100) not null
);

CREATE TABLE IF NOT EXISTS films_users
(
    user_id int references users (id) ON DELETE CASCADE,
    film_id int references films (id) ON DELETE CASCADE,
    primary key (user_id, film_id)
);

CREATE TABLE IF NOT EXISTS films_genres
(
    film_id  int references films (id) ON DELETE CASCADE,
    genre_id int references genres (id) ON DELETE CASCADE,
    primary key (genre_id, film_id)
);

CREATE TABLE IF NOT EXISTS films_directors
(
    film_id     int references films (id) ON DELETE CASCADE,
    director_id int references directors (id) ON DELETE CASCADE,
    primary key (director_id, film_id)
);

CREATE TABLE IF NOT EXISTS reviews
(
    id               INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content          varchar(200),
    is_positive      boolean,
    user_Id          int references users (id) ON DELETE CASCADE,
    film_Id          int references films (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews_rate
(
    user_id int references users (id) ON DELETE CASCADE,
    review_id int references reviews (id) ON DELETE CASCADE,
    rate boolean,
    primary key (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS events
(
    ID         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    USER_ID    INTEGER,
    OPERATION  ENUM ('REMOVE', 'ADD', 'UPDATE'),
    EVENT_TYPE ENUM ('LIKE', 'REVIEW', 'FRIEND'),
    ENTITY_ID  INTEGER,
    TIMESTAMP  TIMESTAMP default LOCALTIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);