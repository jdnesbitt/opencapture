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

import java.lang.reflect.Method;
import java.util.Date;
import java.lang.*;
import java.lang.reflect.*;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import net.filterlogic.OpenCapture.OpenCaptureException;

import java.util.List;
import java.util.HashMap;
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
    
    public DBManager()
    {
        
    }
    
    public long getNextBatch(String moduleID) throws OpenCaptureException
    {
        Queues queues;
        Batches batches;
        List list;
        HashMap map;

        try
        {
            entMgr = entMgrFac.createEntityManager();
            
//            Queues queues = new Queues();
//            queues.setQueueName(moduleID);

            //entMgr.persist(queues);
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
    }

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
    }
    
    public long createBatch(Long batchId, String batchName, long batchClassId, Date scanDateTime, int siteId, 
                                                                int batchState, long queueId) throws OpenCaptureException
    {
        try
        {
            tblBatches = new net.filterlogic.OpenCapture.data.Batches();

            entMgr = entMgrFac.createEntityManager();

            entMgr.getTransaction().begin();

            tblBatches.setBatchName(batchName);
            tblBatches.setBatchClassId(batchClassId);
            tblBatches.setScanDatetime(scanDateTime);
            tblBatches.setSiteId(siteId);
            tblBatches.setBatchState(batchState);
            tblBatches.setQueueId(queueId);

            entMgr.persist(tblBatches);

            entMgr.getTransaction().commit();

            return tblBatches.getBatchId();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    public Batches getBatchRow()
    {
        return tblBatches;
    }
    
    public long createBatchClass(long batchClassID, String batchClassName, String batchDescr, String imagePath) throws OpenCaptureException
    {
        
        try
        {
            tblBatchClasses = new BatchClass(batchClassID, batchClassName, batchDescr, imagePath);
            
            entMgr = entMgrFac.createEntityManager();
            
            entMgr.getTransaction().begin();
            entMgr.persist(tblBatchClasses);
            entMgr.getTransaction().commit();
            //entMgr.flush();
            
            return tblBatchClasses.getBatchClassId();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
}
