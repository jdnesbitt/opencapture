echo off

REM OCImport runs until there's no more work then exits.

REM Import config file.
set IMPORT_CONFIG=.\config\ocimport.properties

set CLASS_PATH=%CLASS_PATH%;.\dist\OCModules.jar

java -cp %CLASS_PATH% net.filterlogic.OpenCapture.module.OCImport %IMPORT_CONFIG%