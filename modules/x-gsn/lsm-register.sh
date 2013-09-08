#!/bin/bash

for jarFile in $( ls target/*jar ); do
     cp=$cp:./$jarFile
done
$JAVA_HOME/bin/java -classpath $cp org.openiot.gsn.metadata.LSM.utils "$@" &
