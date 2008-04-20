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

import net.filterlogic.util.xml.XMLParser;
import java.util.List;
import java.util.HashMap;

/**
 *
 * @author dnesbitt
 */
public class Zones 
{
    private HashMap <String,Zone> zones = new HashMap<String,Zone>();
    
    public Zones(XMLParser batch,String xPath) throws OpenCaptureException
    {
        try
        {
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String zoneName = (String)map.get("Name");
                String type = map.get("Type") != null ? (String)map.get("Type") : "";
                String x = ((String)map.get("X")).trim().length()>0 ? (String)map.get("X") : "0";
                String y = ((String)map.get("Y")).trim().length()>0 ? (String)map.get("Y") : "0";;
                String h = ((String)map.get("H")).trim().length()>0 ? (String)map.get("H") : "0";;
                String w = ((String)map.get("W")).trim().length()>0 ? (String)map.get("W") : "0";;
                String min = ((String)map.get("MinAccuracy")).trim().length()>0 ? (String)map.get("MinAccuracy") : "0";;
                String ft = (String)map.get("FieldType");

                // create and fill ndx field object
                Zone zone = new Zone(zoneName,type,Integer.parseInt(x),Integer.parseInt(y),Integer.parseInt(h),Integer.parseInt(w),Byte.parseByte(min),ft);

                // add ndx field to hash
                zones.put(zoneName, zone);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }   
    }
    
    public Zone getZone(String zoneName)
    {
        Zone zone = (Zone)zones.get(zoneName);
        
        if(zone == null)
            zone = new Zone();
        
        return zone;
    }
    
    public void setZone(Zone zone) throws OpenCaptureException
    {
        String zoneName = zone.getName();

        if(zoneName.trim().length()<1)
            throw new OpenCaptureException("Zone name not set!");

        zones.put(zoneName, zone);
    }
}
