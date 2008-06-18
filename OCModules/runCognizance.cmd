echo off

REM OCCognizance runs until there's no more work then exits.

REM Cognizance config file.
set COGNZANCE_CONFIG=.\config\occognizance.properties

set CLASS_PATH=%CLASS_PATH%;.\dist\OCModules.jar

java -cp %CLASS_PATH% net.filterlogic.OpenCapture.module.OCCognizance %COGNZANCE_CONFIG%
