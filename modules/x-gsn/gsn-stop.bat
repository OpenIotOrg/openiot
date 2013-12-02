REM @echo off
REM setlocal ENABLEDELAYEDEXPANSION

REM SET CLASSPATH=$JAVA_HOME\lib\rt.jar
REM FOR /R ./target %%c in (*.jar) DO SET CLASSPATH=!CLASSPATH!;%%c

java -classpath "./target/*;./target/dependencies/*"  -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.GSNStop 22232
