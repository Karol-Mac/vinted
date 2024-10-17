delete from roles;
insert into vinted.roles (name) values ('ROLE_USER');
insert into vinted.roles (name) values ('ROLE_ADMIN');

delete from users;
insert into vinted.users (email, name, password, username) values ('user1@email.com', 'user1',
                                           '$2a$10$BO38kLt14mkRORfJOf0SRO4XOfsegJIJFLXze50hLqhrlbMXIeJem', 'user1');

insert into vinted.users (email, name, password, username) values ('admin@email.com', 'admin',
                                           '$2a$10$Kcv0RMReIoG7unGNEdqbaO/oCvDkRHJ6.gjQ47bY6on98Ysvpy/7i', 'admin');

insert into vinted.users (email, name, password, username) values ('user2@email.com', 'user2',
                                            '$2a$10$47m4oq68rn2phtIzCSIaEuIEA1ONGWEzcR590cRi2lXwwCqmpO0tm', 'user2');

# delete from users_roles;
# insert into vinted.users_roles (user_id, role_id) values (1, 1);
# insert into vinted.users_roles (user_id,role_id) values (2, 2);
# insert into vinted.users_roles (user_id,role_id) values (3, 1);