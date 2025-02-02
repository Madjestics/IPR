
CREATE TABLE IF NOT EXISTS public.director
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    fio text NOT NULL UNIQUE,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.movie
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    title text NOT NULL UNIQUE,
    year integer NOT NULL,
    director bigint NOT NULL,
    duration time without time zone NOT NULL,
    rating float NOT NULL,
    genre text NOT NULL,
    filepath text,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS public.user
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    username varchar(64) NOT NULL UNIQUE,
    password varchar(1024) NOT NULL,
    role varchar(32) NOT NULL,
    enabled boolean,
    created_at timestamp,
    updated_at timestamp,
    PRIMARY KEY (id)
    );

ALTER TABLE IF EXISTS public.movie
    ADD FOREIGN KEY (director)
    REFERENCES public.director (id);
