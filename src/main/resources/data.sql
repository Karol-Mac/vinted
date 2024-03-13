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
INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Denim Skirt',CURRENT_TIMESTAMP , 'A-line denim skirt, versatile style', 42.25, 'XS', 10, 1);
insert into vinted.clothe_images (clothe_id, images) values(1, 'skirt1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(1, 'skirt2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Classic Denim Jacket',CURRENT_TIMESTAMP , 'Timeless blue denim jacket, suitable for any occasion', 65.0, 'M', 6, 1);
insert into vinted.clothe_images (clothe_id, images) values(2, 'jacket1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(2, 'jacket2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Cropped Sweater',CURRENT_TIMESTAMP , 'Cozy cropped sweater for a stylish winter look', 38.75, 'S', 5,  1);
insert into vinted.clothe_images (clothe_id, images) values(3, 'sweater1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(3, 'sweater2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Casual Shorts',CURRENT_TIMESTAMP , 'Comfortable and casual shorts for warm days', 28.99, 'L', 4, 1);
insert into vinted.clothe_images (clothe_id, images) values(4, 'shorts1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(4, 'shorts2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Knit Hoodie',CURRENT_TIMESTAMP , 'Knit hoodie with a unique texture, perfect for fall', 55.5, 'M', 3, 1);
insert into vinted.clothe_images (clothe_id, images) values(5, 'hoodie1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(5, 'hoodie2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Floral Dress',CURRENT_TIMESTAMP , 'Elegant floral dress with a feminine touch', 68.25, 'S', 7, 1);
insert into vinted.clothe_images (clothe_id, images) values(6, 'dress1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(6, 'dress2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Graphic Print T-Shirt',CURRENT_TIMESTAMP , 'Creative graphic print t-shirt with a modern design', 22.0, 'L', 8, 1);
insert into vinted.clothe_images (clothe_id, images) values(7, 'tshirt1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(7, 'tshirt2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Classic Blue Jeans',CURRENT_TIMESTAMP , 'Timeless blue jeans for a versatile look', 50.75, 'M', 9, 1);
insert into vinted.clothe_images (clothe_id, images) values(8, 'jeans1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(8, 'jeans2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('A-Line Skirt',CURRENT_TIMESTAMP , 'Chic A-line skirt for a sophisticated style', 38.5, 'XS', 10, 3);
insert into vinted.clothe_images (clothe_id, images) values(9, 'skirt3.jpg');
insert into vinted.clothe_images (clothe_id, images) values(9, 'skirt4.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Warm Winter Coat',CURRENT_TIMESTAMP , 'Cozy winter coat to keep you warm in cold weather', 95.99, 'L', 11, 3);
insert into vinted.clothe_images (clothe_id, images) values(10, 'coat1.jpg');
insert into vinted.clothe_images (clothe_id, images) values(10, 'coat2.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Leather Biker Jacket',CURRENT_TIMESTAMP , 'Classic leather biker jacket for edgy look', 120.0, 'M', 6, 3);
insert into vinted.clothe_images (clothe_id, images) values(11, 'jacket3.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Striped Sweater',CURRENT_TIMESTAMP , 'Casual striped sweater for a cozy vibe', 32.5, 'S', 5, 3);
insert into vinted.clothe_images (clothe_id, images) values(12, 'sweater3.jpg');
insert into vinted.clothe_images (clothe_id, images) values(12, 'sweater4.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Cargo Shorts',CURRENT_TIMESTAMP , 'Functional cargo shorts for outdoor activities', 34.99, 'L', 4, 3);
insert into vinted.clothe_images (clothe_id, images) values(13, 'shorts3.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Printed Hoodie',CURRENT_TIMESTAMP , 'Hoodie with unique printed design, trendy style', 42.0, 'M', 3, 3);
insert into vinted.clothe_images (clothe_id, images) values(14, 'hoodie3.jpg');
insert into vinted.clothe_images (clothe_id, images) values(14, 'hoodie4.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Elegant Evening Dress',CURRENT_TIMESTAMP , 'Elegant evening dress for special occasions', 85.0, 'S', 7, 3);
insert into vinted.clothe_images (clothe_id, images) values(15, 'dress3.png');
insert into vinted.clothe_images (clothe_id, images) values(15, 'dress4.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Vintage Graphic T-Shirt',CURRENT_TIMESTAMP , 'Vintage-style graphic t-shirt for retro lovers', 18.5, 'L', 8, 3);
insert into vinted.clothe_images (clothe_id, images) values(16, 'tshirt3.jpg');
insert into vinted.clothe_images (clothe_id, images) values(16, 'tshirt4.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Distressed Jeans',CURRENT_TIMESTAMP , 'Distressed jeans with a grunge touch', 48.75, 'M', 9, 3);
insert into vinted.clothe_images (clothe_id, images) values(17, 'jeans3.jpg');
insert into vinted.clothe_images (clothe_id, images) values(17, 'jeans4.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Floral Midi Skirt',CURRENT_TIMESTAMP , 'Floral midi skirt with a flowy silhouette', 35.0, 'XS', 10, 3);
insert into vinted.clothe_images (clothe_id, images) values(18, 'skirt5.jpg');

INSERT INTO vinted.clothes (name, added_date, description, price, size, category_id, user_id)
VALUES ('Wool Blend Coat',CURRENT_TIMESTAMP , 'Stylish wool blend coat for colder days', 78.99, 'L', 11, 3);
insert into vinted.clothe_images (clothe_id, images) values(19, 'coat3.jpg');
