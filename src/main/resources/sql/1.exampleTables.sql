
DROP TABLE IF EXISTS example007.country CASCADE;
CREATE TABLE IF NOT EXISTS example007.country (
                                                  id SERIAL CONSTRAINT country_pk PRIMARY KEY,
                                                  name varchar(255) NOT NULL
);

DROP TABLE IF EXISTS example007.city CASCADE;
CREATE TABLE IF NOT EXISTS example007.city (
                                               id SERIAL CONSTRAINT city_pk PRIMARY KEY,
                                               name varchar(255) NOT NULL UNIQUE,
                                               country_id integer NOT NULL
);

