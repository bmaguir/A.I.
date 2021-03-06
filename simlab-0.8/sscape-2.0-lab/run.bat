@ECHO OFF

SET RUNCLASS=uchicago.src.sim.sugarScape.SugarModel
SET REPAST_HOME=..\..\repast3.1\RepastJ

rem Saving the classpath so we can restore it at the end.
SET OLDCLASSPATH=%CLASSPATH%

set STARTING_POINT=%CD%
cd ..
cd %STARTING_POINT%

SET CLASSPATH=%OLDCLASSPATH%;%REPAST_HOME%\repast.jar;%REPAST_HOME%\lib/trove.jar;%REPAST_HOME%\lib/colt.jar;%REPAST_HOME%\lib/jgl3.1.0.jar;%REPAST_HOME%\lib/excelaccessor_Runtime.jar;%REPAST_HOME%\lib/jmf.jar;%REPAST_HOME%\lib/jcchart.jar;%REPAST_HOME%\lib/junit.jar;%REPAST_HOME%\lib/plot.jar;%REPAST_HOME%\lib/xerces.jar

java -Xmx200M %RUNCLASS% >nul 2>nul 

rem Restoring the classpath.
SET CLASSPATH=%OLDCLASSPATH%
