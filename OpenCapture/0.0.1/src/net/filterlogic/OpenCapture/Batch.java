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

//import java.util.ArrayList;
//import java.util.List;
//import java.util.Hashtable;
import java.text.DateFormat;
import java.util.Date;
import javax.persistence.Query;
import net.filterlogic.OpenCapture.data.DBManager;
import net.filterlogic.util.xml.XMLParser;
//import java.net.URI;
//import java.net.URISyntaxException;
import net.filterlogic.util.DateUtil;

/**
 *
 * @author dnesbitt
 */
public class Batch 
{
    net.filterlogic.OpenCapture.data.Batches tblBatch;
    
    private String rootPath = "";
    
    private Query query;
    
    private BatchClass batchClass;
    private Queues queues;
    private BatchFields  batchFields;
    private BatchFields  batchDataFields;
    
    private DBManager dbm = new DBManager();
    
    private Documents documents;
    
    private XMLParser xmlParser = null;
    
    private String batchClassXmlFile = "";
    private String batchXmlFileName = "";
    private String batchXmlFilePath = "";
    
    private String batchClassName = "";
    private String BatchName = "";
    private String CreationDateTime = "";
    private String CreateUser = "";
    private String ID = "";
    private Queue currentQueue = new Queue();
    
    private boolean fsLocked = false;
    private boolean dbLocked = false;
   
    public Batch()
    {
    }
    
    /**
     * Create a new batch.
     * @param batchClassName Name of the batch class the batch will be created in.
     * @param batchName Name of the new batch.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void CreateBatch(String batchClassName,String batchName) throws OpenCaptureException
    {
        // instantiate instane
        xmlParser = new XMLParser();
        
        this.batchClassName = batchClassName;
        
        long id = 0;

        // set batch name
        this.BatchName = batchName;
        // set batch creation date time
        this.CreationDateTime = DateUtil.getDateTime();

        java.util.Date scanDate;
        
        try
        {
            scanDate = DateUtil.toDate(this.CreationDateTime, "yyyy/MM/dd HH:mm:ss");
        }
        catch(Exception de)
        {
            scanDate = new Date();
        }
        
        // set user name
        this.CreateUser = System.getProperty("user.name");

        try
        {
            //net.filterlogic.OpenCapture.data.BatchClass bc = dbm.createBatchClass(0, batchClassName,"FFE Logistics Batches", "C:\\code\\java\\opencapture\\ffe\\images\\");

            // create batch db entry and set status to in use.
            id = dbm.createBatch(Long.parseLong("0"), batchName, Long.parseLong("1"),
                                                                            scanDate , 1,OpenCaptureCommon.BATCH_STATUS_PROCESSING, 
                                                                            Long.parseLong("1"));

            // set batch xml file name which is also the folder name
            this.rootPath = OpenCaptureCommon.getRootPath();

            // set batch class xml file name.  this is the batch class template file.
            this.batchClassXmlFile = OpenCaptureCommon.getBatchClassXmlFile(batchClassName);

            try
            {
                // load new copy of batch class setup xml
                xmlParser.loadDocument(this.batchClassXmlFile);
            }
            catch(Exception e)
            {
                throw new OpenCaptureException("Failed to load new copy of batch class[" + this.batchClassXmlFile + "].\n" + e.toString());
            }                

            // save batch to Batch xml folder
            String batchFolderID = OpenCaptureCommon.toHex8(id);

            // set batch filename
            this.batchXmlFileName = batchFolderID +".xml";

            // set batch xml folder path
            this.batchXmlFilePath = OpenCaptureCommon.getBatchFilePath(id);

            // create batch folder
            if(!net.filterlogic.io.Path.createPath(this.batchXmlFilePath))
                throw new OpenCaptureException("Unable to create batch folder[" + batchFolderID + " @ " + this.batchXmlFilePath);

            try
            {
                // save new batch file
                xmlParser.saveDocument(OpenCaptureCommon.getBatchFileName(id));
            }
            catch(Exception ex)
            {
                throw new OpenCaptureException("Failed to save new batch [" + this.batchXmlFilePath + OpenCaptureCommon.PATH_SEPARATOR + this.batchXmlFileName + "].\n" + ex.toString());
            }
        }
        catch(Exception dbe)
        {
            throw new OpenCaptureException(dbe.toString());
        }

        // moark as locked in db (being processed)
        this.dbLocked = true;
        
        // kill xml parser obj
        xmlParser = null;
        
        OpenBatch(id);
    }

    protected void loadBatch(long batchID) throws OpenCaptureException
    {
        // set rootpath if empty
        if(this.rootPath.length()<1)
            this.rootPath = OpenCaptureCommon.getRootPath();

        try
        {
            xmlParser = new XMLParser();
            
            this.batchXmlFileName = OpenCaptureCommon.getBatchFileName(batchID);
            
            // load copy of batch into memory
            xmlParser.loadDocument(this.batchXmlFileName);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Failed to load batch[" + this.batchXmlFileName + "].\n" + e.toString());
        }
    }

    /**
     * Public OpenBatch method used by modules to open a batch.
     * @param moduleID Name of registered module id.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void getNextBatch(String moduleID) throws OpenCaptureException
    {
        long batchID = 0;
        
        try
        {
            dbm = new DBManager();
            
            batchID = dbm.getNextBatch(moduleID);

            OpenBatch(batchID);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
    
    /**
     * Open specified batch.
     * @param batchID ID of batch to open.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    protected void OpenBatch(long batchID) throws OpenCaptureException
    {

        if(OpenCaptureCommon.isBatchXmlFileLocked(batchID))
            throw new OpenCaptureException("Batch is already being processed!");

        // load batch xml
        loadBatch(batchID);

        // create batch class object
        batchClass = new BatchClass(xmlParser);

        String xPath = OpenCaptureCommon.BATCH_FIELDS;
        batchFields = new BatchFields(xmlParser, xPath);

        xPath = OpenCaptureCommon.BATCH_DATA_FIELDS;
        batchDataFields = new BatchFields(xmlParser, xPath);

        // get queues
        queues = new Queues(xmlParser);

        documents = new Documents(xmlParser);
        
        // lock batch xml file
        OpenCaptureCommon.lockBatchXmlFile(batchID);
    }

    /**
     * Return the BatchClass object.
     * @return
     */
    public BatchClass getBatchClass()
    {
        return batchClass;
    }

    public Queue getQueue()
    {
        return currentQueue;
    }

    public String getBatchName()
    {
        return BatchName;
    }

    public String getCreationDateTime()
    {
        return CreationDateTime;
    }

    public String getCreateUser()
    {
        return CreateUser;
    }

    public String getID()
    {
        return ID;
    }

    protected void setBatchName(String BatchName)
    {
        this.BatchName = BatchName;
    }

    protected void setCreationDateTime(String CreationDateTime)
    {
        this.CreationDateTime = CreationDateTime;
    }

    protected void setCreateUser(String CreateUser)
    {
        this.CreateUser = CreateUser;
    }

    protected void setID(String ID)
    {
        this.ID = ID;
    }
}
