FROM mysql

# Ustaw zmienne środowiskowe
ENV MYSQL_ROOT_PASSWORD=rootP$w4
ENV MYSQL_DATABASE=vinted

COPY schema.sql /docker-entrypoint-initdb.d/
COPY data.sql /docker-entrypoint-initdb.d/

EXPOSE 3306
