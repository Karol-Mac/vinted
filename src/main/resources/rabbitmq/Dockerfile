FROM rabbitmq:management

ENV RABBITMQ_DEFAULT_USER=guest
ENV RABBITMQ_DEFAULT_PASS=guest

EXPOSE 5672 15672

CMD ["rabbitmq-server"]


# docker build -t custom-rabbitmq .
# docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 custom-rabbitmq