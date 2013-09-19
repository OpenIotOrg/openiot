@echo off
setlocal ENABLEDELAYEDEXPANSION

SET CLASSPATH=C:\Program Files\Java\jre6\lib\rt.jar
FOR /R ./target %%c in (*.jar) DO SET CLASSPATH=!CLASSPATH!;%%c
java -classpath "%CLASSPATH%" org.openiot.gsn.metadata.LSM.utils %1