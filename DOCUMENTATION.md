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
 - wywołać metodę main z klasy  [VintedApplication](src/main/java/com/restapi/vinted/VintedApplication.java)
    - lub wpisać w terminalu `./mvnw spring-boot:run` w katalogu projektu

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
| GET    | /api/conversations/buying          | wyświetl konwersację (jako kupujący)   | 200             | ADMIN, USER         |
| GET    | /api/conversations/selling         | wyświetl konwersację (jako właściciel) | 200             | ADMIN, USER         |
| POST   | /api/conversations                 | utwórz nową konwersację                | 204             | ADMIN, USER         |
| GET    | /api/conversations/{id}            | wyświetl konwersację (wiadomości)      | 200             | ADMIN, USER         |
JESZCZE METODA DO PISANIA WIADOMOŚCI
 
^ - w przypadku prawidłowego zapytania <br>
^^ - role użytkowników, którzy mogą wykonać tą operację

