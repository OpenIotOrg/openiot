@echo off
setlocal ENABLEDELAYEDEXPANSION

SET CLASSPATH=C:\Program Files\Java\jre6\lib\rt.jar
FOR /R ./target %%c in (*.jar) DO SET CLASSPATH=!CLASSPATH!;%%c
java -classpath "%CLASSPATH%"  -splash:lib/logo.png -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.Main 22232 