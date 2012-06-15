OC_HOME=C:\Code\Java\OCModules
CLASS_PATH=%CLASS_PATH%;%OC_HOME%\dist\OCUtils.jar;%OC_HOME%\dist\lib\OpenCapture.jar


REM $@

java -classpath %CLASS_PATH% -Doc_home=%OC_HOME%  net.filterlogic.OpenCapture.utils.OCBarcodeReader %1 %2 %3 %4 %5 %6 %7
