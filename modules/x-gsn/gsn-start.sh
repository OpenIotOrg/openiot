#!/bin/bash

for jarFile in $( ls target/*jar ); do
     cp=$cp:./$jarFile
done
$JAVA_HOME/bin/java -classpath $cp -splash:lib/logo.png -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false gsn.Main 22232 &

        
