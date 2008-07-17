#!/bin/sh

CLASS_PATH=$CLASS_PATH:./dist/OCUtils.jar:./dist/lib/OpenCapture.jar:../OCpenCapture/dist/lib

#java -cp $CLASS_PATH -Doc_home=/home/dnesbitt/NetBeansProjects/OCModules net.filterlogic.OpenCapture.utils.OCBarcodeReader $@

java -classpath $CLASS_PATH -Doc_home=/home/dnesbitt/NetBeansProjects/OCModules  net.filterlogic.OpenCapture.utils.OCBarcodeReader $1 $2 $3 $4 $5 $6 $7