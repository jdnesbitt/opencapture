#!/bin/sh

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

java -classpath $CLASS_PATH -Doc_home=/home/dnesbitt/NetBeansProjects/OCModules  net.filterlogic.OpenCapture.utils.OCBarcodeReader $1 $2 $3 $4 $5 $6 $7

