/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 *
 * @author Darron Nesbitt
 */
public class TimeWarp 
{
    private String s_CurrentDate = "";
    private String s_CurrentDateFormat = "";
    private String s_CurrentTZ = "";
    private String s_NewDate = "";
    private String s_NewDateFormat = "";
    private String s_NewTZ = "";
    private Date d_CurrentDate;
    private Date d_NewDate;

    private static SimpleTimeZone UTC_TZ = new SimpleTimeZone(0, "UTC");

    /**
     * Default constructor.
     */
    public TimeWarp()
    {
    }

    /**
     * Contructor for TimeWarp
     * 
     * @param currentDate
     * @param currentDateFormat
     * @param currentTZ
     * @param newDateFormat
     * @param newTZ
     */
    public TimeWarp(String currentDate, String currentDateFormat, String currentTZ, String newDateFormat, String newTZ)
    {
        this.s_CurrentDate = currentDate;
        this.s_CurrentDateFormat = currentDateFormat;
        this.s_CurrentTZ = currentTZ;
        this.s_NewDateFormat = newDateFormat;
        this.s_NewTZ = newTZ;
    }

    private boolean validateSettings()
    {
        boolean result = false;
        
        if(s_CurrentDate.length()>0 && s_CurrentDateFormat.length()>0 && s_CurrentTZ.length()>0)
        {
            result = true;
        }
        
        return result;
    }

    /**
     * Execute time warp.
     * 
     * @throws java.lang.Exception
     */
    public void warp() throws Exception
    {
        try
        {
            // set time zone to specified
            //SimpleTimeZone simpletimezone = (SimpleTimeZone)TimeZone.getTimeZone(s_CurrentTZ);
            TimeZone simpletimezone = TimeZone.getTimeZone(s_CurrentTZ);

            // create simple date formatter with current date's format 
            SimpleDateFormat simpledateformat = new SimpleDateFormat(s_CurrentDateFormat);
            // set time zone for formatter.
            simpledateformat.setTimeZone(simpletimezone);

            // convert current date to date object.
            d_CurrentDate = simpledateformat.parse(s_CurrentDate);

            // convert current date to UTC
            simpledateformat.setTimeZone(UTC_TZ);
            
            // convert current utc date to specified format.
            s_NewDate = simpledateformat.format(d_CurrentDate);

            // make sure new date format specified
            if(s_NewDateFormat.length()>0)
            {
                // get time zone obj for new time zone specified.
                TimeZone newtimezone = TimeZone.getTimeZone(s_NewTZ);

                // convert utc date to new format
                SimpleDateFormat formatter = new SimpleDateFormat (s_NewDateFormat);
                formatter.setTimeZone(newtimezone);

                // format string date
                s_NewDate = formatter.format(d_CurrentDate);
            }

            //d_NewDate = DateFormat.getInstance().parse(s_NewDate);
        }
        catch(Exception e)
        {
            throw new Exception("Error converting timezone: " + e.toString());
        }
    }

    public String getCurrentDate() 
    {
        return s_CurrentDate;
    }

    public String getCurrentDateFormat() 
    {
        return s_CurrentDateFormat;
    }

    public String getCurrentTZ() 
    {
        return s_CurrentTZ;
    }

    /**
     * Get new date.  Call this method after a successfull
     * call to the warp method.
     *
     * @return String containing newly formatted and in specified time zone.
     */
    public String getNewDate() 
    {
        return s_NewDate;
    }

    public String getNewDateFormat() 
    {
        return s_NewDateFormat;
    }

    public String getNewDateTZ() 
    {
        return s_NewTZ;
    }

    public void setNewDateTZ(String s_NewTZ) 
    {
        this.s_NewTZ = s_NewTZ;
    }

    public static void main(String[] args)
    {
        String[] tz = java.util.TimeZone.getAvailableIDs();

        for(int i=0;i<tz.length;i++)
            System.out.println(i + "=" + tz[i]);
        
        TimeWarp tw = new TimeWarp("2008-07-05 15:00:00", "yyyy-MM-dd HH:mm:ss", "America/Los_Angeles", "MM/dd/yyyy HH:mm:ss", "America/Chicago");
        
        try
        {
            tw.warp();

            System.out.print(tw.getNewDate());
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
    }
}
