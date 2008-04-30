/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.data;

import java.util.ArrayList;
import java.util.List;

import net.filterlogic.util.NamedValueList;
import net.filterlogic.OpenCapture.OpenCaptureCommon;
import net.filterlogic.util.DateUtil;
import net.filterlogic.OpenCapture.data.BatchClass;
import net.filterlogic.OpenCapture.data.Batches;
import net.filterlogic.OpenCapture.data.LkpBatchclassQueues;
import net.filterlogic.OpenCapture.data.LkpBatchclassQueuesPK;
import net.filterlogic.OpenCapture.data.Queues;
import org.omg.CORBA.NameValuePair;
        

/**
 *
 * @author Darron Nesbitt
 */
public class ResultSet
{

    private ArrayList<Object> fieldList;
    private int rowCount = 0;
    
    private List resultSet;
    
    public ResultSet(List resultSet)
    {
        fieldList = new ArrayList<Object>();
        this.resultSet = resultSet;
    }
    
    private void createResultSet()
    {
        for(int i=0;i<resultSet.size();i++)
        {
            Object obj = resultSet.get(i);


            if(obj.getClass().equals("net.filterlogic.OpenCapture.data.BatchClass"))
                createResultSet((BatchClass)obj);

            if(obj.getClass().equals("net.filterlogic.OpenCapture.data.Batches"))
                createResultSet((Batches)obj);

            if(obj.getClass().equals("net.filterlogic.OpenCapture.data.LkpBatchclassQueues"))
                createResultSet((LkpBatchclassQueues)obj);

            if(obj.getClass().equals("net.filterlogic.OpenCapture.data.LkpBatchclassQueuesPK"))
                createResultSet((LkpBatchclassQueuesPK)obj);

            if(obj.getClass().equals("net.filterlogic.OpenCapture.data.Queues"))
                createResultSet((Queues)obj);
        }
        
    }

    private void createResultSet(BatchClass batchClass)
    {
    }

    private void createResultSet(Batches batches)
    {
        NamedValueList<String,String> map = new NamedValueList<String, String>();

        map.put("Batch_Id", batches.getBatchId().toString());
        map.put("Batch_Name", batches.getBatchName());
        map.put("Batch_Desc", batches.getBatchDesc());
        map.put("Batch_State", String.valueOf(batches.getBatchState()));
        map.put("Priority", batches.getPriority().toString());
        map.put("Queue_Id", String.valueOf(batches.getQueueId()));
        map.put("Scan_DateTime", DateUtil.getDateTime(OpenCaptureCommon.DATE_FORMAT, batches.getScanDatetime()));
        map.put("Site_Id", String.valueOf(batches.getSiteId()));
        map.put("Error_No", batches.getErrorNo().toString());
        map.put("Error_Msg", batches.getErrorMsg());
        map.put("Batch_Class_Id", String.valueOf(batches.getBatchClassId()));

        this.resultSet.add(map);
    }
    
    private void createResultSet(LkpBatchclassQueues lkpBatchclassQueues)
    {
    }
    
    private void createResultSet(LkpBatchclassQueuesPK lkpBatchclassQueuesPK)
    {
    }
    
    private void createResultSet(Queues queues)
    {
    }
  
    public List getFieldList()
    {
        return this.fieldList;
    }
}
