/*
Copyright 2008 - 2010 Filter Logic

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

package net.filterlogic.OpenCapture.data;

import net.filterlogic.OpenCapture.OpenCaptureException;
import net.filterlogic.io.Path;
import net.filterlogic.util.xml.XMLParser;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.spi.PersistenceUnitInfo;
import net.filterlogic.OpenCapture.OpenCaptureException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.spi.PersistenceProvider;

import java.util.List;
import java.util.Map;
import javax.management.Query;
import net.filterlogic.OpenCapture.OpenCaptureCommon;

/**
 * This class is used to initialize the OpenCapture database.  You must provide the
 * database user, password, db url, and driver class.  The TopLink persistence
 * library is used by OpenCapture.
 *
 * @author dnesbitt
 */
public class InitializeDB
{
    private EntityManagerFactory entMgrFac = Persistence.createEntityManagerFactory("OpenCapturePU");
    private EntityManager entMgr;

    private XMLParser initDBParser = null;
    private XMLParser classParser = new XMLParser();

    private String ocRootPath = "";
    private String ocConfigFile = "OpenCaptureConfig.xml";
    //private String ocInitDBConfigFile = "initDB.xml";
    private String ocConfigPath = "";

    private static final String initDBQueuesXpath = "/OpenCapture/Queues/Queue";
    private static final String initDBBatchClassesXpath = "/OpenCapture/initDB/BatchClasses/BatchClass";
    private static final String initDBUsersXpath = "/OpenCapture/initDB/Users/User";
    private static final String batchClassQueuesXpath = "/BatchClass/Configuration/Queues/Queue";

    /**
     * Default constructor
     *
     * @throws OpenCaptureException
     */
    public InitializeDB() throws OpenCaptureException
    {
        try
        {
            // get path to application root folder
            ocRootPath = Path.FixPath(Path.getApplicationPath());
            // set config path
            ocConfigPath = Path.FixPath(ocRootPath + "config");

            // init db parser and load initdb.xml file
            initDBParser = new XMLParser();
            initDBParser.loadDocument(ocConfigPath + ocConfigFile);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    /**
     * Initialize OpenCapture database.
     *
     * Only call this method if persistence.xml has ddl-generation set accordingly.
     *
     * @throws OpenCaptureException
     */
    private void InitDB() throws OpenCaptureException
    {
        try
        {
            // this will create tableshttp://www.google.com/
            entMgr = entMgrFac.createEntityManager();

            // sleep for a few seconds to allow table creation to occur
            //Thread.sleep(5000);

            // insert default queues.
            AddQueues();
            // add bc & lkp entries
            AddBatchClasses();

            
        }
        catch(Exception e)
        {

        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();

            if(entMgrFac.isOpen())
                entMgrFac.close();

            entMgr = null;
        }
    }

    /**
     * Add queues to Queues table.
     *
     * @throws OpenCaptureException
     */
    private void AddQueues() throws OpenCaptureException
    {
        EntityTransaction tx = null;

        try
        {
            // get list of queues to insert into Queues table
            List queueNames = initDBParser.getNodeList(initDBQueuesXpath);

            if(queueNames.size()>0)
            {
                tx = entMgr.getTransaction();

                for(int i=0;i<queueNames.size();i++)
                {
                    tx.begin();

                    // insert standard queues
                    Map map = (Map)queueNames.get(i);

                    Long qid = Long.valueOf(map.get("ID").toString());
                    String qname = map.get("Name").toString();
                    String qdesc = map.get("Description").toString();

                    // create queue
                    Queues queues = new Queues(qid, qname);
                    queues.setQueueDesc(qdesc);

                    entMgr.persist(queues);

                    tx.commit();
                }

                
            }

        }
        catch(Exception e)
        {
            // if transaction still active, roll it back.
            if(tx!=null)
                if(tx.isActive())
                    tx.rollback();

            throw new OpenCaptureException("Error initializing database\n" + e.toString());
        }
    }

    private void AddBatchClasses() throws OpenCaptureException
    {
        EntityTransaction tx = null;

        try
        {
            // get list of queues to insert into Queues table
            List bcNames = initDBParser.getNodeList(initDBBatchClassesXpath);

            if(bcNames.size()>0)
            {
                tx = entMgr.getTransaction();
                tx.begin();

                for(int i=0;i<bcNames.size();i++)
                {
                    // insert standard queues
                    Map map = (Map)bcNames.get(i);

                    String bcname = map.get("File").toString();
                    String bcdName = map.get("Name").toString();
                    String desc = map.get("Description").toString();
                    Long bid = Long.valueOf(0);

                    // create bc
                    BatchClass batchClass = new BatchClass(Long.valueOf(0),bcdName, bcname, desc, ocRootPath + "test");

                    entMgr.persist(batchClass);

                    // commit batch class insert
                    tx.commit();

                    // get batch class id
                    bid = batchClass.getBatchClassId();

                    // load batch class config file
                    classParser = new XMLParser();
                    classParser.loadDocument(ocConfigPath + bcname);

                    // update carrier image path in batch class config file
                    classParser.setValue(OpenCaptureCommon.BATCH_CLASS, "ImagePath", ocRootPath + "test");
                    // save updated file
                    classParser.saveDocument(ocConfigPath + bcname);

                    // get ordered queue list
                    List queueNames = classParser.getNodeList(batchClassQueuesXpath);

                    if(queueNames.size()>0)
                    {
                        // begin queues to batch class lkp
                        tx.begin();

                        for(int b=0;b<queueNames.size();b++)
                        {
                            // get q map
                            Map qmap = (Map)queueNames.get(b);

                            long qid = Long.valueOf(qmap.get("ID").toString());

                            String qname = qmap.get("Name").toString();

                            LkpBatchclassQueuesPK lbcqpk = new LkpBatchclassQueuesPK(qid, bid);
                            LkpBatchclassQueues lbcq = new LkpBatchclassQueues(lbcqpk,b);

                            entMgr.persist(lbcq);
                        }
                        
                        // commit lkp
                        tx.commit();
                    }
                }

                
            }

        }
        catch(Exception e)
        {
            // if transaction stillList active, roll it back.
            if(tx!=null)
                if(tx.isActive())
                    tx.rollback();

            throw new OpenCaptureException("Error initializing database\n" + e.toString());
        }
    }


    public static void main(String[] args)
    {

        try
        {
            //InitializeDB idb = new InitializeDB("opencapture", "corwin1", "jdbc:mysql://localhost:3306/opencapture","com.mysql.jdbc.Driver" );
            InitializeDB idb = new InitializeDB();
            
            idb.InitDB();
        }
        catch(OpenCaptureException e)
        {
            System.out.println(e.toString());
        }
    }
}
