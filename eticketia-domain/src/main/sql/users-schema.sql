DROP SCHEMA IF EXISTS users CASCADE;
CREATE SCHEMA users;


CREATE TABLE IF NOT EXISTS users.UserTable (
    uid             VARCHAR,
    email           VARCHAR(100) NOT NULL,
    display_name    VARCHAR(50),
    birthDate       DATE        NOT NULL,
    registered_at   TIMESTAMP   NOT NULL,
    last_updated    TIMESTAMP   NOT NULL,

    CONSTRAINT PK_UserTable PRIMARY KEY (uid)
);