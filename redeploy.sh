#/bin/bash
# This script builds the projects and updates the docker contains.
#
# Notes:
# - Has to be executed as root user
# - Currently no multi-user support
# - Requires Docker-Compose to be installed

if [ "$EUID" != 0 ]; then
  echo "Please run as root"
  exit
fi

docker-compose down &
echo Execute gradle using user \'$(users)\'...
su $(users) -c "./gradlew build" && docker-compose up --build -d
