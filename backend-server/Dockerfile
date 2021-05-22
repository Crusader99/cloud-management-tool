FROM amazoncorretto:16-alpine
MAINTAINER Simon Forschner

ENV TZ=Europe/Berlin
ENV DIR=/server-backend

RUN mkdir $DIR
# ADD command will automatically extract the tar file
ADD build/distributions/*.tar $DIR
WORKDIR $DIR
STOPSIGNAL SIGINT
EXPOSE 8080
RUN rm */bin/*.bat
RUN ln -s $DIR/*/bin/* /usr/local/bin/start
CMD ["start"]
