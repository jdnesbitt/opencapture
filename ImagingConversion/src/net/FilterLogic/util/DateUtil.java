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

package net.FilterLogic.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author dnesbitt
*/
public class DateUtil 
{

    /**
     * Returns the current date and or time in the specified format.
     * @param format Format of date to be returned.
     * @return String containing current date.
     */
    public static String getDateTime(String format)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Returns date/time formatted.
     * @param format DateTime format to return.
     * @param date Date to format.
     * @return Return String of formatted date.
     */
    public static String getDateTime(String format, Date date)
    {
        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
    
    
    /**
     * Returns the curent date time in yyyy/MM/dd HH:mm:ss format.
     * @return String containg current date and time.
     */
    public static String getDateTime() 
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    } 
    
    /**
     * Convert string date to Date object.
     * @param date String date.
     * @param dateFormat Format of date parameter.
     * @return Date object.
     * @throws java.text.ParseException
     */
    public static Date toDate(String date, String dateFormat) throws java.text.ParseException
    {
        return new SimpleDateFormat(dateFormat).parse(date);
    }
}
