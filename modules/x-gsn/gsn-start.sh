#!/bin/bash

#for jarFile in $( find -name "*.jar" ); do
#     cp=$cp:./$jarFile
#done
for jarFile in $( ls target/**/*jar ); do
     cp=$cp:./$jarFile
done
#echo $cp
#java -classpath target/dependencies/resteasy-jaxrs-2.3.5.Final.jar:target/classes:$cp -splash:lib/logo.png -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.Main 22232 &
java -classpath target/classes:$cp -splash:lib/logo.png -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.Main 22232 &
