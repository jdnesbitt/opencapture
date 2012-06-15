/*
Copyright 2008 Filter Logic

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package net.filterlogic.OpenCapture.module;

import au.com.bytecode.opencsv.CSVReader;
import net.filterlogic.OpenCapture.*;
import net.filterlogic.io.Path;
import net.filterlogic.util.imaging.*;

import org.apache.log4j.*;

import java.util.Properties;
import java.util.List;

import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import net.filterlogic.OpenCapture.managers.ImportSettings;
import net.filterlogic.OpenCapture.managers.Mapping;
import net.filterlogic.OpenCapture.managers.MappingFile;
import net.filterlogic.OpenCapture.managers.ValueMap;
import net.filterlogic.io.FileAccess;
import net.filterlogic.util.xml.XMLParser;

import net.filterlogic.OpenCapture.interfaces.IOCImportPlugin;
import net.filterlogic.util.xml.XSLTransform;

class FileNameFilter implements FilenameFilter 
{
    String ext;

    public FileNameFilter(String ext) 
    {
        this.ext = ext.toUpperCase();
    }

    public boolean accept(File dir, String name) 
    {
        return (name.toUpperCase().endsWith(this.ext));
    }
}

/**
 *
 * @author Darron Nesbitt
 */
public class OCImport 
{
    static Logger myLogger = Logger.getLogger(OCImport.class.getName( ));

    private String configFile = "";
    private String log4j = "";
    private Properties prop = new Properties();
    private String OC_HOME = "";

    private int totalPages = 0;

    public OCImport(String configFile) throws Exception
    {
        this.configFile = configFile;

        // load property config file.
        FileInputStream fis = new FileInputStream(new File(configFile));
        prop.load(fis);
        fis.close();

        OC_HOME = prop.getProperty("oc_home");
        log4j = prop.getProperty("log4j");

        PropertyConfigurator.configure(log4j);
        
        myLogger.info("OCImport started..");
    }

    /**
     * Retrieves file list.  Directories are not returned.
     * @param path Path to directory.
     * @return File array.
     */
    private File[] getFileList(String path)
    {
        if(!new File(path).exists())
            return null;

        // This filter only returns files
        FileFilter dirFilter = new FileFilter() 
        {
            public boolean accept(File file) 
            {
                return !file.isDirectory();
            }
        };

        File[] files = new File(path).listFiles(dirFilter);

        return files;
    }

    /**
     *
     * @param path
     * @param ext
     * @return
     */
    private File[] getFileList(String path,String ext)
    {
        if(!new File(path).exists())
            return null;

        File[] files = new File(path).listFiles(new FileNameFilter(ext));

        return files;
    }

    /**
     * Retrieve list of directories.
     * @param path Path to starting directory
     * @return File[] containing directories.  Returns null if no directories found.
     */
    private File[] getDirList(String path)
    {
        if(!new File(path).exists())
            return null;

        // This filter only returns files
        FileFilter dirFilter = new FileFilter()
        {
            public boolean accept(File file)
            {
                return file.isDirectory();
            }
        };

        File[] files = new File(path).listFiles(dirFilter);

        return files;
    }

    /**
     * Import batches into OpenCapture.
     */
    public void importBatches()
    {
        Batch batch;
        XMLParser xmlParser = null;

        try
        {
            int pollDirs = Integer.parseInt(prop.getProperty("polldircount"));

            for(int i=1;i<=pollDirs;i++)
            {
                String path = prop.getProperty("polldir." + String.valueOf(i));
                String batchclass = prop.getProperty("batchclass." + String.valueOf(i));
                boolean filesonly = ((String)prop.get("importfilesonly." + String.valueOf(i))).toLowerCase().equals("yes") ? true : false;
                boolean processsubfolder = ((String)prop.get("processsubfolder." + String.valueOf(i))).toLowerCase().equals("yes") ? true : false;
                boolean documentperfile = ((String)prop.get("documentperfile." + String.valueOf(i))).toLowerCase().equals("yes") ? true : false;
                boolean batchperfile = ((String)prop.get("batchperfile." + String.valueOf(i))).toLowerCase().equals("yes") ? true : false;
                String ext = prop.getProperty("importtrigger." + String.valueOf(i));
                String mappingfile = prop.getProperty("importmappingfile." + String.valueOf(i));
                boolean useMappingFile = false;
                String batchname = "";
                String imagePath = "";
                String archivePath = prop.getProperty("archivepath." + String.valueOf(i));
                String batchnameprefix = prop.getProperty("batchnameprefix." + String.valueOf(i)) != null ? prop.getProperty("batchnameprefix." + String.valueOf(i)) : "";
                String batchnamesuffix = prop.getProperty("batchnamesuffix." + String.valueOf(i)) != null ? prop.getProperty("batchnamesuffix." + String.valueOf(i)) : "";
                String importPlugin = prop.getProperty("importplugin." + String.valueOf(i)) != null ? prop.getProperty("importplugin." + String.valueOf(i)) : "";

                myLogger.info("pollDir=" + path);
                myLogger.info("archiveDir=" + archivePath);
                myLogger.info("importTrigger=" + ext);
                myLogger.info("batchclass=" + batchclass);
                myLogger.info("processsubfolder=" + processsubfolder);
                myLogger.info("importfilesonly=" + filesonly);
                myLogger.info("importmappingfile=" + mappingfile);
                myLogger.info("documentperfile=" + documentperfile);

                // create settings obj
                ImportSettings impset = new ImportSettings();

                impset.setArchivePath(archivePath);
                impset.setBatchClass(batchclass);
                impset.setBatchPerFile(batchperfile);
                impset.setDocumentPerFile(documentperfile);
                impset.setFilesOnly(filesonly);
                impset.setImagePath(imagePath);
                impset.setMappingFile(mappingfile);
                impset.setPollDir(path);
                impset.setProcessSubFolder(processsubfolder);
                impset.setTriggerExt(ext);
                impset.setBatchNamePrefix(batchnameprefix);
                impset.setBatchNameSuffix(batchnamesuffix);

                MappingFile mf = null;

                // if mapping file set, create mappingfile object
                if(mappingfile.length()>0)
                {
                    mf = new MappingFile(mappingfile);

                    impset.setUseMappingFile(true);
                    impset.setMappingFileObj(mf);
                }
                else
                {
                    impset.setUseMappingFile(false);
                    impset.setMappingFileObj(null);
                }


                /**
                 * ***** ADD CODE HERE TO LOAD AND RUN IMPORT PLUGIN *****
                 */

                if(importPlugin.length()<1)
                {
                    // if process sub dirs is true, get dir list
                    File[] directories = null;

                    if(processsubfolder)
                    {
                        directories = getDirList(path);

                        // if no sub directories, process current
                        if(directories == null || directories.length<1)
                        {
                            directories = new File[1];

                            // add file object (path) to array
                            directories[0] = new File(path);
                        }
                    }
                    else
                    {
                        directories = new File[1];
                        // add file object (path) to array
                        directories[0] = new File(path);
                    }

                    myLogger.info("DirectoryCount=" + directories.length);

                    // loop through directories
                    for(int d=0;d<directories.length;d++)
                    {
                        String dirPath = directories[d].getAbsolutePath();

                        myLogger.info("Processing dir=" + dirPath);

                        File[] files;

                        if(filesonly)
                             files = getFileList(dirPath);
                        else
                            files =  getFileList(dirPath,ext);

                        if(files == null || files.length<1)
                        {
                            myLogger.info("No files to process in " + dirPath);

                            continue;
                        }

                        // if there's something to process, do it
                        if(files.length>0)
                        {
                            if(batchperfile)
                                ProcessBatchPerFile(files, impset, mf);
                            else
                                ProcessMultiFilePerBatch(files, impset, mf);
                        }

                    } // dir for loop

                }   // import plugin check
                else
                {
                    // use import plugin to process
                }

            }   // polldir loop
        }
        catch(Exception e)
        {
            myLogger.error(net.filterlogic.util.StackTraceUtil.getStackTrace(e));
        }
        catch(OpenCaptureException oce)
        {
            myLogger.error(net.filterlogic.util.StackTraceUtil.getStackTrace(oce));
        }
    }

    private void ProcessBatchPerFile(File[] files, ImportSettings impSet, MappingFile mf) throws Exception
    {
        Batch batch = null;
        String batchname = "";
        String imagePath = impSet.getImagePath();
        String archivePath = impSet.getArchivePath();
        String batchclass = impSet.getBatchClass();
        String triggerExt = impSet.getTriggerExt().toUpperCase();
        XMLParser xmlParser = null;

        // loop through files
        for(int b=0;b<files.length;b++)
        {
            try
            {
                batch = new Batch();

                // get current trigger file
                File file = files[b];

                //batchname = Path.getFileNameWithoutExtension(files[b].getName().toUpperCase());
                batchname = batch.createBatchName(impSet.getBatchNamePrefix(), impSet.getBatchNameSuffix());

                myLogger.debug("batchName=" + batchname);
                myLogger.debug("imagePath=" + imagePath);
                myLogger.debug("archivePath=" + archivePath);

                try
                {

                    // create new batch
                    batch.CreateBatch(batchclass, batchname);
                    myLogger.debug("Created batch");
                    // get batch id
                    long batchID = Long.parseLong(batch.getID());
                    myLogger.debug("Got batch id");
                    // open batch
                    batch.OpenBatch(batchID);

                    myLogger.debug("Opened batch");

                   // check if trigger used
                    if(impSet.getTriggerExt().length()>0)
                    {

                        if(triggerExt.toUpperCase().equals("XML"))
                        {

                            if(mf.getTrigger().toUpperCase().equals("XMLS"))
                            {
                                ProcessXMLS(file, batch, impSet);

                                // move trigger file to archive path
                                FileAccess.Move(file.getAbsolutePath(), impSet.getArchivePath(), true);


                            }

                            if(mf.getTrigger().toUpperCase().equals("XMLM"))
                            {
                                ProcessXMLM(file, batch, impSet);

                                // move trigger file to archive path
                                FileAccess.Move(file.getAbsolutePath(), impSet.getArchivePath(), true);
                            }
                        }
                    
                    }
                }
                catch(OpenCaptureException e)
                {
                    myLogger.error("Error creating batch!\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(e));
                }
                catch(OpenCaptureImagingException oie)
                {
                    myLogger.error("Error creating batch!\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(oie));
                }

            }
            catch(OpenCaptureException oe)
            {
                myLogger.error("Error creating batch!\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(oe));
            }

            try
            {
                if(batch != null)
                    batch.CloseBatch();
            }
            catch(OpenCaptureException oce)
            {
                myLogger.error("Error closing batch." + net.filterlogic.util.StackTraceUtil.getStackTrace(oce));
            }
        } // file for loop
    }

    /**
     * Create a new batch.
     *
     * @param impSet ImportSettings object
     * @return Long representing batch id.  User batch.open(batchID) to open batch.
     *
     * @throws OpenCaptureException
     */
    private long createBatch(ImportSettings impSet) throws OpenCaptureException
    {
        // create new batch
        Batch batch = new Batch();

        // create batch name
        String batchname = batch.createBatchName(impSet.getBatchNamePrefix(), impSet.getBatchNameSuffix());

        myLogger.debug("batchName=" + batchname);
        myLogger.debug("imagePath=" + impSet.getImagePath());
        myLogger.debug("archivePath=" + impSet.getArchivePath());

        // create new batch
        batch.CreateBatch(impSet.getBatchClass(), batchname);
        myLogger.debug("Created batch: " + batchname);
        // get batch id
        long batchID = Long.parseLong(batch.getID());
        myLogger.debug("Got batch id");

        return batchID;
    }

    private void ProcessXMLS(File triggerFile,Batch batch, ImportSettings impSet) throws Exception,OpenCaptureImagingException,OpenCaptureException
    {
        List mappings = null;
        MappingFile mf = impSet.getMappingFileObj();
        String imagePath = "";
        File[] imageFiles = null;
        XMLParser xmlParser = null;

        try
        {
            xmlParser = new XMLParser();

            if(mf != null)
            {
                mappings = mf.getMappings();
            }
            else
            {
                throw new Exception("Must have a mapping file for XML trigger files.");
            }

            if(triggerFile == null)
                throw new Exception("Invalid trigger file.");

            // load xml trigger file
            xmlParser.loadDocument(triggerFile.getAbsolutePath());

            // get image path
            imagePath = triggerFile.getParent();

            Mapping fileNameMap = mf.findMappingByDestination("FILENAME");
            Mapping formIDMap = mf.findMappingByDestination("FORMID");
            String fileNameXPath = fileNameMap.getSource();
            String formIDXPath = formIDMap.getSource();

            String impFileName = "";

            if(fileNameXPath.length()>0)
                impFileName = xmlParser.getValue(fileNameXPath, "");

            if(impFileName.length()>0)
                imageFiles = getFileList(imagePath, impFileName);
            else
                imageFiles = getFileList(imagePath, "tif");

            for(int i=0;i<imageFiles.length;i++)
            {
//                // if batchperfile se and not first file
//                if(impSet.batchPerFile && i >0)
//                {
//                    // close current batch
//                    batch.CloseBatch();
//                    // create new batch and get it's id
//                    long batchId = createBatch(impSet);
//                    // create new batch object
//                    batch = new Batch();
//                    // opoen new batch
//                    batch.OpenBatch(batchId);
//                }

                String[] tiffs = new String[1];

                tiffs[0] = imageFiles[i].getAbsolutePath();

                //List list = ToTIFF.toTIFF(tiffs, OpenCaptureCommon.getBatchFilePath(batchID), "00000000", false, 200);
                List list = ToTIFF.toTIFF(tiffs, batch.getImageFilePath(),impSet.getArchivePath(), "00000000", false, 200);

                // get value @ form id xpath
                String formIDValue = xmlParser.getValue(formIDXPath, "");

                ValueMap formIDVM = formIDMap.findValueMap(formIDValue);

                // add check here for db type when connecting to db
                String formID = "DOCUMENT";
                String vmapValue = "Document";
                String docName = "Document";
                
                // get index fields from batch class
                IndexFields indexFields = batch.getConfigurations().getIndexFields();

                // check doctype default value
                IndexField ndxDocType = indexFields.getIndexField("DOC_TYPE");

                                // if doc type index field found
                                if(ndxDocType.getName().length()>0 && ndxDocType.getValue().length()>0)
                                {
                                    myLogger.debug("Default DOC_TYPE set in batch class: " + ndxDocType.getName());
                                    myLogger.debug(ndxDocType.toString());
                                    
                                    // set form id, vmap value, and doc name
                                    if(ndxDocType.getKeyValue().length()>0)
                                        formID = ndxDocType.getKeyValue();
                                    else
                                        formID = ndxDocType.getValue();

                                    vmapValue = ndxDocType.getValue();
                                    
                                    myLogger.debug("FormID: " + formID);
                                    myLogger.debug("DocType: " + vmapValue);

                                }
                                
                                if(formIDVM != null)
                                {
                                    myLogger.debug("Override DOC_TYPE from mapping template");
                                    
                                    formID = formIDVM.getId();
                                    vmapValue = formIDVM.getValue();
                                }
                                
                                myLogger.debug("FormID: " + formID);
                                myLogger.debug("DocType: " + vmapValue);
                                
                // get document config from batch class configuration section
                Configuration documentConfig = batch.getConfigurations().getConfiguration(vmapValue);

                if(documentConfig != null)
                {
                    docName = documentConfig.getName();
                }

                Document document = new Document();

                document.setFormID(formID);
                document.setName(docName);
                //document.setNumber(documentNumber);
                //++documentNumber;

                if(document!=null)
                {
                    // add pages to document
                    for(int p=0;p<list.size();p++)
                    {
                        ++totalPages;

                        String pageName = (String)list.get(p);
                        pageName = Path.getFileName(pageName);

                        Page page = new Page(pageName, totalPages, totalPages);

                        document.addPage(page);
                    }

                    for(int e=0;e<mappings.size();e++)
                    {
                        Mapping map = (Mapping)mappings.get(e);

                        if(map.getDestination().equals("FILENAME") || map.getDestination().equals("FORMID"))
                            continue;

                        // get source xpath
                        String xpath = map.getSource();
                        // get source value
                        String entryValue = xmlParser.getValue(xpath,"");

                        String vmValue = "";
                        String vmKeyValue = "";

                        // if value map exists, check it for mapping
                        if(map.getValueMapList().size()>0)
                        {
                            ValueMap vm = map.findValueMap(entryValue);

                            if(vm != null)
                            {
                                vmValue = vm.getValue();
                                vmKeyValue = vm.getKeyValue();
                            }
    //                                        else
    //                                            vmValue = entryValue;

                        }
                        else
                            vmValue = entryValue;

                        // set index field value
                        indexFields.getIndexField(map.getDestination()).setValue(vmValue);
                        indexFields.getIndexField(map.getDestination()).setKeyValue(vmKeyValue);
                    }

                    // set index fields in document
                    document.setIndexFields(indexFields);

                    // add the document to batch
                    batch.addDocument(document);
                }
            }
        }
        catch(Exception e)
        {
            myLogger.error("Error in ProcessXMLS\n" + e.toString());

            throw(e);
        }

    }

    private void ProcessXMLM(File triggerFile, Batch batch, ImportSettings impSet) throws OpenCaptureImagingException,OpenCaptureException,Exception
    {
        List mappings = null;
        MappingFile mf = impSet.getMappingFileObj();
        String imagePath = "";
        File[] imageFiles = null;
        XMLParser xmlParser = null;

        try
        {
            xmlParser = new XMLParser();

            if(mf != null)
            {
                mappings = mf.getMappings();
            }
            else
            {
                throw new Exception("Must have a mapping file for XML trigger files.");
            }

            if(triggerFile == null)
                throw new Exception("Invalid trigger file.");

            // load xml trigger file
            xmlParser.loadDocument(triggerFile.getAbsolutePath());

            // if xslt provided, transform xml doc
            if(impSet.getMappingFileObj().getXslt().length()>0)
                xmlParser.setXmlDocument(XSLTransform.transform(xmlParser.getXmlDocument(), impSet.getMappingFileObj().getXslt()));

            // get image path
            imagePath = triggerFile.getParent();

            Mapping fileNameMap = mf.findMappingByDestination("FILENAME");
            Mapping formIDMap = mf.findMappingByDestination("FORMID");
            String fileNameXPath = fileNameMap.getSource();
            String formIDXPath = formIDMap.getSource();

            List impFileNames = null;

            String impFileName = "";

            // get list of names in xml doc
            if(fileNameXPath.length()>0)
                impFileNames = xmlParser.getValues(fileNameXPath);

            if(impFileNames.size()>0)
                imageFiles = getFileList(imagePath, impFileName);
            else
                imageFiles = getFileList(imagePath, "tif");

            for(int i=0;i<imageFiles.length;i++)
            {
//                // if batchperfile se and not first file
//                if(impSet.batchPerFile && i >0)
//                {
//                    // close current batch
//                    batch.CloseBatch();
//                    // create new batch and get it's id
//                    long batchId = createBatch(impSet);
//                    // create new batch object
//                    batch = new Batch();
//                    // opoen new batch
//                    batch.OpenBatch(batchId);
//                }

                String[] tiffs = new String[1];

                tiffs[0] = imageFiles[i].getAbsolutePath();

                //List list = ToTIFF.toTIFF(tiffs, OpenCaptureCommon.getBatchFilePath(batchID), "00000000", false, 200);
                List list = ToTIFF.toTIFF(tiffs, batch.getImageFilePath(),impSet.getArchivePath(), "00000000", false, 200);

                // get value @ form id xpath
                String formIDValue = xmlParser.getValue(formIDXPath, "");

                ValueMap formIDVM = formIDMap.findValueMap(formIDValue);

                // add check here for db type when connecting to db
                String formID = "DOCUMENT";
                String vmapValue = "Document";
                String docName = "Document";

                // get index fields from batch class
                IndexFields indexFields = batch.getConfigurations().getIndexFields();

                // check doctype default value
                IndexField ndxDocType = indexFields.getIndexField("DOC_TYPE");

                                // if doc type index field found
                                if(ndxDocType.getName().length()>0 && ndxDocType.getValue().length()>0)
                                {
                                    myLogger.debug("Default DOC_TYPE set in batch class: " + ndxDocType.getName());
                                    myLogger.debug(ndxDocType.toString());
                                    
                                    // set form id, vmap value, and doc name
                                    if(ndxDocType.getKeyValue().length()>0)
                                        formID = ndxDocType.getKeyValue();
                                    else
                                        formID = ndxDocType.getValue();

                                    vmapValue = ndxDocType.getValue();
                                    
                                    myLogger.debug("FormID: " + formID);
                                    myLogger.debug("DocType: " + vmapValue);

                                }
                                
                                if(formIDVM != null)
                                {
                                    myLogger.debug("Override DOC_TYPE from mapping template");
                                    
                                    formID = formIDVM.getId();
                                    vmapValue = formIDVM.getValue();
                                }
                                
                                myLogger.debug("FormID: " + formID);
                                myLogger.debug("DocType: " + vmapValue);

                // get document config from batch class configuration section
                Configuration documentConfig = batch.getConfigurations().getConfiguration(vmapValue);

                if(documentConfig != null)
                {
                    docName = documentConfig.getName();
                }

                Document document = new Document();

                document.setFormID(formID);
                document.setName(docName);
                //document.setNumber(documentNumber);
                //++documentNumber;

                if(document!=null)
                {
                    // add pages to document
                    for(int p=0;p<list.size();p++)
                    {
                        ++totalPages;

                        String pageName = (String)list.get(p);
                        pageName = Path.getFileName(pageName);

                        Page page = new Page(pageName, totalPages, totalPages);

                        document.addPage(page);
                    }

                    for(int e=0;e<mappings.size();e++)
                    {
                        Mapping map = (Mapping)mappings.get(e);

                        if(map.getDestination().equals("FILENAME") || map.getDestination().equals("FORMID"))
                            continue;

                        // get source xpath
                        String xpath = map.getSource();
                        // get source value
                        String entryValue = xmlParser.getValue(xpath,"");

                        String vmValue = "";
                        String vmKeyValue = "";

                        // if value map exists, check it for mapping
                        if(map.getValueMapList().size()>0)
                        {
                            ValueMap vm = map.findValueMap(entryValue);

                            if(vm != null)
                            {
                                vmValue = vm.getValue();
                                vmKeyValue = vm.getKeyValue();
                            }
    //                                        else
    //                                            vmValue = entryValue;

                        }
                        else
                            vmValue = entryValue;

                        // set index field value
                        indexFields.getIndexField(map.getDestination()).setValue(vmValue);
                        indexFields.getIndexField(map.getDestination()).setKeyValue(vmKeyValue);
                    }

                    // set index fields in document
                    document.setIndexFields(indexFields);

                    // add the document to batch
                    batch.addDocument(document);
                }
            }
        }
        catch(Exception e)
        {
            myLogger.error("Error in ProcessXMLS\n" + e.toString());

            throw(e);
        }

    }

    private void ProcessMultiFilePerBatch(File[] files, ImportSettings impSet, MappingFile mf) throws Exception,OpenCaptureException
    {
        Batch batch = null;
        String batchname = "";
        String imagePath = impSet.getImagePath();
        String archivePath = impSet.getArchivePath();
        String batchclass = impSet.getBatchClass();
        String triggerExt = impSet.getTriggerExt().toUpperCase();
        XMLParser xmlParser = null;
        List mappings = null;
        File importFile=null;

        CSVReader csvReader = null;

        // create new batch
        batch = new Batch();

        // create batch name
        batchname = batch.createBatchName(impSet.getBatchNamePrefix(), impSet.getBatchNameSuffix());

        myLogger.debug("batchName=" + batchname);
        myLogger.debug("imagePath=" + imagePath);
        myLogger.debug("archivePath=" + archivePath);

        // create new batch
        batch.CreateBatch(batchclass, batchname);
        myLogger.debug("Created batch: " + batchname);
        // get batch id
        long batchID = Long.parseLong(batch.getID());
        myLogger.debug("Got batch id");
        // open batch
        batch.OpenBatch(batchID);
        myLogger.debug("Opened batch");

        // loop through files
        for(int b=0;b<files.length;b++)
        {
            File file = files[b];
            List entries = null;
            int documentNumber = 1;

            try
            {

                try
                {

                    // check if trigger used
                    if(impSet.getTriggerExt().length()>0)
                    {

                        if(triggerExt.toUpperCase().equals("XML"))
                        {

                            if(mf.getTrigger().toUpperCase().equals("XMLS"))
                            {
                                ProcessXMLS(file, batch, impSet);

                                // move trigger file to archive path
                                FileAccess.Move(file.getAbsolutePath(), impSet.getArchivePath(), true);

                                
                            }

                            if(mf.getTrigger().toUpperCase().equals("XMLM"))
                            {
                                ProcessXMLM(file, batch, impSet);

                                // move trigger file to archive path
                                FileAccess.Move(file.getAbsolutePath(), impSet.getArchivePath(), true);
                            }
                        }
                        else
                        {
                            // all others treated like CSV

                            if(impSet.getMappingFileObj() != null)
                            {
                                mappings = impSet.getMappingFileObj().getMappings();

                                String delimiter = impSet.getMappingFileObj().getDelimiter();
                                char delim = ',';

                                if(delimiter.length()>0)
                                    delim = delimiter.charAt(0);

                                csvReader = new CSVReader(new FileReader(file), delim, '"', (int)impSet.getMappingFileObj().getSkipLines());
                            }
                            else
                            {
                                // create default filename mapping
                                mappings = new ArrayList();
                                Mapping mapping = new Mapping("1", "FILENAME");

                                char delim = ',';
                                csvReader = new CSVReader(new FileReader(file), delim, '"', 0);
                            }

                            // read all into list
                            entries = csvReader.readAll();
                            csvReader.close();
                        }
                    }

                    if(entries != null)
                    {
                        
                        Mapping fileNameMap = mf.findMappingByDestination("FILENAME");
                        Mapping formIDMap = mf.findMappingByDestination("FORMID");
                        int fileNameCol = Integer.parseInt(fileNameMap.getSource());
                        int formIDCol = Integer.parseInt(formIDMap.getSource());

                        for(int fc = 0;fc<entries.size();fc++)
                        {
                            String[] entry = (String[])entries.get(fc);
                            String path = file.getParent();
                            path = Path.FixPath(path);
                            // get file name
                            String fileName = entry[fileNameCol];
                           
                            // create file object
                            importFile = new File(path + fileName);

                            String[] tiffs = new String[1];

                            tiffs[0] = importFile.getAbsolutePath();

                            // check if file exists
                            if((new File(tiffs[0]).exists()))
                            {

                                //List list = ToTIFF.toTIFF(tiffs, OpenCaptureCommon.getBatchFilePath(batchID), "00000000", false, 200);
                                List list = ToTIFF.toTIFF(tiffs, batch.getImageFilePath(),archivePath, "00000000", false, 200);

                                String formIDColValue = entry[formIDCol];
                                ValueMap formIDVM = formIDMap.findValueMap(formIDColValue);

                                // add check here for db type when connecting to db
                                String formID = "DOCUMENT";
                                String vmapValue = "Document";
                                String docName = "Document";

                                // get index fields from batch class
                                IndexFields indexFields = batch.getConfigurations().getIndexFields();

                                // check doctype default value
                                IndexField ndxDocType = indexFields.getIndexField("DOC_TYPE");

                                // if doc type index field found
                                if(ndxDocType.getName().length()>0 && ndxDocType.getValue().length()>0)
                                {
                                    myLogger.debug("Default DOC_TYPE set in batch class: " + ndxDocType.getName());
                                    myLogger.debug(ndxDocType.toString());
                                    
                                    // set form id, vmap value, and doc name
                                    if(ndxDocType.getKeyValue().length()>0)
                                        formID = ndxDocType.getKeyValue();
                                    else
                                        formID = ndxDocType.getValue();

                                    vmapValue = ndxDocType.getValue();
                                    
                                    myLogger.debug("FormID: " + formID);
                                    myLogger.debug("DocType: " + vmapValue);

                                }
                                
                                if(formIDVM != null)
                                {
                                    myLogger.debug("Override DOC_TYPE from mapping template");
                                    
                                    formID = formIDVM.getKeyValue();
                                    vmapValue = formIDVM.getValue();
                                }
                                
                                myLogger.debug("FormID: " + formID);
                                myLogger.debug("DocType: " + vmapValue);
                                
                                // set index field doctype value
                                indexFields.getIndexField("DOC_TYPE").setValue(vmapValue);
                                indexFields.getIndexField("DOC_TYPE").setKeyValue(formID);

                                Configuration documentConfig = batch.getConfigurations().getConfiguration(vmapValue);

                                if(documentConfig != null)
                                {
                                    docName = documentConfig.getName();
                                }

                                Document document = new Document();

                                document.setFormID(formID);
                                document.setName(docName);
                                document.setNumber(documentNumber);
                                ++documentNumber;

                                for(int p=0;p<list.size();p++)
                                {
                                    ++totalPages;

                                    String pageName = (String)list.get(p);
                                    pageName = Path.getFileName(pageName);

                                    Page page = new Page(pageName, totalPages, totalPages);

                                    document.addPage(page);
                                }

                                if(document!=null)
                                {
                                    for(int e=0;e<mappings.size();e++)
                                    {
                                        Mapping map = (Mapping)mappings.get(e);

                                        if(map.getDestination().equals("FILENAME") || map.getDestination().equals("FORMID"))
                                            continue;

                                        int col = -1;
                                        String entryValue = "";
                                        String vmValue = "";
                                        String vmKeyValue = "";
                                        if(map.getSource().length()>0)
                                        {
                                            col = Integer.parseInt(map.getSource());
                                            entryValue = entry[col];
                                        }

                                        // if value map exists, check it for mapping
                                        if(map.getValueMapList().size()>0)
                                        {
                                            ValueMap vm = map.findValueMap(entryValue);

                                            if(vm != null)
                                            {
                                                vmValue = vm.getValue();
                                                vmKeyValue = vm.getKeyValue();
                                            }
    //                                        else
    //                                            vmValue = entryValue;

                                        }
                                        else
                                            vmValue = entryValue;

                                        // set index field value
                                        indexFields.getIndexField(map.getDestination()).setValue(vmValue);
                                        indexFields.getIndexField(map.getDestination()).setKeyValue(vmKeyValue);
                                    }

                                    // set index fields in document
                                    document.setIndexFields(indexFields);

                                    // add the document to batch
                                    batch.addDocument(document);
                                }
                            }   // if import file exists
                        }
                    }
                    else
                    {
                        // entries is null meaning no trigger
                        // all files should be added to current batch
                        // file is current image file


                    }

                    batch.CloseBatch();

                    // delete trigger file
                    if(entries != null)
                    {
                        file.delete();
                    }
                }
                catch(OpenCaptureException oe)
                {
                     myLogger.error("Error creating batch!\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(oe));
                }
                catch(OpenCaptureImagingException oie)
                {
                     myLogger.error("Error creating batch!\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(oie));
                }
            }
            catch(Exception e)
            {
                myLogger.error("Error creating batch!\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(e));

                try
                {
                    batch.DeleteBatch();
                }
                catch(Exception ex)
                {
                    myLogger.error("Problem deleting batch:\n" + net.filterlogic.util.StackTraceUtil.getStackTrace(ex));

                    throw(ex);
                }
            }
        } // file for loop
    }

    private Document createDocument()
    {

        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try
        {
            if(args.length<1)
            {
                System.out.println("Must pass path to configuration file.");
                System.exit(0);
            }

            OCImport ocImport = new OCImport(args[0]);

            ocImport.importBatches();
            
        }
        catch(Exception e)
        {
            myLogger.fatal(net.filterlogic.util.StackTraceUtil.getStackTrace(e));
            e.printStackTrace();
        }
    }

}