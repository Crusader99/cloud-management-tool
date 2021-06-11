FROM electronuserland/builder:14
MAINTAINER Simon Forschner
# docker run -it -v .:/project:z -w /project electronuserland/builder
COPY ./nginx.conf /etc/nginx/conf.d/default.conf

RUN mkdir -p /project
WORKDIR /project
RUN yarn add electron-builder --dev
RUN yarn add electron --dev
RUN yarn global add electron-builder
RUN electron-builder
