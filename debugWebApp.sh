#!/bin/bash
# This script starts the web-app in hot-reload mode that directly updates the code when any changes occurred.
#
# Note: This file is only a shortcut for the following long command:

if [ "$EUID" = 0 ]; then
  echo "Please don't run as root"
  exit
fi

./gradlew :web-app:browserDevelopmentRun --continuous --no-build-cache
