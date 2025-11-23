create table users(
    id serial primary key,
    username varchar(255) not null unique,
    password varchar(255) not null,
    role varchar(255) not null
);