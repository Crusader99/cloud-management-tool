FROM electronuserland/builder:14
MAINTAINER Simon Forschner
RUN mkdir -p /project
WORKDIR /project
CMD sh -c 'yarn add electron-builder --dev && yarn add electron --dev && yarn global add electron-builder && electron-builder -l'
