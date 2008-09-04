#!/bin/sh

OC_HOME=`pwd`/

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

CLASS_PATH=$CLASS_PATH:./:./dist/OCUtils.jar:$CP

java -classpath $CLASS_PATH -Doc_home=$OC_HOME  net.filterlogic.OpenCapture.utils.OCTIFFormatter $1 $2

