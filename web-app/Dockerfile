FROM nginx:1.19.9-alpine
MAINTAINER Simon Forschner

COPY ./nginx.conf /etc/nginx/conf.d/default.conf
RUN rm -rf /usr/share/nginx/html/*
ADD build/artifact-js /usr/share/nginx/html/artifact-js
ADD build/index.html /usr/share/nginx/html