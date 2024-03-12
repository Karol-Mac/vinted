-- creating roles
insert into vinted.roles (name) values ('ROLE_USER');
insert into vinted.roles (name) values ('ROLE_ADMIN');

-- creating users
insert into vinted.users (email, name, password, username) values ('user1@email.com', 'user1',
                                    '$2a$10$BO38kLt14mkRORfJOf0SRO4XOfsegJIJFLXze50hLqhrlbMXIeJem', 'username1');

insert into vinted.users (email, name, password, username) values ('admin@email.com', 'admin',
                                    '$2a$10$Kcv0RMReIoG7unGNEdqbaO/oCvDkRHJ6.gjQ47bY6on98Ysvpy/7i', 'adminUsername');

insert into vinted.users (email, name, password, username) values ('user2@email.com', 'user2',
                                        '$2a$10$47m4oq68rn2phtIzCSIaEuIEA1ONGWEzcR590cRi2lXwwCqmpO0tm', 'username2');


-- give users specific roles
insert into vinted.users_roles (user_id, role_id) values (1, 1);
insert into vinted.users_roles (user_id, role_id) values (2, 2);
insert into vinted.users_roles (user_id, role_id) values (3, 1);


-- inserting categories
INSERT INTO vinted.categories (name) VALUES ('Leggings');
INSERT INTO vinted.categories (name) VALUES ('Chinos');
INSERT INTO vinted.categories (name) VALUES ('Hoodie');
INSERT INTO vinted.categories (name) VALUES ('Shorts');
INSERT INTO vinted.categories (name) VALUES ('Sweater');
INSERT INTO vinted.categories (name) VALUES ('Jacket');
INSERT INTO vinted.categories (name) VALUES ('Dress');
INSERT INTO vinted.categories (name) VALUES ('T-Shirt');
INSERT INTO vinted.categories (name) VALUES ('Jeans');
INSERT INTO vinted.categories (name) VALUES ('Skirt');
INSERT INTO vinted.categories (name) VALUES ('Coat');
INSERT INTO vinted.categories (name) VALUES ('Blouse');

-- Inserting clothes to db
INSERT INTO vinted.clothes (name, description, price, size, category_id, user_id)
VALUES ('Denim Skirt', 'A-line denim skirt, versatile style', 42.25, 'XS', 10, 1);
insert into vinted.clothe_images (clothe_id, images)
values(1, 'skirt1.jpg');
insert into vinted.clothe_images (clothe_id, images)
values(1, 'skirt2.jpg');

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Classic Denim Jacket', 'Timeless blue denim jacket, suitable for any occasion', 65.0, 'M', 6, '["jacket1.jpg", "jacket2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Cropped Sweater', 'Cozy cropped sweater for a stylish winter look', 38.75, 'S', 5, '["sweater1.jpg", "sweater2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Casual Shorts', 'Comfortable and casual shorts for warm days', 28.99, 'L', 4, '["shorts1.jpg", "shorts2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Knit Hoodie', 'Knit hoodie with a unique texture, perfect for fall', 55.5, 'M', 3, '["hoodie1.jpg", "hoodie2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Floral Dress', 'Elegant floral dress with a feminine touch', 68.25, 'S', 7, '["dress1.jpg", "dress2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Graphic Print T-Shirt', 'Creative graphic print t-shirt with a modern design', 22.0, 'L', 8, '["tshirt1.jpg", "tshirt2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Classic Blue Jeans', 'Timeless blue jeans for a versatile look', 50.75, 'M', 9, '["jeans1.jpg", "jeans2.jpg"]', 1);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('A-Line Skirt', 'Chic A-line skirt for a sophisticated style', 38.5, 'XS', 10, '["skirt3.jpg", "skirt4.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Warm Winter Coat', 'Cozy winter coat to keep you warm in cold weather', 95.99, 'L', 11, '["coat1.jpg", "coat2.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Leather Biker Jacket', 'Classic leather biker jacket for edgy look', 120.0, 'M', 6, '["jacket3.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Striped Sweater', 'Casual striped sweater for a cozy vibe', 32.5, 'S', 5, '["sweater3.jpg", "sweater4.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Cargo Shorts', 'Functional cargo shorts for outdoor activities', 34.99, 'L', 4, '["shorts3.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Printed Hoodie', 'Hoodie with unique printed design, trendy style', 42.0, 'M', 3, '["hoodie3.jpg", "hoodie4.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Elegant Evening Dress', 'Elegant evening dress for special occasions', 85.0, 'S', 7, '["dress3.jpg", "dress4.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Vintage Graphic T-Shirt', 'Vintage-style graphic t-shirt for retro lovers', 18.5, 'L', 8, '["tshirt3.jpg", "tshirt4.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Distressed Jeans', 'Distressed jeans with a grunge touch', 48.75, 'M', 9, '["jeans3.jpg", "jeans4.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Floral Midi Skirt', 'Floral midi skirt with a flowy silhouette', 35.0, 'XS', 10, '["skirt5.jpg"]', 3);

INSERT INTO vinted.clothes (name, description, price, size, category_id, images, user_id)
VALUES ('Wool Blend Coat', 'Stylish wool blend coat for colder days', 78.99, 'L', 11, '["coat3.jpg"]', 3);