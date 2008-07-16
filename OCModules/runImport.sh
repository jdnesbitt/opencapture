
# OCImport runs until there's no more work then exits.

# Import config file.
IMPORT_CONFIG=./config/ocimport.properties

CLASS_PATH=$CLASS_PATH:./dist/OCModules.jar

java -cp $CLASS_PATH net.filterlogic.OpenCapture.module.OCImport $IMPORT_CONFIG

