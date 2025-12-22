#!/bin/bash

cd "$(dirname "$0")/ai-meeting-boot"
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

echo "启动应用..."
java -jar target/ai-meeting-boot.jar 2>&1 | tee /tmp/app-startup.log

