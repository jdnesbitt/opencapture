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
import java.util.HashMap;

import net.filterlogic.util.xml.XMLParser;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
public class Logging 
{
    private List<Log> logging;

    /**
     * Default Logging constructor.
     */
    public Logging()
    {
        logging = new ArrayList<Log>();
    }
    
    /**
     * Logging constructor used to parse batch xml when instantiated.
     * @param batch XMLParser object containing batch xml.
     * @param xPath XPath to query.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public Logging(XMLParser batch, String xPath) throws OpenCaptureException
    {
        try
        {
            if(logging == null)
                logging = new ArrayList<Log>();

            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String queueName = (String)map.get("QueueName");
                String startDateTime = (String)map.get("StartDateTime");
                String endDateTime = (String)map.get("EndDateTime");
                String host = (String)map.get("Host");
                String message = (String)map.get("Message");

                // create and fill log object
                logging.add(new Log(queueName, startDateTime, endDateTime, host, message));
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Logging constructor w/ parsing: " + e.toString());
        } 
    }
    
    /**
     * Add new log to logging collection.
     * @param log Log to add.
     */
    public void addLog(Log log)
    {
        this.logging.add(log);
    }
    
    /**
     * Return a list of logs.
     * @return List containing log objects.
     */
    public List getLogs()
    {
        return logging;
    }
    
    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";

        for(int i=0;i<logging.size();i++)
        {
            Log log = (Log)logging.get(i);

            xml += "<Log QueueName=\"" + log.getQueueName() + "\" StartDateTime=\"" + log.getStartDateTime() + 
                    "\" EndDateTime=\"" + log.getEndDateTime() + 
                    "\" Host=\"" + log.getHost() + "\" Message=\"" + log.getMessage() + "\" />\n";
        }

        return xml;
    }
}
