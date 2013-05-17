mTorClient
==========

Pluggable jar for the mTor monitoring system
Add this jar as a dependency in the Java projects you want to monitor
This will monitor project variables as well as the server environment.

By default a heartbeat message is sent every 5 minutes.

Also by default the disk space and free memory is monitored.
If thresholds are passed, status messages are sent to a central mTor server.


Using mTorClient
================

1. Add the jar file to your maven repository with mvn install

2. Add the following dependency to your projects pom.xml
  <dependency>
    <groupId>nl.bhit.mtor.client</groupId>
    <artifactId>mTorClient</artifactId>
    <version>x.x</version>
  </dependency>

3. Add the StartupListener for the client to the web.xml:
   <listener>
     <listener-class>nl.bhit.mtor.client.StartupListener</listener-class>
   </listener>

4. Add a file mTor.properties to your projects root wherein you add exceptions to mTor.default.properties:
mTor.server.username=admin
mTor.server.password=admin
mTor.project.id=-1

5. Add your own providers to monitor application parameters or more server parameters
