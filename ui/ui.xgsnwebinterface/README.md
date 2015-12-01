# README #

Prem Jayaraman
Aaron Prince
Jay Kelly 
Luke Herron 
Neel Sahdeo 
Zachary Williamson 

For the log streaming functionality with PrimeFaces Push/Atmosphere to work
with JBoss AS 7.1.1.Final the Apache Portable Runtime (APR) needs to be
enabled by configuring the standalone.xml and changing native to true, look
for the following line around line 258:

<subsystem xmlns="urn:jboss:domain:web:1.1" default-virtual-server="default-host" native="true">

N.B. Enabling the APR is known to break some part of the other OpenIoT ui
     pages
