FROM nginx:1.21-alpine
MAINTAINER Simon Forschner

COPY ./nginx.conf /etc/nginx/conf.d/default.conf
RUN rm -rf /usr/share/nginx/html/*
ADD build/artifact-js /usr/share/nginx/html
