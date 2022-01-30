create database chat;

create table persons(
                        id serial primary key,
                        name varchar
);

create table rooms(
                      id serial primary key,
                      description varchar,
                      person_id int references persons(id)
);

create table messages(
                         id serial primary key,
                         text varchar,
                         person_id int references persons(id),
                         room_id int references rooms(id)
);