create table users(
    id serial primary key,
    username varchar(255) not null unique,
    password varchar not null,
    role
);