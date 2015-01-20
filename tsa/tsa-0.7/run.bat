@ECHO OFF

SET RUNCLASS=sim.tsa.TSAModel
SET REPAST_HOME=.

rem Saving the classpath so we can restore it at the end.
SET OLDCLASSPATH=%CLASSPATH%

set STARTING_POINT=%CD%
cd ..
cd %STARTING_POINT%

SET CLASSPATH=%OLDCLASSPATH%;%REPAST_HOME%\repast.jar;%REPAST_HOME%\trove.jar;%REPAST_HOME%\colt.jar;%REPAST_HOME%\jgl3.1.0.jar;%REPAST_HOME%\excelaccessor_Runtime.jar;%REPAST_HOME%\jmf.jar;%REPAST_HOME%\jcchart.jar;%REPAST_HOME%\junit.jar;%REPAST_HOME%\plot.jar;%REPAST_HOME%\xerces.jar

java -Xmx200M %RUNCLASS%

rem Restoring the classpath.
SET CLASSPATH=%OLDCLASSPATH%
