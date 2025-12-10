-- Connect to the application database
\c star-wars-planets-db

-- Create the planets table
CREATE TABLE IF NOT EXISTS planets
(
    id uuid NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    terrain character varying(255) COLLATE pg_catalog."default" NOT NULL,
    climate character varying(255) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    updated_at timestamp(6) without time zone NOT NULL,
    movie_appearances integer,
    CONSTRAINT planets_pkey PRIMARY KEY (id),
    CONSTRAINT planets_name_key UNIQUE (name)
);

-- Create index for performance
CREATE INDEX IF NOT EXISTS idx_planets_name ON planets(name);
