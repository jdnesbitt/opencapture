/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String queueName = (String)map.get("QueueName");
                String startDateTime = (String)map.get("StartDateTime=");
                String endDateTime = (String)map.get("EndDateTime=");
                String host = (String)map.get("Host");
                String message = (String)map.get("Message");

                // create and fill ndx field object
                logging.add(new Log(queueName, startDateTime, endDateTime, host, message));
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
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
