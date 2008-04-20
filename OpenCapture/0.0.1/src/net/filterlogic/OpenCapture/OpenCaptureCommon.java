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

/**
 *
 * @author dnesbitt
 */
public class OpenCaptureCommon 
{
    public static String PATH_SEPARATOR = System.getProperty("file.separator");
    public static String BATCH_CLASS_NAME = "//BatchClass/@Name";
    public static String BATCH_CLASS_IMAGE_PATH = "//BatchClass/@ImagePath";
    public static String BATCH_CLASS_VERSION = "//BatchClass/@version";

    public static String BATCH_NAME = "//BatchClass/Batch/@Name";

    public static String BATCH_FIELDS = "//BatchClass/Batch/BatchFields/BatchField";
    public static String BATCH_FIELD = "//BatchClass/Batch/BatchFields/BatchField[@Name=\"<1>\"]";
    public static String BATCH_DATA_FIELDS = "//BatchClass/Batch/BatchDataFields/BatchDataField";
    public static String BATCH_DATA_FIELD = "//BatchClass/Batch/BatchDataFields/BatchDataField[@Name=\"<1>\"]";

    public static String QUEUES = "//BatchClass/Batch/Queues/*";
    public static String DOCUMENTS = "//BatchClass/Batch/Documents/Document";
    public static String DOCUMENT = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]";
    public static String INDEX_FIELDS = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]/IndexFields/IndexField";
    public static String INDEX_FIELD = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]/IndexFields/IndexField[@Name=\"<2>\"]";
    public static String INDEX_DATA_FIELDS = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]/IndexDataFields/IndexDataField";
    public static String INDEX_DATA_FIELD = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]/IndexDataFields/IndexDataField[@Name=\"<2>\"]";
    public static String ZONES = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]/Zones/Zone";
    public static String ZONE = "//BatchClass/Batch/Documents/Document[@Name=\"<1>\"]/Zones/Zone[@Name=\"<2>\"]";

    public static String OC_NAME_TAG = "Name";
    public static String OC_DOCUMENT_FORMID_TAG = "FormID";

    public static int BATCH_STATUS_READY = 0;
    public static int BATCH_STATUS_PROCESSING = 10;
    public static int BATCH_STATUS_ERROR = 20;
    public static int BATCH_STATUS_HOLD = 30;

    public static String BATHCXML_FOLDER_NAME = "BatchXML";

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
    
    public static void lockBatchXmlFile(long batchID) throws OpenCaptureException
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
    
    public static void unlockBatchXmlFile(long batchID) throws OpenCaptureException
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
    
    public static String getBatchClassXmlFile(String batchClassName) throws OpenCaptureException
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
}