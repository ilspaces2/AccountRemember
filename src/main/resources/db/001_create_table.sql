create table if not exists site (
    id serial primary key,
    name text,
    login text,
    password text
);