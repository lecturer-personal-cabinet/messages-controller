FROM openjdk:8-alpine

RUN apk add unzip
RUN apk add bash

EXPOSE 8802
EXPOSE 9902
EXPOSE 9000

ADD ./target/universal/messages_controller-1.0.zip /

RUN unzip messages_controller-1.0.zip
RUN mv messages_controller-1.0 messages_controller

CMD ["messages_controller/bin/messages_controller", "-Dplay.http.secret.key=lkdnfosdnfoisdnfisdjfsodijfsoidfhsdfsdf4sd98f4sd89f4sd98f4"]
