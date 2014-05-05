#!/bin/bash

for jarFile in $( ls target/**/*jar ); do
     cp=$cp:./$jarFile
done

java -classpath target/classes:$cp:conf org.openiot.gsn.metadata.LSM.utils "$@" &
