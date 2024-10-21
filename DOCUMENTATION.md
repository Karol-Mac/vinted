# Dokumentacja projektu

## Uruchomienie aplikacji
#### Wymagania:
 - java 20 (lub wyższa)
 - zainstalowany i aktywny docker engine
 - wolne porty: 8080 i 3306

#### Uruchomienie bazy danych:
Baza danych jest automatycznie inicjalizowana jako komponent Docker <br>
 i zapełniana przykładowymi danymi przy starcie aplikacji.

#### Uruchomienie Serwera:
 - wywołać metodę main z klasy [VintedApplication](src/main/java/com/restapi/vinted/VintedApplication.java)
    - lub wpisać w terminalu `./mvnw spring-boot:run` w katalogu projektu

## Endpointy aplikacji:
| Metoda | Endpoint                           | Zastosowanie                           | Kod odpowiedzi^ | Role użytkowników^^ |
|--------|------------------------------------|----------------------------------------|-----------------|---------------------|
| GET    | /api/categories                    | Lista kategorii                        | 200             | PUBLIC              |
| POST   | /api/categories                    | Dodanie kategorii                      | 201             | ADMIN               |
| GET    | /api/categories/{id}               | pojedyncza Kategoria                   | 200             | PUBLIC              |
| PUT    | /api/categories/{id}               | aktualizacja kategorii                 | 200             | ADMIN               |
| DELETE | /api/categories/{id}               | usunięcie kategorii                    | 204             | ADMIN               |
| POST   | /api/auth/login                    | zalogowanie                            | 200             | PUBLIC              |
| POST   | /api/auth/register                 | rejestracja                            | 201             | PUBLIC              |
| GET    | /api/images/{imageName}            | pobranie obrazu po nazwie              | 200             | PUBLIC              |
| GET    | /api/clothes/category/{categoryId} | pobranie wszystkich ubrań z kategorii  | 200             | PUBLIC              |
| GET    | /api/clothes/{id}                  | pojedyncze ubranie                     | 200             | PUBLIC              |
| GET    | /api/clothes/my                    | ubrania sprzedawane przez użytkownika  | 200             | ADMIN, USER         |
| POST   | /api/clothes                       | dodanie ubrania                        | 201             | ADMIN, USER         |
| PUT    | /api/clothes/{id}                  | aktualizacja ubrania                   | 200             | ADMIN, USER         |
| DELETE | /api/clothes/{id}                  | usunięcie ubrania                      | 204             | ADMIN, USER         |
| POST   | /api/conversations                 | utwórz nową konwersację                | 204             | ADMIN, USER         |
| GET    | /api/conversations/buying          | wyświetl konwersację (jako kupujący)   | 200             | ADMIN, USER         |
| GET    | /api/conversations/selling         | wyświetl konwersację (jako właściciel) | 200             | ADMIN, USER         |
| GET    | /api/conversations/{id}            | wyświetl konwersację (wiadomości)      | 200             | ADMIN, USER         |
| GET    | /api/conversations/send            | wyślij nową wiadomość                  | 200             | ADMIN, USER         |
 
^ - w przypadku prawidłowego zapytania <br>
^^ - role użytkowników, którzy mogą wykonać tą operację

## Funkcjonalności aplikacji:
- zabezpieczenie ednpointów za pomocą tokenów JWT
- zarządzanie użytkownikami (system ról)
- zapisywanie zdjęć na serwerze
- CRUD ubrań
- CRUD kategorii (specjalne uprawnienia administratora)
- zarządzanie konwersacjami i wiadomościami

## Przykładowe użycie aplikacji:

### Rejestracja i logowanie użytkownika
Endpointy związane z rejestracją i logowaniem użytkownika są dostępne dla wszystkich użytkowników,
Dostępne są one w klasie [AuthController](src/main/java/com/restapi/vinted/controller/AuthController.java),
który następnie przekazuje zapytanie do serwisu [AuthService](src/main/java/com/restapi/vinted/service/impl/AuthServiceimpl.java).
1. Rejestracja nowego użytkownika:
   ```
   curl --location 'http://localhost:8080/api/auth/register' \
   --header 'Content-Type: application/json' \
   --data-raw '{   
   "name": "test",
   "username": "testUsername",
   "email": "test@email.com",
   "password": "Password123!"
   }'
   ```
   W przypadku poprawnej rejestracji, otrzymamy odpowiedź z kodem 201 i komunikat,
    niespełnienie walidacji po stronie serwera zwróci kod 400.
    Waldacja pól:

| Pole     | Opis                                                                                                                |
|----------|---------------------------------------------------------------------------------------------------------------------|
| name     | Nie może być puste                                                                                                  |
| username | Musi zawierać od 3 do 20 znaków, nie może być puste                                                                 |
| email    | Musi być w formacie poprawnego adresu email (musi zawierać '@'), nie może być puste                                 |
| password | Musi zawierać co najmniej: 1 wielką literę, 1 małą literę, 1 cyfrę, 1 znak specjalny, minimalna długość to 8 znaków |
w kodzie jest to klasa [RegisterDto](src/main/java/com/restapi/vinted/payload/RegisterDto.java)

2. Logowanie użytkownika:
    ```
   curl --location 'http://localhost:8080/api/auth/login' \
    --header 'Content-Type: application/json' \
    --data '{   
    "usernameOrEmail": "test@email.com",
    "password": "Password123!"
    }'
    ```
    W przypadku poprawnego logowania, otrzymamy odpowiedź z kodem 200 oraz Odpowiedź z tokenem:
    ```JSON
    {
        "accessToken": "example_JWT_access_token",
        "tokenType": "Bearer",
        "userId": 3,
        "usernameOrEmail": "user2@email.com",
        "role": "[ROLE_USER]"
    }
    ```
    Wprowadzenie niepopranych danych skutkuje kodem 403 i wyrzuceniem wyjątku:
   ```JSON
   {
       "timestamp": "2024-10-21T07:55:09.732+00:00",
       "message": "Wrong username/email or password",
       "details": "uri=/api/auth/login"
   }
    ```

### Ubrania
Endpointy związane z przeglądaniem oferty sklepu są dostępne dla wszystkich użytkowników,
    ale tutaj skupimy się na operacjach dla zalogowanych użytkowników (np. pisaniu wiadomości do włąścicieli i edycji ubrań).
Dostępne są one w klasie [ClotheController](src/main/java/com/restapi/vinted/controller/ClotheController.java),
który następnie przekazuje zapytanie do serwisu [ClothesService](src/main/java/com/restapi/vinted/service/impl/ClothesServiceImpl.java).

1. Pobranie wszystkich ubrań z danej kategorii:



### Konwersacje