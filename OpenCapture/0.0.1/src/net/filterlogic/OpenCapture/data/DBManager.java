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

package net.filterlogic.OpenCapture.data;

import java.util.Date;
import java.lang.reflect.*;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import net.filterlogic.OpenCapture.OpenCaptureException;

import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author dnesbitt
 */
public class DBManager 
{
    private EntityManagerFactory entMgrFac = Persistence.createEntityManagerFactory("OpenCapturePU");
    private EntityManager entMgr;
    private Query qry;
    
    private Batches tblBatches;
    private BatchClass tblBatchClasses;
    
    /**
     * Default constructor.
     */
    public DBManager()
    {
        
    }
    
    /**
     * Delete batch using batch id as key.
     * @param batchID Batch id.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void deleteBatch(long batchID) throws OpenCaptureException
    {

        try
        {
            entMgr = entMgrFac.createEntityManager();

            qry = entMgr.createNamedQuery("Batches.removeBatchByBatchID");
            
            qry.setParameter("batchId", batchID);

            qry.executeUpdate();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to delete batch! " + e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }
    
    /**
     * Set batch queue using batch id.
     * @param batchID Batch id.
     * @param queueID Id of queue to move batch to.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void setBatchQueueByBatchIDQueueID(long batchID, long queueID, int batchState) throws OpenCaptureException
    {
        EntityTransaction tx = null;
                
        try
        {
            entMgr = entMgrFac.createEntityManager();

            tx = entMgr.getTransaction();
            tx.begin();

            qry = entMgr.createNamedQuery("Batches.updateBatchQueueByBatchIDQueueID");

            qry.setParameter("batchId", batchID);
            qry.setParameter("queueId", queueID);
            qry.setParameter("batchState", batchState);

            qry.executeUpdate();

            tx.commit();
        }
        catch(Exception e)
        {
            tx.rollback();
            throw new OpenCaptureException("Unable to set batch queue using queue id. " + e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }
    
    /**
     * Set batch state using batch id as key.
     * @param batchID Batch id.
     * @param stateID Batch state id located in OpenCaptureCommon.BATCH_STATE....
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void setBatchStateByBatchID(long batchID, long stateID) throws OpenCaptureException
    {
        EntityTransaction tx = null;
        
        try
        {
            entMgr = entMgrFac.createEntityManager();
            
            tx = entMgr.getTransaction();
            tx.begin();
            
            qry = entMgr.createNamedQuery("Batches.updateBatchStateByBatchID");
            
            qry.setParameter("batchId", batchID);
            qry.setParameter("batchState", stateID);
  
            qry.executeUpdate();
            
            tx.commit();
        }
        catch(Exception e)
        {
            tx.rollback();
            throw new OpenCaptureException("Unable to set batch queue using queue id. " + e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
            
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }
    
    /**
     * Close entety mangr factory connections
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void CloseConnections() throws OpenCaptureException
    {
        try
        {
            if(entMgrFac.isOpen())
                entMgrFac.close();

            entMgrFac = null;
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
    /**
     * Get queue id specified by queue name.
     * @param queueName Name of queue to retrieve id.
     * @return Return long containing queue id.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public long getQueueIDByName(String queueName) throws OpenCaptureException
    {
        List list;
        Queues queues;
        
        try
        {
            entMgr = entMgrFac.createEntityManager();
            
            qry = entMgr.createNamedQuery("Queues.findByQueueName").setParameter("queueName", queueName);

            list = qry.getResultList();

            // if list not empty, get queue object
            if(list.size()>0)
                queues  = (Queues)list.get(0);
            else
                throw new OpenCaptureException("Invalid queue name[" + queueName + "]!");
            
            return queues.getQueueId();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
        finally
        {
            queues = null;
            list = null;
            
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
//            
            entMgr = null;
        }
    }
    
    /**
     * Get next batch in modules queue.
     * @param moduleID ID of module to retrieve batch for.
     * @return Returns batch id.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public long getNextBatch(String moduleID) throws OpenCaptureException
    {
        Queues queues;
        Batches batches;
        List list;

        try
        {
            entMgr = entMgrFac.createEntityManager();
            
            qry = entMgr.createNamedQuery("Queues.findByQueueName").setParameter("queueName", moduleID);

            list = qry.getResultList();

            // if list not empty, get queue object
            if(list.size()>0)
                queues  = (Queues)list.get(0);
            else
                throw new OpenCaptureException("Invalid moduleID[" + moduleID + "]!");

            // use queueid to get next batch id from db
            qry = entMgr.createNamedQuery("Batches.getNextBatchByQueueId").setParameter("queueId", queues.getQueueId());
            list = qry.getResultList();
            
            // if list isn't empty
            if(list.size()>0)
                batches = (Batches)list.get(0);
            else
                throw new OpenCaptureException("No batches availabe to process.");

            return batches.getBatchId();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to getQueueByModuleID[" + moduleID + "].  " + e.toString());
        }
        finally
        {
            list = null;
            queues = null;
            batches = null;
            
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }

    /**
     * Get batch using id as key.
     * @param batchID Batch id.
     * @return Returns a list of batches.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public List getBatchByID(long batchID) throws OpenCaptureException
    {
        
        try
        {
            Batches batches = new Batches(batchID);
            entMgr = entMgrFac.createEntityManager();

            entMgr.persist(batches);
            qry = entMgr.createNamedQuery("Batches.findByBatchId");

            List list = qry.getResultList();
            
            return list;
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to retrieve batch id.  " + e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
        
    }
    
    public List getBatchList() throws OpenCaptureException
    {
        try
        {
            entMgr = entMgrFac.createEntityManager();

            qry = entMgr.createNamedQuery("Batches.BatchList");

            List list = qry.getResultList();

            
            
            return list;
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to retrieve batch id.  " + e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }

    /**
     * Create a new batch.
     * @param batchId Set this to 0.
     * @param batchName Name of new batch.
     * @param batchClassId Id of batch class the batch is assigned to.
     * @param scanDateTime DateTime batch is created.
     * @param siteId Id of site that is creating batch.
     * @param batchState State of batch when creating (usually OpenCaptureCommon.BATCH_STATE_READY).
     * @param queueId Id of starting queue.
     * @return Return new batch id.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public long createBatch(Long batchId, String batchName, long batchClassId, Date scanDateTime, int siteId, 
                                                                int batchState, long queueId) throws OpenCaptureException
    {
        EntityTransaction tx = null;
        
        try
        {
            tblBatches = new net.filterlogic.OpenCapture.data.Batches();

            entMgr = entMgrFac.createEntityManager();

            tx = entMgr.getTransaction();
            tx.begin();

            tblBatches.setBatchName(batchName);
            tblBatches.setBatchClassId(batchClassId);
            tblBatches.setScanDatetime(scanDateTime);
            tblBatches.setSiteId(siteId);
            tblBatches.setBatchState(batchState);
            tblBatches.setQueueId(queueId);

            entMgr.persist(tblBatches);

            tx.commit();

            return tblBatches.getBatchId();
        }
        catch(Exception e)
        {
            tx.rollback();
            throw new OpenCaptureException(e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }

    /**
     * Get batch row.
     * @return
     */
    public Batches getBatchRow()
    {
        return tblBatches;
    }
    
    /**
     * Create new batch class.
     * @param batchClassID Set this to 0.
     * @param batchClassName Name of new batch class.
     * @param batchDescr Description of batch class.
     * @param imagePath Path when scanned/imported images will be stored.
     * @return Return id of new batch class.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public long createBatchClass(long batchClassID, String batchClassName, String batchDescr, String imagePath) throws OpenCaptureException
    {
        EntityTransaction tx = null;
        
        try
        {
            tblBatchClasses = new BatchClass(batchClassID, batchClassName, batchDescr, imagePath);
            
            entMgr = entMgrFac.createEntityManager();
            
            tx = entMgr.getTransaction();
            tx.begin();
            
            entMgr.persist(tblBatchClasses);
            
            tx.commit();
            //entMgr.flush();
            
            return tblBatchClasses.getBatchClassId();
        }
        catch(Exception e)
        {
            tx.rollback();
            throw new OpenCaptureException(e.toString());
        }
        finally
        {
            if(entMgr.isOpen())
                entMgr.close();
            
//            if(entMgrFac.isOpen())
//                entMgrFac.close();
            
            entMgr = null;
        }
    }
}
