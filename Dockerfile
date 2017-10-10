FROM maven:alpine
LABEL maintainer="halde@matthias-kuech.de"
COPY . /harmony/

WORKDIR /harmony
RUN mvn install

EXPOSE 8081

CMD java -jar /harmony/target/restful-harmony-1.0.0.jar $HARMONY_IP

