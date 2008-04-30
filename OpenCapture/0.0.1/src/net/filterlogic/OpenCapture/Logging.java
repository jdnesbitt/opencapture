/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
public class Logging 
{
    private List<Log> logging;

    public Logging()
    {
        logging = new ArrayList<Log>();
    }
    
    /**
     * Add new log to logging collection.
     * @param log Log to add.
     */
    public void addLog(Log log)
    {
        this.logging.add(log);
    }
    
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
