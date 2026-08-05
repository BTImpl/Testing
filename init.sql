CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     fname VARCHAR(255),
                                     lname VARCHAR(255),
                                     age SMALLINT
);

insert into users(fname, lname, age) values ('Tamas','Bodis', 36);
insert into users(fname, lname, age) values ('Gabi','Bodis-Farkas', 37);
