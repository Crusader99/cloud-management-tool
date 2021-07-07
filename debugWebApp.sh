#!/bin/bash
# This script starts the web-app in hot-reload mode that directly updates the code when any changes occurred.
#
# Note: This file is only a shortcut for the following long command:

./gradlew :web-app:browserDevelopmentRun --continuous --no-build-cache
