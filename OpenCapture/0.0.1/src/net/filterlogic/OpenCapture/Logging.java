/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author D106931
 */
public class Logging 
{
    private List<Log> logging;

    public Logging()
    {
        logging = new ArrayList<Log>();
    }
    
    public void addLog(Log log)
    {
        this.logging.add(log);
    }
    
    public List getLogs()
    {
        return logging;
    }
}
