## PLAN PRACY NA NAJBLIŻSZY CZAS

 -[x] zastanów się nad zmianą endpointów dla ubrań.

 -[ ] dodatkowe funkcje:
   -[x] dodanie licznika ubrań w kategorii i licznika wyświetleń w ubraniu
   -[ ] wysyłanie powiadomienia do sprzedawcy po zakupie jego ubrania
       (zrób oddzielny serwis do wysyłania mailu/SMS-ów)
     -[x] podepnij serwer RabbitMQ
     -[ ] utwórz encję konwersacji i wiadomości

  -[ ] dodanie zamówień
    -[ ] dodaj encję Order i Cart.
            Cart (koszyk) będzie przechowywał ubrania, które użytkownik chcę zakupić
            Order, to reprezentacja pojedynczego zamówienia - 1 sprzedawca, 1 kupujący
            W momencie kupienia wszystkich ubrań w koszyku tworzone jest kilka zamówień (sprawdź czy się da)
    -[ ] połącz się z dostawcą płatności: Adyen/Stripe/PayU <- możliwe, że są tam jakieś opłaty
    -[ ] system ocen/opinii użytkowników (po zakupie ubrań)

-[ ] zwieksz poziom API na 3 - żeby działało jak strona internetowa

 -[ ] napisz testy
   -[ ] najpierw jednostowe (dla kontrolera od strony klienta)
   -[ ] potem integracyjne (od strony serwera)
   -[ ] pamiętaj o security