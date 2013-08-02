@echo off
setlocal ENABLEDELAYEDEXPANSION

SET CLASSPATH=$JAVA_HOME\lib\rt.jar
FOR /R ./lib %%c in (*.jar) DO SET CLASSPATH=!CLASSPATH!;%%c
"$JAVA_HOME\bin\java" -classpath "%CLASSPATH%"  -splash:lib/logo.png -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.Main 22232
