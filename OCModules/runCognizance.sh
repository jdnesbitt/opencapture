#!/bin/bash

# OCCognizance runs until there's no more work then exits.

# Cognizance config file.
COGNZANCE_CONFIG=./config/occognizance.properties

FILE_LIST=`find ./dist/lib -true`
CNTR="0"

for file in `echo $FILE_LIST`; do
	if [ "$CNTR" = "0" ]; then
		CNTR="1"
		CP=$file
	else
		CP=$CP:$file
	fi
done

CLASS_PATH=./dist/OpenCapture.jar:./dist/OCModules.jar:$CP

java -cp $CLASS_PATH -verbose:gc net.filterlogic.OpenCapture.module.OCCognizance $COGNZANCE_CONFIG

