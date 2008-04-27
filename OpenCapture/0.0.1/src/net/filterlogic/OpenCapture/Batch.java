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

import java.util.Date;
import javax.persistence.Query;
import net.filterlogic.OpenCapture.data.DBManager;
import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.DateUtil;

/**
 * The Batch class represents a batch within the OpenCapture.  When a batch is loaded
 * this object is populated.  All operations on the batch will start with the Batch 
 * object.
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
    private Logging logging;
    private Log log;
    
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
    
    private long batchID = 0;
    
    private boolean fsLocked = false;
    private boolean dbLocked = false;
   
    private String moduleID = "";

    public Batch()
    {
        logging = new Logging();
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
                                                                            scanDate , 1,OpenCaptureCommon.BATCH_STATUS_READY, 
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
            logException(dbe.toString());
            throw new OpenCaptureException(dbe.toString());
        }

        // kill xml parser obj
        xmlParser = null;
    }

    /**
     * Log error message and add log object to logging.
     * @param message
     */
    protected void logException(String message)
    {
        log.setMessage(message);
        logging.addLog(log);
    }
    
    protected void loadBatch(long batchID) throws OpenCaptureException
    {
        // set batch id
        this.batchID = batchID;
        
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
        this.moduleID = moduleID;

        try
        {
            dbm = new DBManager();
            
            batchID = dbm.getNextBatch(moduleID);
            
            OpenBatch(batchID);
        }
        catch(Exception e)
        {
            logException(e.toString());
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
        //DBManager dbm = new DBManager();
        
        // of batch already locked, error.
        if(OpenCaptureCommon.isBatchXmlFileLocked(batchID))
            throw new OpenCaptureException("Batch is already being processed!");

        // set batch state in db to processing.
        dbm.setBatchStateByBatchID(batchID, OpenCaptureCommon.BATCH_STATUS_PROCESSING);

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

        // make sure this isn't a create batch, then add new log entry.
        if(moduleID.length()>0)
            log = new Log(this.moduleID,"");
    }

    /**
     * Close batch.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void CloseBatch() throws OpenCaptureException
    {
        long id = 0;

        try
        {
        net.filterlogic.OpenCapture.data.DBManager dbm = new net.filterlogic.OpenCapture.data.DBManager();

        // move batch to next queue.
        queues.moveNextQueue();

        // set end time
        log.setEndDateTime(DateUtil.getDateTime());

        // add log to logging object
        logging.addLog(log);

        // if no more queues, write output log
        if(this.currentQueue.getCurrentQueue().length()<1)
        {
            // log batch
            OpenCaptureCommon.writeBatchLog(logging);

            // remove lock file
            OpenCaptureCommon.unlockBatchXmlFile(batchID);

            // delete batch 
            dbm.deleteBatch(batchID);
        }
        else
        {
            // get id of next queue for batch
            id = dbm.getQueueIDByName(this.currentQueue.getQueueName());

            // set batch to next queue
            dbm.setBatchQueueByBatchIDQueueID(batchID, id);
        }

        // unlock batch file.
        OpenCaptureCommon.unlockBatchXmlFile(batchID);
        }
        catch(Exception e)
        {
            logException("Unable to close batch. " + e.toString());
            throw new OpenCaptureException(e.toString());
        }
        finally
        {
            // save the batch
            saveBatch();
        }
    }

    /**
     * Return the BatchClass object.
     * @return
     */
    public BatchClass getBatchClass()
    {
        return batchClass;
    }

    /**
     * 
     * @return
     */
    public Queue getQueue()
    {
        return currentQueue;
    }

    /**
     * 
     * @return
     */
    public String getBatchName()
    {
        return BatchName;
    }

    /**
     * 
     * @return
     */
    public String getCreationDateTime()
    {
        return CreationDateTime;
    }

    /**
     * 
     * @return
     */
    public String getCreateUser()
    {
        return CreateUser;
    }

    /**
     * 
     * @return
     */
    public String getID()
    {
        return ID;
    }

    /**
     * 
     * @param BatchName
     */
    protected void setBatchName(String BatchName)
    {
        this.BatchName = BatchName;
    }

    /**
     * 
     * @param CreationDateTime
     */
    protected void setCreationDateTime(String CreationDateTime)
    {
        this.CreationDateTime = CreationDateTime;
    }

    /**
     * 
     * @param CreateUser
     */
    protected void setCreateUser(String CreateUser)
    {
        this.CreateUser = CreateUser;
    }

    /**
     * 
     * @param ID
     */
    protected void setID(String ID)
    {
        this.ID = ID;
    }
    
    protected void saveBatch() throws OpenCaptureException
    {
        String batchXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        
        try
        {
            // create batch xml.
            batchXML += this.batchClass.getXML();
            batchXML += "<Batch Name=\"" + this.BatchName + "\" ID=\"" + this.ID + "\" ScanUser=\"\">\n";
            batchXML += "<BatchFields>\n";
            batchXML += this.batchFields.getXML();
            batchXML += "</BatchFields>\n";
            batchXML += "<BatchDataFields>\n";
            batchXML += this.batchDataFields.getXML();
            batchXML += "</BatchDataFields>\n";
            batchXML += "<Queues CurrentQueue=\"" + this.queues.getCurrentQueue().getQueueName() + "\">\n";
            batchXML += this.queues.getXML();
            batchXML += "</Queues>\n";
            batchXML += "<Logging>\n";
            batchXML += this.logging.getXML();
            batchXML += "</Logging>\n";
            batchXML += "<Documents>\n";
            
            batchXML += "</Documents>\n";
            batchXML += "</Batch>\n";
            batchXML += "</BatchClass>\n";
            
            // replace xml in batch XMLParser object
            xmlParser.parseDocument(batchXML);
            
            // save xml file to disk.
            xmlParser.saveDocument(this.batchXmlFileName);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to save batch[" + this.BatchName + "]. " + e.toString());
        }
    }
}
