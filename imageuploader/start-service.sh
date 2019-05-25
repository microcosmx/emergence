#!/bin/sh

set -e

export JAVA_HOME=$(dirname $(dirname $(update-alternatives --display java | grep points | grep -oP "/usr/.*")))

export JAVA_OPTS="$JAVA_OPTS -server -Xmx4g -Xms4g -Xmn256m -XX:MaxMetaspaceSize=3g -XX:CompressedClassSpaceSize=1g -XX:+UseG1GC"

rm -f /opt/src/RUNNING_PID

java -Dplay.http.secret.key=price -Dhttp.port=9000 -cp "/opt/src/target/scala-2.11/*" play.core.server.ProdServerStart


