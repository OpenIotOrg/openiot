@echo on
setlocal ENABLEDELAYEDEXPANSION

REM SET CLASSPATH=C:\Program Files\Java\jdk1.7.0_25\jre\lib
REM FOR /R ./target %%c in (*.jar) DO (
REM SET CLASSPATH=!CLASSPATH!;%%c
REM )

REM set CLASSPATH="
REM for /R ./target %%a in (*.jar) do (
REM    set CLASSPATH=!CLASSPATH!;%%a
REM )
REM set CLASSPATH=!CLASSPATH!"
REM echo !CLASSPATH!
	
java -classpath "./target/*;./target/dependencies/*"  -splash:lib/logo.png -Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.Log4JLogger -Dorg.mortbay.log.LogFactory.noDiscovery=false org.openiot.gsn.Main 22232 