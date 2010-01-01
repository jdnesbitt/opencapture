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

package net.filterlogic.OpenCapture;

//import java.util.HashMap;
//import net.filterlogic.util.DateUtil;
import java.io.File;
import java.util.List;
import net.filterlogic.io.Path;
import net.filterlogic.io.ReadWriteTextFile;
import net.filterlogic.util.DateUtil;

import java.io.FilenameFilter;
import java.io.FileFilter;
import net.filterlogic.util.xml.XMLParser;

class FileNameFilter implements FilenameFilter 
{
    String ext;

    public FileNameFilter(String ext) 
    {
        this.ext = ext;
    }

    public boolean accept(File dir, String name) 
    {
        return (name.endsWith(this.ext));
    }
}

/**
 *
 * @author dnesbitt
 */
public class OpenCaptureCommon 
{
    public static String PATH_SEPARATOR = System.getProperty("file.separator");
    public static String CURRENT_USER_NAME = System.getProperty("user.name");
    public static String BATCH_CLASS_NAME = "//BatchClass/@Name";
    public static String BATCH_CLASS_IMAGE_PATH = "//BatchClass/@ImagePath";
    public static String BATCH_CLASS_VERSION = "//BatchClass/@version";
    public static String BATCH_CLASS_PRIORITY = "//BatchClass/@Priority";
    public static String BATCH_CLASS_ID = "//BatchClass/@ID";
    public static String BATCH_CLASS_SITE_ID = "//BatchClass/@SiteID";

    public static String BATCH_NAME = "//BatchClass/Batch/@Name";
    public static String BATCH_ID = "//BatchClass/Batch/@ID";
    public static String BATCH_SCAN_USER = "//BatchClass/Batch/@ScanUser";

    public static String CUSTOM_CONFIGURATION_PROPERTIES = "//BatchClass/Configuration/CustomProperties/Property";
    public static String CUSTOM_CONFIGURATION_PROPERTY = "//BatchClass/Configuration/CustomProperties/Property[@Name=\"<1>\"]";
    public static String CONF_BATCH_FIELDS = "//BatchClass/Configuration/BatchFields/BatchField";
    public static String CONF_BATCH_FIELD = "//BatchClass/Configuration/BatchFields/BatchField[@Name=\"<1>\"]";
    public static String CONF_DOCUMENTS = "//BatchClass/Configuration/Documents/Document";
    public static String CONF_DOCUMENT = "//BatchClass/Configuration/Documents/Document[@Name=\"<1>\"]";
    public static String CONF_INDEX_FIELDS = "//BatchClass/Configuration/IndexFields/IndexField";
    public static String CONF_INDEX_FIELD = "//BatchClass/Configuration/IndexFields/IndexField[@Name=\"<1>\"]";
    public static String ZONES = "//BatchClass/Configuration/Documents/Document[@Name=\"<1>\"]/Zones/Zone";
    public static String ZONE = "//BatchClass/Configuration/Documents/Document[@Name=\"<1>\"]/Zones/Zone[@Name=\"<2>\"]";

    public static String QUEUES = "//BatchClass/Configuration/Queues/*";
    public static String CURRENT_QUEUE = "//BatchClass/Configuration/Queues/@CurrentQueue";
    public static String QUEUE = "//BatchClass/Configuration/Queues/Queue[@Name=\"<1>\"]";
    
    public static String LOG_FIELDS = "//BatchClass/Batch/Logging/Log";

    public static String BATCH_FIELDS = "//BatchClass/Batch/BatchFields/BatchField";
    public static String BATCH_FIELD = "//BatchClass/Batch/BatchFields/BatchField[@Name=\"<1>\"]";

    // Document tag that contains batch class configuration.
    public static String DOCUMENTS = "//BatchClass/Batch/Documents/Document";
    public static String DOCUMENT = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]";
    public static String INDEX_FIELDS = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]/IndexFields/IndexField";
    public static String INDEX_FIELD = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]/IndexFields/IndexField[@Name=\"<2>\"]";
    public static String LOOSE_PAGES = "//BatchClass/Batch/Pages/Page";
    public static String LOOSE_PAGE = "//BatchClass/Batch/Pages/Page[@Name=\"<1>\"]";

    public static String CUSTOM_DOCUMENT_PROPERTIES = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]/CustomProperties/Property";
    public static String CUSTOM_DOCUMENT_PROPERTY = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]/CustomProperties/Property[@Name=\"<1>\"]";
    
    // Document tag that contains actual index data
    public static String PAGES = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]/Pages/Page";
    public static String PAGE = "//BatchClass/Batch/Documents/Document[@Number=\"<1>\"]/Pages/Page[@Name=\"<2>\"]";
    
    public static String OC_NAME_TAG = "Name";
    public static String OC_DOCUMENT_FORMID_TAG = "FormID";
    public static String OC_DOCUMENT_NUMBER_TAG = "Number";
    public static String OC_LOG_TAG = "Log";

    public static int BATCH_STATUS_READY = 0;
    public static int BATCH_STATUS_PROCESSING = 10;
    public static int BATCH_STATUS_ERROR = 20;
    public static int BATCH_STATUS_HOLD = 30;

    public static String DOCUMENT_EXCEPTION_PROPERTY = "OC_DOCUMENT_EXCEPTION";

    public static String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";

    private static String LOG_FOLDER = "Logs";

    public static String BATHCXML_FOLDER_NAME = "BatchXML";

    /**
     * Returns the application root path.  Appends a slash at the end.
     * @return Root application path.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public static String getRootPath() throws OpenCaptureException
    {
        String rootPath = "";
        
        try
        {
            // get application path
            java.io.File path = new java.io.File(".");
            rootPath = path.getCanonicalPath();
            
            if(rootPath.lastIndexOf(PATH_SEPARATOR) != rootPath.length())
                rootPath += PATH_SEPARATOR;
        }
        catch(java.io.IOException ioe)
        {
            throw new OpenCaptureException(ioe.toString());
        }
        
        return rootPath;
    }
    
    public static String toHex8(long x)
    {
        // Format to hexadecimal
        String s = Long.toHexString(x);
        
        if (s.length() % 8 != 0) 
        {
            // get remainder and prepend 0's
            int r = 8 - s.length();
            
            for(int i=0;i<r;i++)
                // Pad with 0
                s = "0"+s;
        }
        
        return s;
    }
    
    /**
     * Convert a List to a String array.
     * 
     * @param list List containing strings.
     * 
     * @return String array.
     */
    public static String[] ListToStringArray(List list)
    {
        if(list!=null)
        {
            String[] strings = new String[list.size()];

            for(int i=0;i<list.size();i++)
                strings[i] = (String)list.get(i);

            return strings;
        }
        else
            return null;
    }
    
    public static String getBatchFileName(long batchID) throws OpenCaptureException
    {
        String folderID = toHex8(batchID);
        
        String batchFile = "";
        
        try
        {
            batchFile = getRootPath() + BATHCXML_FOLDER_NAME + PATH_SEPARATOR + folderID + PATH_SEPARATOR + folderID + ".xml";
        }
        catch(OpenCaptureException oce)
        {
            throw new OpenCaptureException("Unable to getBatchName[" + batchFile + "].  " + oce.toString());
        }
        
        return batchFile;
    }
    
    /**
     * Get path to batch xml file.
     * @param batchID
     * @return Path to batch xml file.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public static String getBatchFilePath(long batchID) throws OpenCaptureException
    {
        String folderID = toHex8(batchID);
        
        String batchFile = "";
        
        try
        {
            batchFile = getRootPath() + BATHCXML_FOLDER_NAME + PATH_SEPARATOR + folderID + PATH_SEPARATOR;
        }
        catch(OpenCaptureException oce)
        {
            throw new OpenCaptureException("Unable to getBatchFilePath[" + batchFile + "].  " + oce.toString());
        }
        
        return batchFile;
    }

    /**
     * Return batch folder name.
     * @param batchID
     * @return Batch folder name (00000034).
     */
    public static String getBatchFolderName(long batchID)
    {
        return toHex8(batchID);
    }

    /**
     * Lock a batch xml file.
     * @param batchID ID of batch file to lock.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    protected static void lockBatchXmlFile(long batchID) throws OpenCaptureException
    {
        String batchFile = "";
        
        try
        {
            if(!isBatchXmlFileLocked(batchID))
                batchFile = getBatchFileName(batchID);
            else
                throw new OpenCaptureException("Batch [" + String.valueOf(batchID) + "] already locked!");
            
            new java.io.File(batchFile + ".lock").createNewFile();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Error locking batch xml file[" + batchFile + "].  " + e.toString());
        }
    }
    
    /**
     * Unlock a batch xml file.
     * @param batchID ID of batch file to unlock.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    protected static void unlockBatchXmlFile(long batchID) throws OpenCaptureException
    {
        String batchFile = "";

        try
        {
            batchFile = getBatchFileName(batchID);
            (new java.io.File(batchFile + ".lock")).delete();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Error locking batch xml file[" + batchFile + "].  " + e.toString());
        }
    }
    
    /**
     * Checks to see if the batch xml file is locked.
     * @param batchID ID of batch to check.
     * @return Returns true if file is locked, else false.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public static boolean isBatchXmlFileLocked(long batchID) throws OpenCaptureException
    {
        boolean ok = false;
        String batchFile = "";
        
        try
        {
            batchFile = getBatchFileName(batchID);
            ok = (new java.io.File(batchFile + ".lock")).exists();
        }
        catch(Exception e)
        {
            ok = true;
        }
        
        return ok;
    }
    
    /**
     * Gets the path to the batch class xml definition file.
     * 
     * @param batchClassName
     * 
     * @return Returns a string containing the path to the definition file.
     * 
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    protected static String getBatchClassXmlFile(String batchClassName) throws OpenCaptureException
    {
        String batchClassXmlFile = "";
        
        try
        {
            // remove all spaces from batch class name to create a batch class xml file name without spaces.
            // this is how the template file is named.
            if(batchClassName.length()>0)
                batchClassXmlFile = batchClassName.replaceAll(" ", "").toLowerCase() + ".xml";
            else
                throw new OpenCaptureException("BatchClassName is empty.");

            // set batch class xml file name.  this is the batch class template file.
            batchClassXmlFile = getRootPath() + "config" + PATH_SEPARATOR + batchClassXmlFile;

        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to getBatchClassXmlFile[" + batchClassXmlFile + "].  " + e.toString());
        }
        
        return batchClassXmlFile;
    }
    
    /**
     * Write batch log to history log when batch completes all queues
     * or is deleted.
     * @param logging Logging object that contains all log data.
     * 
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    protected static void writeBatchLog(Batch batch) throws OpenCaptureException
    {
        XMLParser xml = new XMLParser();

        Logging logging = batch.getLogging();

        List logs = logging.getLogs();
        int pageCount = 0;
        String logEntries = "<Batch Name=\"" + batch.getBatchName() + 
                "\" CreateDateTime=\"" + batch.getCreationDateTime() + 
                "\" CreateUser=\"" + batch.getCreateUser() + "\">\n";

        logEntries += "<Documents Count=\"" + batch.getDocuments().Count() + "\" />\n";
        
        try
        {
        // get page count
        for(int i=1;i<=batch.getDocuments().Count();i++)
            // TODO:  fix NPE here
            pageCount += batch.getDocuments().getDocument(i).getPages().Count();
        }
        catch(Exception e)
        {
            // do nothing
        }

        // add loose page count
        pageCount += batch.getLoosePageCount();

        logEntries += "<Pages Count=\"" + pageCount + "\" />\n";

        logEntries += "<Logging>\n";

        String rootPath = "";

        logEntries += batch.getLogging().getXML();

        logEntries += "</Logging>\n";
        logEntries += "</Batch>\n";

        rootPath = getRootPath() + LOG_FOLDER;

        if(!Path.createPath(rootPath))
            throw new OpenCaptureException("Unable to create log folder!");

        ReadWriteTextFile rwt = new ReadWriteTextFile();
        String today = DateUtil.getDateTime("MMyyyy");

        File file = new File(rootPath + PATH_SEPARATOR + today + ".log.xml");

        try
        {
            rwt.appendContents(file, logEntries);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to write batch log. " + e.toString());
        }
        finally
        {
            rwt = null;
        }
    }
    
    /**
     * Retrieves file list.  Directories are not returned.
     * @param path Path to directory.
     * @return File array.
     */
    public static File[] getFileList(String path)
    {
        if(!new File(path).exists())
            return null;

        // This filter only returns directories
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
     * Get file list with matching extension.
     * @param path Path to search.
     * @param ext Extension to search for.
     * @return Returns an array of File objects.
     */
    public static File[] getFileList(String path,String ext)
    {
        if(!new File(path).exists())
            return null;

        File[] files = new File(path).listFiles(new FileNameFilter(ext));

        return files;
    }
    
    /**
     * Get this computer's name.
     * @return String containing computer name.  Empty string returned if exception occurs.
     */
    public static String getLocalHostName()
    {
        try 
        {
            java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
            return localMachine.getHostName();
        }
        catch (java.net.UnknownHostException uhe) 
        {
            System.out.println(uhe.toString());
            
            return "";
        }
    }
    
    /**
     * Get value of string.
     * @param value Value to retrieve.
     * @return Returns a string containing value. If value is null, empty string returned.
     */
    public static String getStringValue(Object value)
    {
        String s="";
        
        try
        {
            if(value.getClass().toString().equals("class java.lang.String"))
            {            
                if(value != null)
                    s = value.toString();
                else
                    s = "";
            }
            
            if(value.getClass().toString().equals("class java.lang.Integer"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }

            if(value.getClass().toString().equals("class java.lang.Short"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }
            
            if(value.getClass().toString().equals("class java.lang.Byte"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }

            if(value.getClass().toString().equals("class java.lang.Character"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }

            if(value.getClass().toString().equals("class java.lang.Boolean"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "False";
            }

            if(value.getClass().toString().equals("class java.lang.Double"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }
            
            if(value.getClass().toString().equals("class java.lang.Float"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }
            
            if(value.getClass().toString().equals("class java.lang.Long"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "0";
            }
            
            if(value.getClass().toString().equals("class java.lang.Object"))
            {
                if(value != null)
                    s = value.toString();
                else
                    s = "";
            }
        }
        catch(Exception e)
        {
            s = "";
        }
        
        return s;
    }
}