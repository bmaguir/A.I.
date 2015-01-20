#! /bin/sh

# set this var to point to the location of repast on your machine 
#REPAST_HOME=/Applications/Repast-3.1/RepastJ
REPAST_HOME=../../repast3.1/RepastJ

REPAST_CP=$CLASSPATH:.:$REPAST_HOME/repast.jar:$REPAST_HOME/lib/trove.jar:$REPAST_HOME/lib/colt.jar:$REPAST_HOME/lib/jgl3.1.0.jar:$REPAST_HOME/lib/excelaccessor_Runtime.jar:$REPAST_HOME/lib/jmf.jar:$REPAST_HOME/lib/jcchart.jar:$REPAST_HOME/lib/junit.jar:$REPAST_HOME/lib/plot.jar:$REPAST_HOME/lib/xerces.jar
# JAVAC=jikes
JAVAC=javac

echo $JAVAC   -classpath $REPAST_CP uchicago/src/repastdemos/mousetrap/MouseTrapModel.java
exec $JAVAC   -classpath $REPAST_CP uchicago/src/repastdemos/mousetrap/MouseTrapModel.java

