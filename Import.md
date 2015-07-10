# Introduction #

The Import module is used to perform file system imports.  A configuration file is used to identify the directories the import module should poll for new documents.


# Details #

OCImport currently only supports TIFF files.  Future releases will support other formats such as PDF.  Trigger files are used only as a triggering mechanism to identify files ready for import.  The Import module uses a configuration file.  The startup script points to a configuration file named ocimport.properties.  The ocimport.properties file should contain the following properties:

| oc\_home | OC Home property should point to the root OpenCapture directory. |
|:---------|:-----------------------------------------------------------------|
| log4j    | Path to the log4j property file.                                 |
| polldircount | Number of directories to poll.  Each direcotry will have an associated number. |
| polldir.n | Directory to poll. N represent the directory number to poll.     |
| archivepath.n | Path to the directory where imported files should be stored once imported. |
| batchclass.n | Batch Class the documents should be assigned to.                 |
| importfilesonly.n | If importing files only meaning there are no trigger files to signify a file can be imported. |
| importtrigger.n | Use this if importfilesonly=no.  This will be the extension of the trigger file( ex: xml) |


### Example Configuration File ###

```
# OpenCapture home
oc_home=/home/dnesbitt/NetBeansProjects/OCModules

# Log 4 j config file
log4j=/home/dnesbitt/NetBeansProjects/OCModules/config/log4j-ocimport.properties

# Number of poll directories configured.
polldircount=1

# Images to import into OpenCapture are here
polldir.1=/home/dnesbitt/Test/OpenCapture/TEST_IMAGES

# Path where imported images will be archived.
archivepath.1=/home/dnesbitt/Test/OpenCapture/TEST_IMAGES/Archive

# Batch Class the images should be assigned to
batchclass.1=Carrier Files

# If importing files only meaning there are no trigger files to
# signify a file can be imported.
importfilesonly.1=yes

# Use this if importfilesonly=no.  This will be the extension
# of the trigger file( ex: xml)
importtrigger.1=
```


### How it Works ###

Import takes the name of the TIFF file and uses that as the batch name in OC.  The multi-page TIFF is split into single page TIFFs during the import process.  Once the split is complete the batch is closed and import moves to the next file in the import folder.

We use packbits compression as the java JPEG compression has a memory leak.  **If anyone has a work-around for this please let us know.**

NOTE: **The current version of import does not support using mutiple single page TIFFs to create a batch.**