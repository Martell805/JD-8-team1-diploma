-- liquibase formatted sql

--changeset mara:1

create table ads(
    id          BIGSERIAL PRIMARY KEY,
    title       TEXT,
    price       INTEGER,
    description TEXT,
    imageId     INTEGER
);

-- changeSet andrew:2
CREATE TABLE users
(
    id         SERIAL NOT NULL PRIMARY KEY,
    email      TEXT,
    first_name TEXT,
    last_name  TEXT,
    phone      TEXT,
    reg_date   DATE   NOT NULL,
    id_image   INTEGER,
    password   TEXT,
    username   TEXT
);

-- changeSet igor:1
Create TABLE comment
(
    id SERIAL NOT NULL PRIMARY KEY,
    author INTEGER,
    text TEXT,
    createdAt TEXT
);