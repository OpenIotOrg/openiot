#!/bin/bash

for jarFile in $( ls target/**/*jar ); do
    cp=$cp:./$jarFile
done

java -classpath target/classes:$cp -splash:lib/logo.png -Dlogback.configurationFile=conf/logback.xml -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.Main 22232 &
