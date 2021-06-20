FROM amazoncorretto:16-alpine
MAINTAINER Simon Forschner

# Configure environment variables
ENV TZ=Europe/Berlin
ENV DIR=/server-backend

RUN mkdir $DIR
# ADD command will automatically extract the tar file
ADD build/distributions/*.tar $DIR
WORKDIR $DIR
STOPSIGNAL SIGINT
RUN rm */bin/*.bat
RUN ln -s $DIR/*/bin/* /usr/local/bin/start

# Execute start script
CMD ["start"]
