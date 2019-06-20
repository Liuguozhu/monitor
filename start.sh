#!/bin/sh
#nohup java -server -Xmx64m -jar monitor.jar &
echo "nohup java -jar -Djava.security.egd=file:/dev/./urandom monitor.jar &"
. /etc/profile
nohup java -jar -Djava.security.egd=file:/dev/./urandom monitor.jar --spring.profiles.active=prod > log.file 2>&1 &
