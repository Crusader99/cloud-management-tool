#/bin/bash
# This script builds the projects and updates the docker contains.
# Note: Has to be executed as root user.
docker-compose down &
echo Execute gradle using user \'$(users)\'...
su $(users) -c "gradle build"
docker-compose up --build -d
