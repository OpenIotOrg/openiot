#!/bin/bash

for jarFile in $( ls target/*jar ); do
     cp=$cp:./$jarFile
done
#$JAVA_HOME/bin/java -classpath $cp -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false gsn.GSNStop 22232         
java -classpath $cp -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false gsn.GSNStop 22232         
