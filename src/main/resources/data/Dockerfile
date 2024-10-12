FROM mysql

# Ustaw zmienne środowiskowe
ENV MYSQL_ROOT_PASSWORD=rootP$w4
ENV MYSQL_DATABASE=vinted

COPY schema.sql /docker-entrypoint-initdb.d/
COPY data.sql /docker-entrypoint-initdb.d/

EXPOSE 3306

# plik nie gotowy
# schema.sql i data.sql nie są uruchamiane po starcie kontrolera - więc ma pustą bazę bez tabel
# docker build -t custom-vinted .
# docker run -d --name vintedDB -p 3306:3306 custom-vinted