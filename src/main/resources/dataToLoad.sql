use vinted;

insert into roles (name) values ('ROLE_ADMIN');
insert into roles (name) values ('ROLE_USER');

-- creating users

insert into vinted.users (email, name, password, username) values ('admin@email.com', 'admin',
                                                                   '$2a$10$Kcv0RMReIoG7unGNEdqbaO/oCvDkRHJ6.gjQ47bY6on98Ysvpy/7i', 'adminUsername');

insert into vinted.users (email, name, password, username) values ('user1@email.com', 'user1',
                                                                   '$2a$10$BO38kLt14mkRORfJOf0SRO4XOfsegJIJFLXze50hLqhrlbMXIeJem', 'username1');

insert into vinted.users (email, name, password, username) values ('user2@email.com', 'user2',
                                                                   '$2a$10$47m4oq68rn2phtIzCSIaEuIEA1ONGWEzcR590cRi2lXwwCqmpO0tm', 'username2');
-- give users specific roles
insert into vinted.users_roles (user_id, role_id) values (1, 1);
insert into vinted.users_roles (user_id,role_id) values (2, 2);
insert into vinted.users_roles (user_id,role_id) values (3, 2);

INSERT INTO categories (name, description, created_at, updated_at)
VALUES ('T-Shirts', 'Category for various T-Shirts', DATE(NOW()), DATE(NOW()));
INSERT INTO categories (name, description, created_at, updated_at)
VALUES ('Jeans', 'Category for different styles of Jeans', DATE(NOW()), DATE(NOW()));
INSERT INTO categories (name, description, created_at, updated_at)
VALUES ('Dresses', 'Category for elegant and casual Dresses', DATE(NOW()), DATE(NOW()));
INSERT INTO categories (name, description, created_at, updated_at)
VALUES ('Skirts', 'Category for various types of Skirts', DATE(NOW()), DATE(NOW()));
INSERT INTO categories (name, description, created_at, updated_at)
VALUES ('Blouses', 'Category for stylish Blouses', DATE(NOW()), DATE(NOW()));



INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id)
            VALUES ('T-Shirt', 'Comfortable cotton t-shirt', 19.99, 'M', DATE(NOW()), DATE(NOW()), 'Cotton', 10, 1, 2);
INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id)
            VALUES ('Jeans', 'Stylish blue jeans', 49.99, 'L', DATE(NOW()), DATE(NOW()), 'Denim', 20, 2, 3);
INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id)
            VALUES ('Dress', 'Elegant black dress', 79.99, 'S', DATE(NOW()), DATE(NOW()), 'Silk', 15, 3, 2);
INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id)
            VALUES ('Skirt', 'Green summer skirt', 29.99, 'M', DATE(NOW()), DATE(NOW()), 'Linen', 5, 4, 3);
INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id)
            VALUES ('Blouse', 'White elegant blouse', 39.99, 'L', DATE(NOW()), DATE(NOW()), 'Cotton', 8, 5, 2);
INSERT INTO clothes (name, description, price, size, created_at, updated_at, material, views, category_id, user_id)
            VALUES ('White T-Shirt', 'Comfortable cotton t-shirt', 24.99, 'M', DATE(NOW()), DATE(NOW()), 'Cotton', 10, 1, 2);
