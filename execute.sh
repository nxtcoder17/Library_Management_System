#! /bin/bash

CUSTOM_CLASSPATH=./requirements/mariadb-java-client-2.2.5.jar:./requirements/controlsfx-9.0.0.jar:./bin
SOURCE_PATH=src

MAIN_TARGET=src/library/Main.java
MAIN_CLASS=library.Main

echo "[*] Compiling ... $MAIN_TARGET"
javac -d bin -sourcepath $SOURCE_PATH -classpath $CUSTOM_CLASSPATH "$MAIN_TARGET" src/library/ui/Controller.java

echo "[*] Executing ..."
java -classpath $CUSTOM_CLASSPATH:./src $MAIN_CLASS

exit 0
