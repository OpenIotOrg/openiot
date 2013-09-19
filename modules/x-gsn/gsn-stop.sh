#!/bin/bash

for jarFile in $( find -name "*.jar" ); do
     cp=$cp:./$jarFile
done

java -classpath $cp -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.GSNStop 22232
