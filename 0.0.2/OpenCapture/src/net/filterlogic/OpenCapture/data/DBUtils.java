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


package net.filterlogic.OpenCapture.data;

import java.util.List;

/**
 * DBUtils class contains helper methods for working with persistence objects.
 * 
 * @author Darron Nesbitt
 */
public class DBUtils 
{
    public static String BatchesListToXML(List list)
    {
        String xml = "<BatchList>";
        
        // build xml string to return to caller
        for(int i=0;i<list.size();i++)
        {
            Batches batches = (Batches)list.get(i);
            
            xml += "<Batch " +
                    "BatchID=\"" + batches.getBatchId() + "\" " +
                    "Name=\"" + batches.getBatchName() + "\" " +
                    "Description=\"" + batches.getBatchDesc() + "\" " +
                    "BatchClassID=\"" + batches.getBatchClassId() + "\" " +
                    "BatchState=\"" + batches.getBatchState() + "\" " +
                    "Priority=\"" + batches.getPriority().toString() + "\" " +
                    "QueueID=\"" + String.valueOf(batches.getQueueId()) + "\" " +
                    "ScanDateTime=\"" + batches.getScanDatetime() + "\" " +
                    "SiteID=\"" + String.valueOf(batches.getSiteId()) + "\" " +
                    "ErrorNumber=\"" + batches.getErrorNo().toString() + "\" " +
                    "ErrorMessage=\"" + batches.getErrorMsg() + "\" " +
                    " />";
        }
        
        xml += "</BatchList>";

        return xml;
    }
}
