BEGIN;

CREATE TABLE IF NOT EXISTS public.director
(
    id SERIAL NOT NULL,
    fio text NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.movie
(
    id SERIAL NOT NULL,
    title text NOT NULL,
    year integer NOT NULL,
    director bigint NOT NULL,
    duration time without time zone NOT NULL,
    rating float4 NOT NULL,
    genre text NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.user
(
    id SERIAL NOT NULL,
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
    REFERENCES public.director (id) MATCH SIMPLE
    ON UPDATE NO ACTION
       ON DELETE NO ACTION
    NOT VALID;

END;