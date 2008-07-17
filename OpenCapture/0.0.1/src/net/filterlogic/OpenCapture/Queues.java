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
import java.util.HashMap;
import net.filterlogic.util.xml.XMLParser;
import java.util.List;

/**
 * Queues object is used to 
 * @author dnesbitt
 */
public class Queues 
{
    private String currentQueue = "";
    private List queues;

    /**
     * Coinstructor to add queue.
     * @param queue
     */
    public Queues(Queue queue)
    {
        if(queues == null)
            queues = new ArrayList();

        queues.add(queue);
    }
    
    public Queues(XMLParser batch) throws OpenCaptureException
    {
        try
        {
            queues = new ArrayList();

            List list = batch.getNodeList(OpenCaptureCommon.QUEUES);

            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                Queue queue = new Queue();
                
                queue.setQueueName((String)map.get("Name"));
                queue.setID(Long.parseLong((String)map.get("ID")));
                queue.setPluginID((String)map.get("CustomPlugin"));
                
                queues.add(queue);
            }
            
            currentQueue = batch.getValue(OpenCaptureCommon.CURRENT_QUEUE).trim();

            if(currentQueue.trim().length()<1)
            {
                // set current q to first entry in map
                currentQueue = ((Queue)queues.get(0)).getQueueName();
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    /**
     * moveNextQueue moves the batch to the next queue in the flow.
     */
    public void moveNextQueue() throws OpenCaptureException
    {
        // clear out queue name.
        //this.currentQueue = "";

        try
        {
            for(int i=0;i<queues.size();i++)
            {
                String qName = ((Queue)queues.get(i)).getQueueName().toLowerCase();

                if(qName.equals(this.currentQueue.toLowerCase()))
                {
                    // if i < number of queues
                    if(i < queues.size() -1)
                    {
                        // inc i
                        ++i;

                        // set current q = to next q.
                        this.setCurrentQueue(((Queue) queues.get(i)).getQueueName().toLowerCase());

                        // leave for loop
                        break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
    
    /**
     * Get specified queue object.
     * @param name Name of queue to return,
     * @return Queue object.  If name not found and empty queue 
     * object is returned.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public Queue getQueue(String name) throws OpenCaptureException
    {
        Queue queue = null;
        HashMap map = null;
        
        try
        {
            for(int i=0;i<queues.size();i++)
            {
                String qName = ((Queue)queues.get(i)).getQueueName().toLowerCase();

                if(qName.equals(name.toLowerCase()))
                {
                        // set current q = to next q.
                        queue = (Queue)queues.get(i);

                        // leave for loop
                        break;
                }
            }
            
            if(queue == null)
                queue = new Queue();
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }

        return queue;

    }
    
    /**
     * Get current queue object.
     * @return Current queue object.  If not found, exception thrown.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public net.filterlogic.OpenCapture.Queue getCurrentQueue() throws OpenCaptureException
    {
        net.filterlogic.OpenCapture.Queue queue = null;
        HashMap map = null;
        
        try
        {
            for(int i=0;i<queues.size();i++)
            {
                queue = (Queue)queues.get(i);
                String qName = queue.getQueueName().toLowerCase();

                if(qName.equals(this.currentQueue.toLowerCase()))
                {
                        // set current q = to next q.
                        //queue = (net.filterlogic.OpenCapture.Queue)queues.get(i);

                        // leave for loop
                        break;
                }
            }
            
            if(queue == null)
                throw new Exception("Cannot find current queue[" + this.currentQueue + "]");
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }

        return queue;
    }
    
    /**
     * MapToQueue copies map data to Queue object.
     * @param map 
     * @return Queue object containing mapped data.
     */
    private Queue MapToQueue(HashMap map)
    {
        Queue queue = new Queue();

        queue.setQueueName((String)map.get("Name"));
        queue.setPluginID((String)map.get("CustomPlugin"));
        queue.setID(Long.parseLong((String)map.get("ID")));

        return queue;
    }

    /**
     * Count of queues in OpenCapture flow.
     * @return Integer
     */
    public int Count()
    {
        return queues.size();
    }
    
    /**
     * Add queue to OpenCapture flow.
     * @param queue Queue to add.
     */
    public void addQueue(Queue queue)
    {
        queues.add(queue);
    }

    /**
     * Delete queue.
     * @param queueName Name of queue to delete.
     * @param queueIndex Index of queue to delete (queues can be specified multiple times).
     */
    public void deleteQueue(String queueName, int queueIndex)
    {
        Queue q = (Queue)queues.get(queueIndex);
        
        if(q.getQueueName().equals(queueName))
            queues.remove(queueIndex);

        q = null;
    }
    
    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";

        for(int i=0;i<queues.size();i++)
        {
            Queue queue = (Queue)queues.get(i);

            xml += "<Queue Name=\"" + queue.getQueueName() + "\" ID=\"" + queue.getID() + 
                    "\" CustomPlugin=\"" + queue.getPluginID() + "\" />\n";
        }

        return xml;
    }

    protected void setCurrentQueue(String currentQueue)
    {
        this.currentQueue = currentQueue;
    }
}
