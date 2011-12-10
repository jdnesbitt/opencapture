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

import java.util.ArrayList;
import java.util.List;
import net.filterlogic.OpenCapture.data.DBManager;

/**
 *
 * @author dnesbitt
 */
public class Batches 
{
    private DBManager dbm;
    private List<net.filterlogic.OpenCapture.data.Batches> batches;
    private long batchClassID;

    private Logging logging = null;

    public Batches()
    {
        logging = new Logging();
    }

    public List<String> getBatchNameList() throws OpenCaptureException
    {
        List<String> batchNames = new ArrayList<String>();

        try
        {
            // create dbmgr obj
            dbm = new DBManager();

            // query dbmgr for list of batches
            batches = dbm.getBatchList();

            // loop through batch list.
            for(int i=0;i<batches.size();i++)
            {
                // get batches obj from query result list
                net.filterlogic.OpenCapture.data.Batches batch = batches.get(i);

                // add batch name to name list
                batchNames.add(batch.getBatchName());
            }
        }
        catch(OpenCaptureException oce)
        {
            oce.printStackTrace();

            throw oce;
        }

        return batchNames;
    }

    /**
     * Get list of persistence Batches objects.
     *
     * @return net.filterlogic.OpenCapture.data.Batches
     * @throws OpenCaptureException
     */
    public List<net.filterlogic.OpenCapture.data.Batches> getRawBatchList() throws OpenCaptureException
    {
        try
        {
            // create dbmgr obj
            dbm = new DBManager();

            // query dbmgr for list of batches
            batches = dbm.getBatchList();
        }
        catch(OpenCaptureException oce)
        {
            oce.printStackTrace();

            throw oce;
        }

        return batches;
    }

    /**
     * Get batch list by batch class and queue id's
     * @param batchClassId
     * @param queueId
     * @return
     * @throws OpenCaptureException
     */
    public List<net.filterlogic.OpenCapture.data.Batches> getRawBatchList(long batchClassId, long queueId) throws OpenCaptureException
    {
        try
        {
            // create dbmgr obj
            dbm = new DBManager();

            // query dbmgr for list of batches
            batches = dbm.getBatchListByBatchClassAndQueueId(batchClassId, queueId);
        }
        catch(OpenCaptureException oce)
        {
            oce.printStackTrace();

            throw oce;
        }

        return batches;
    }
}
