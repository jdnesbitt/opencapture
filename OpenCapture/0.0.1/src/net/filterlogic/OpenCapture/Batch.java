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
import net.filterlogic.io.Path;

/**
 * The Batch class represents a batch within the OpenCapture.  When a batch is loaded
 * this object is populated.  All operations on the batch will start with the Batch 
 * object.
 * @author dnesbitt
 */
public class Batch 
{
    net.filterlogic.OpenCapture.data.Batches tblBatch;
    
    private OpenCaptureConfig ocConfig = null;
    
    private String rootPath = "";
    
    private Query query;
    
    private BatchClass batchClass = null;
    //private Queues queues = null;
    private BatchFields  batchFields = null;
    private Logging logging = null;
    private Log log = null;
    private Pages loosePages = null;
    private Documents documents = null;
    private Configurations configurations = null;
    private XMLParser xmlParser = null;    

    private DBManager dbm = null;

    private String batchClassXmlFile = "";
    private boolean hasException = false;
    /**
     * Path and name of xml batch file.
     */
    private String batchXmlFileName = "";
    private String batchXmlFilePath = "";
    
    private String batchClassName = "";
    private String BatchName = "";
    private String CreationDateTime = "";
    private String CreateUser = "";
    private String ID = "";
    private Queue currentQueue = new Queue();
    
    private long batchID = 0;
    
    //private boolean fsLocked = false;
    //private boolean dbLocked = false;
   
    private String moduleID = "";

    public Batch() throws OpenCaptureException
    {
        dbm = new DBManager();;

        logging = new Logging();

        ocConfig = new OpenCaptureConfig();
    }

    /**
     * Use this constructor when OC configuration and batch class configuration only is needed.
     * The OCBR utility uses this constructor as database connectivity isn't required.  Only access
     * to the OC config and batch class objects are required.
     * @param BatchClass Name of batch class to load.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public Batch(String BatchClass) throws OpenCaptureException
    {
        // load oc config object
        ocConfig = new OpenCaptureConfig();        

        // load batch class object
        loadBatchClass(batchClassName);
    }
    
    public Batch(boolean useDBM) throws OpenCaptureException
    {
        if(useDBM)
        {
            dbm = new DBManager();;

            logging = new Logging();           
        }
        
        // load oc config object
        ocConfig = new OpenCaptureConfig();        
        
    }
    
    /**
     * Load batch class
     * @param batchClassName Name of batch class to load.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    private void loadBatchClass(String batchClassName) throws OpenCaptureException
    {
            // set batch class xml file name.  this is the batch class template file.
            this.batchClassName = batchClassName;
            this.batchClassXmlFile = OpenCaptureCommon.getBatchClassXmlFile(batchClassName);

            try
            {
                // instantiate instance
                xmlParser = new XMLParser();

                // load new copy of batch class setup xml
                xmlParser.loadDocument(this.batchClassXmlFile);
            }
            catch(Exception e)
            {
                throw new OpenCaptureException("Failed to load new copy of batch class[" + this.batchClassXmlFile + "].\n" + e.toString());
            }                

            // create batch class object
            batchClass = new BatchClass(xmlParser);
    }

    /**
     * Delete active batch.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void DeleteBatch() throws OpenCaptureException
    {
        try
        {
            deleteBatch(this.batchID);
        }
        catch(OpenCaptureException oe)
        {
            logException(oe.toString());
            throw new OpenCaptureException(oe.toString());
        }
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
        this.CreateUser = OpenCaptureCommon.CURRENT_USER_NAME;

        try
        {
            //net.filterlogic.OpenCapture.data.BatchClass bc = dbm.createBatchClass(0, batchClassName,"FFE Logistics Batches", "C:\\code\\java\\opencapture\\ffe\\images\\");

            // create batch db entry and set status to in use.
            id = dbm.createBatch(Long.parseLong("0"), batchName, Long.parseLong("1"),
                                                                            scanDate , 1,OpenCaptureCommon.BATCH_STATUS_READY, 
                                                                            Long.parseLong("1"));

            // save batch id
            this.batchID = id;
            this.setID(String.valueOf(id));

            // set batch xml file name which is also the folder name
            //this.rootPath = OpenCaptureCommon.getRootPath();

            // set batch class xml file name.  this is the batch class template file.
            //this.batchClassXmlFile = OpenCaptureCommon.getBatchClassXmlFile(batchClassName);

            try
            {
                // load new copy of batch class setup xml
                //xmlParser.loadDocument(this.batchClassXmlFile);
                loadBatchClass(batchClassName);
            }
            catch(Exception e)
            {
                throw new OpenCaptureException("Failed to load new copy of batch class[" + this.batchClassXmlFile + "].\n" + e.toString());
            }                

            // create batch class object
            //batchClass = new BatchClass(xmlParser);

            // save batch to Batch xml folder
            String batchFolderID = OpenCaptureCommon.toHex8(id);

            // set batch filename
            this.batchXmlFileName = batchFolderID +".xml";

            // set batch xml folder path
            this.batchXmlFilePath = OpenCaptureCommon.getBatchFilePath(id);

            // create batch folder
            if(!net.filterlogic.io.Path.createPath(this.batchXmlFilePath))
                throw new OpenCaptureException("Unable to create batch folder[" + batchFolderID + " @ " + this.batchXmlFilePath);

            // create image file path
            String imageFolder = getImageFilePath();
            if(!net.filterlogic.io.Path.createPath(imageFolder))
                throw new OpenCaptureException("Unable to create image file folder[" + imageFolder + "]");

            // add following to xml
            xmlParser.setValue(OpenCaptureCommon.BATCH_NAME, this.BatchName);
            xmlParser.setValue(OpenCaptureCommon.BATCH_ID, this.ID);
            xmlParser.setValue(OpenCaptureCommon.BATCH_SCAN_USER, this.CreateUser);

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
        getLog().setMessage(message);
        getLogging().addLog(getLog());
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
            //dbm = new DBManager();
            
            batchID = dbm.getNextBatch(moduleID);
            
            if(batchID>0)
                OpenBatch(batchID);
            else
                this.BatchName = "";
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
    public void OpenBatch(long batchID) throws OpenCaptureException
    {
        //DBManager dbm = new DBManager();
        
        // of batch already locked, error.
        if(OpenCaptureCommon.isBatchXmlFileLocked(batchID))
            throw new OpenCaptureException("Batch is already being processed!");

        // set batch state in db to processing.
        dbm.setBatchStateByBatchID(batchID, OpenCaptureCommon.BATCH_STATUS_PROCESSING);

        // load batch xml
        loadBatch(batchID);

        this.BatchName = xmlParser.getValue(OpenCaptureCommon.BATCH_NAME);
        this.ID = xmlParser.getValue(OpenCaptureCommon.BATCH_ID);
        this.CreateUser = xmlParser.getValue(OpenCaptureCommon.BATCH_SCAN_USER);

        // create batch class object
        batchClass = new BatchClass(xmlParser);

        // load configuration section
        configurations = new Configurations(xmlParser);
        
        // load batch log entries
        logging = new Logging(xmlParser, OpenCaptureCommon.LOG_FIELDS);

        // load batch fields
        String xPath = OpenCaptureCommon.BATCH_FIELDS;
        setBatchFields(new BatchFields(xmlParser, xPath));

        // get pages
        xPath = OpenCaptureCommon.LOOSE_PAGES;
        loosePages = new Pages(xmlParser, xPath);

        // load separated documents
        this.documents = new Documents(xmlParser);

        // get current queue
        this.currentQueue = configurations.getQueues().getCurrentQueue();
        
        // set module id
        this.moduleID = this.currentQueue.getQueueName();

        // lock batch xml file
        OpenCaptureCommon.lockBatchXmlFile(batchID);

        // make sure this isn't a create batch, then add new log entry.
        //if(moduleID.length()>0)
        log  = new Log(this.moduleID,OpenCaptureCommon.getLocalHostName());
        getLog().setStartDateTime(DateUtil.getDateTime());
    }

    public void CloseBatch(boolean withException,String msgException) throws OpenCaptureException
    {
        long id = 0;

        try
        {
            //dbm = new net.filterlogic.OpenCapture.data.DBManager();

            // move batch to exception q
            configurations.getQueues().setCurrentQueue("OCException");

            // set end time
            String batchCloseDate = DateUtil.getDateTime();

            getLog().setMessage(msgException);
            getLog().setEndDateTime(batchCloseDate);

            // add log to logging object
            getLogging().addLog(getLog());

            // if no more queues, write output log
            if(configurations.getQueues().getCurrentQueue().getQueueName().length()<1)
            {
                // remove lock file
                OpenCaptureCommon.unlockBatchXmlFile(batchID);

                // delete batch
                deleteBatch(batchID);
            }
            else
            {
                // get id of next queue for batch
                id = dbm.getQueueIDByName(this.currentQueue.getQueueName());

                int batchState = 0;

                // if exception occured, set batch state to error
                if(withException)
                    batchState = OpenCaptureCommon.BATCH_STATUS_ERROR;
                else
                    batchState = OpenCaptureCommon.BATCH_STATUS_READY;

                // set batch to next queue
                dbm.setBatchQueueByBatchIDQueueID(batchID, id,batchState);

                // unlock batch file.
                OpenCaptureCommon.unlockBatchXmlFile(batchID);
            }

        }
        catch(Exception e)
        {
            logException("Unable to close batch. " + e.toString());
            throw new OpenCaptureException(e.toString());
        }
        finally
        {
            dbm.CloseConnections();
            
            // save the batch
            saveBatch();
        }
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
            //dbm = new net.filterlogic.OpenCapture.data.DBManager();

            // move batch to next queue.
            configurations.getQueues().moveNextQueue();

            // set end time
            String batchCloseDate = DateUtil.getDateTime();
            getLog().setEndDateTime(batchCloseDate);

            // add log to logging object
            getLogging().addLog(getLog());

            // if no more queues, write output log
            if(configurations.getQueues().getCurrentQueue().getQueueName().length()<1)
            {
                // remove lock file
                OpenCaptureCommon.unlockBatchXmlFile(batchID);
                
                // delete batch
                deleteBatch(batchID);
            }
            else
            {
                // get id of next queue for batch
                id = dbm.getQueueIDByName(configurations.getQueues().getCurrentQueue().getQueueName());

                // set batch to next queue
                dbm.setBatchQueueByBatchIDQueueID(batchID, id,OpenCaptureCommon.BATCH_STATUS_READY);
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
            dbm.CloseConnections();

            // if valid next queue exists, save batch
            if(configurations.getQueues().getCurrentQueue().getQueueName().length()>0)
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
        return String.valueOf(batchID);
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
        this.ID = String.valueOf(batchID);
    }

    /**
     * Get image file path.
     * @return Returns batch image file path.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public String getImageFilePath() throws OpenCaptureException
    {
        String batchXMLFolderName = "";

        try
        {
            batchXMLFolderName = batchClass.getImagePath() + OpenCaptureCommon.getBatchFolderName(this.batchID) + OpenCaptureCommon.PATH_SEPARATOR;
            
            return batchXMLFolderName;
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    /**
     * Add loose page to batch.
     * @param page Page to add.
     */
    public void addLoosePage(Page page) throws OpenCaptureException
    {
        if(loosePages == null)
                loosePages = new Pages(page);
        else
            loosePages.addPage(page);
    }

    /**
     * Get page.
     * @param pageNumber
     * @return Page object.
     */
    public Page getPage(int pageNumber)
    {
        return loosePages.getPage(pageNumber);
    }

    /**
     * Get loose page count.
     * @return Int containing count of loose pages.
     */
    public int getLoosePageCount()
    {
        return loosePages.Count();
    }

    /**
     * Get page file name.
     * @param pageNumber
     * @return String containing file name without page.
     */
    public String getPageFileName(int pageNumber)
    {
        String pageName = "";
        
        Page page = loosePages.getPage(pageNumber);

        if(page!=null)
            pageName = page.getName();

        return pageName;
    }

    /**
     * Delete batch basedon specified batch id.
     * @param batchID ID of batch to delete.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    private void deleteBatch(long batchID) throws OpenCaptureException
    {
        String batchFilePath = "";
        String imageFilePath = "";

        try
        {
            // delete batch 
            dbm.deleteBatch(batchID);

            // log batch
            OpenCaptureCommon.writeBatchLog(this);

            // get batch xml folder path
            batchFilePath = OpenCaptureCommon.getBatchFilePath(batchID);
            
            // delete batch folder and contents
            boolean k = Path.deleteDir(new java.io.File(batchFilePath));
            
            // get image path
            imageFilePath = getImageFilePath();
            
            // delete images
            k = Path.deleteDir(new java.io.File(imageFilePath));

        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Error deleting batch [" + batchID + "].  " + e.toString());
        }
    }
    
    /**
     * Delete loose page from batch.
     * @param pageName Name of loose page.
     * @return Page being deleted.  Returns empty page if pageName doesn't exist.
     */
    public Page deleteLoosePage(String pageName)
    {
        return loosePages.deletePage(pageName);
    }
    
    protected void saveBatch() throws OpenCaptureException
    {
        String batchXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        
        try
        {
            // create batch xml.
            batchXML += this.batchClass.getXML();
            batchXML += this.configurations.getXML();
            batchXML += "<Batch Name=\"" + this.BatchName + "\" ID=\"" + this.ID + "\" ScanUser=\"" + this.CreateUser + "\">\n";
            batchXML += "<BatchFields>\n";
            batchXML += this.getBatchFields().getXML();
            batchXML += "</BatchFields>\n";
            batchXML += "<Logging>\n";
            batchXML += this.getLogging().getXML();
            batchXML += "</Logging>\n";
            batchXML += "<Pages>\n";
            batchXML += this.loosePages.getXML();
            batchXML += "</Pages>\n";
            batchXML += "<Documents>\n";
            batchXML += this.getDocuments().getXML();
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

//    public Queues getQueues() {
//        return queues;
//    }
//
//    public void setQueues(Queues queues) {
//        this.queues = queues;
//    }

    public BatchFields getBatchFields() {
        return batchFields;
    }

    public void setBatchFields(BatchFields batchFields) {
        this.batchFields = batchFields;
    }

//    public BatchFields getBatchDataFields() {
//        return batchDataFields;
//    }
//
//    public void setBatchDataFields(BatchFields batchDataFields) {
//        this.batchDataFields = batchDataFields;
//    }

    public Logging getLogging() {
        return logging;
    }

    public void setLogging(Logging logging) {
        this.logging = logging;
    }

    public Configurations getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Configurations configurations) {
        this.configurations = configurations;
    }
    
    //------------------------------------------------------
    
//    public void addQueues(Queue queue) 
//    {
//        if(queues == null)
//            queues = new Queues(queue);
//        else
//            this.queues.addQueue(queue);
//    }

    public void addBatchField(BatchField batchField) 
    {
        if(batchFields == null)
            batchFields = new BatchFields(batchField);
        else
            this.batchFields.addBatchField(batchField);
    }

    public void addLog(Log log) 
    {
        if(logging == null)
            logging = new Logging();

        this.logging.addLog(log);
    }

    public void addDocument(Document document) 
    {
        if(getDocuments() == null)
            setDocuments(new Documents(document));
        else
            this.getDocuments().addDocument(document);
    }

    public Documents getDocuments()
    {
        return documents;
    }

    public void setDocuments(Documents documents)
    {
        this.documents = documents;
    }

    /**
     * Get opencapture configuration.
     * @return OpenCaptureConfig object.
     */
    public OpenCaptureConfig getOcConfig()
    {
        return ocConfig;
    }

    public Log getLog()
    {
        return log;
    }

    /**
     * Batch has an exception.
     * 
     * @return True, if batch exception has occured.
     */
    public boolean hasException() 
    {
        return hasException;
    }

    /**
     * 
     * @param hasException
     */
    public void setException(boolean hasException) 
    {
        this.hasException = hasException;
    }
}
