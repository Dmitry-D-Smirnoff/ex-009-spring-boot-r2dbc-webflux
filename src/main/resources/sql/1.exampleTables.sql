
DROP TABLE IF EXISTS country CASCADE;
CREATE TABLE IF NOT EXISTS country (
                                                  id SERIAL CONSTRAINT country_pk PRIMARY KEY,
                                                  name varchar(255) NOT NULL
);

DROP TABLE IF EXISTS city CASCADE;
CREATE TABLE IF NOT EXISTS city (
                                               id SERIAL CONSTRAINT city_pk PRIMARY KEY,
                                               name varchar(255) NOT NULL UNIQUE,
                                               country_id integer NOT NULL
);

