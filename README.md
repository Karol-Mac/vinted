# Vinted REST API

### To run this project on your device you have to:

- [x] Install java (20)
- [x] Install env - I made it in IntelIj, but it doesn't matter
- [x] create DB and connect to it (in application.properties file)

Hibernate should create tables automatically 
#### Hope you enjoy ;D

Application is secured with JWT token,
so after you create user
(on link http://localhost:8080/api/auth/register)

you need to copy auto-generated token and paste it whenever you want to use a resource
(if api-tester won't do it itself)

__Only admin users can create new categories__

Roles ROLE_ADMIN and ROLE_USER are created automatically, but new user **default role is ROLE_USER**

if you want to create a user with **admin rights**, you have to change their role manually (in DB: change role_id in table users_roles)

_MyClothes is controller created to manage individual user's clothes_

## MYCLOTHES

| REQUEST STATUS | LINKS                       | REQUEST CODE  | RETURN TYPE                      | TYPE                | ACCESS |
|----------------|-----------------------------|---------------|----------------------------------|---------------------|--------|
| GET            | `/api/myclothes/{id}`       | 200 (OK)      | `ResponseEntity<ClotheDto>`      | `long`              | USER   |
| POST           | `/api/myclothes`            | 201 (Created) | `ResponseEntity<ClotheDto>`      | `ClotheDto`         | USER   |
| PUT            | `/api/myclothes/{id}`       | 200 (OK)      | `ResponseEntity<ClotheDto>`      | `long`, `ClotheDto` | USER   |
| DELETE         | `/api/myclothes/{id}`       | 200 (OK)      | `ResponseEntity<String>`         | `long`              | USER   |
| GET            | `/api/myclothes?parameters` | 200 (OK)      | `ResponseEntity<ClotheResponse>` |                     | USER   |

## CATEGORY

| REQUEST STATUS | LINKS                          | REQUEST CODE  | RETURN TYPE                       | TYPE                  | ACCESS |
|----------------|--------------------------------|---------------|-----------------------------------|-----------------------|--------|
| GET            | `/api/categories`              | 200 (OK)      | `ResponseEntity<List<ClotheDto>>` | -                     | USER   |
| GET            | `/api/categories/{categoryId}` | 200 (OK)      | `ResponseEntity<CategoryDto>`     | `long`                | USER   |
| POST           | `/api/categories`              | 201 (Created) | `ResponseEntity<CategoryDto>`     | `CategoryDto`         | ADMIN  |
| PUT            | `/api/categories/{categoryId}` | 200 (OK)      | `ResponseEntity<ClotheDto>`       | `long`, `CategoryDto` | ADMIN  |
| DELETE         | `/api/categories/{categoryId}` | 200 (OK)      | `ResponseEntity<ClotheDto>`       | `long`                | ADMIN  |


## CLOTHE

| REQUEST  STATUS | LINKS                                             | REQUEST CODE  | RETURN TYPE                      | TYPE                          | ACCESS |
|-----------------|---------------------------------------------------|---------------|----------------------------------|-------------------------------|--------|
| GET             | `/api/categories/{categoryId}/clothes/{clotheId}` | 200 (OK)      | `ResponseEntity<ClotheDto>`      | `long`, `long`                | USER   |
| GET             | `/api/categories/{categoryId}/clothes?parameters` | 200 (OK)      | `ResponseEntity<ClotheResponse>` | `long`                        | USER   |


## AUTH

| REQUEST STATUS | LINKS                | REQUEST CODE  | RETURN TYPE                       | PARAMETERS    | ACCESS |
|----------------|----------------------|---------------|-----------------------------------|---------------|--------|
| POST           | `/api/auth/login`    | 200 (OK)      | `ResponseEntity<JWTAuthResponse>` | `LoginDto`    | USER   |
| POST           | `/api/auth/register` | 201 (Created) | `ResponseEntity<String>`          | `RegisterDto` | USER   |