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
import net.filterlogic.OpenCapture.OpenCaptureCommon;

/**
 * DBUtils class contains helper methods for working with persistence objects.
 * 
 * @author Darron Nesbitt
 */
public class DBUtils 
{
    public static String BatchesListToXML(List list)
    {
        String xml = "<BatchList>\n";
        
        // build xml string to return to caller
        for(int i=0;i<list.size();i++)
        {
            Batches batches = (Batches)list.get(i);
            
            xml += "<Batch " +
                    "BatchID=\"" + OpenCaptureCommon.getStringValue(batches.getBatchId()) + "\" " +
                    "Name=\"" + OpenCaptureCommon.getStringValue(batches.getBatchName()) + "\" " +
                    "Description=\"" + OpenCaptureCommon.getStringValue(batches.getBatchDesc()) + "\" " +
                    "BatchClassID=\"" + OpenCaptureCommon.getStringValue(batches.getBatchClassId()) + "\" " +
                    "BatchState=\"" + OpenCaptureCommon.getStringValue(batches.getBatchState()) + "\" " +
                    "Priority=\"" + OpenCaptureCommon.getStringValue(batches.getPriority()) + "\" " +
                    "QueueID=\"" + OpenCaptureCommon.getStringValue(batches.getQueueId()) + "\" " +
                    "ScanDateTime=\"" + net.filterlogic.util.DateUtil.getDateTime("yyyy/MM/dd HH:mm:ss", batches.getScanDatetime()) + "\" " +
                    "SiteID=\"" + OpenCaptureCommon.getStringValue(batches.getSiteId()) + "\" " +
                    "ErrorNumber=\"" + OpenCaptureCommon.getStringValue(batches.getErrorNo()) + "\" " +
                    "ErrorMessage=\"" + OpenCaptureCommon.getStringValue(batches.getErrorMsg()) + "\" " +
                    " />\n";
        }
        
        xml += "</BatchList>\n";

        return xml;
    }

    /**
     * QueueListToXML returns XML of Queue object.
     * 
     * @param list List containing queues.
     * 
     * @return XML containing String.
     */
    public static String QueueListToXML(List list)
    {
        String xml = "<QueueList>\n";
        
        // build xml string to return to caller
        for(int i=0;i<list.size();i++)
        {
            Queues queues = (Queues)list.get(i);
            
            xml += "<Queue " +
                    "QueueID=\"" + OpenCaptureCommon.getStringValue(queues.getQueueId()) + "\" " +
                    "Name=\"" + OpenCaptureCommon.getStringValue(queues.getQueueName()) + "\" " +
                    "Description=\"" + OpenCaptureCommon.getStringValue(queues.getQueueDesc()) + "\" " +
                    "Plugin=\"" + OpenCaptureCommon.getStringValue(queues.getPlugin()) + "\" " +
                    " />\n";
        }

        xml += "</QueueList>\n";

        return xml;
    }

    /**
     * BatchClassListToXML converts batch class list to xml.
     * 
     * @param list List containing  BatchClass data objects.
     * 
     * @return String containing XML.
     */
    public static String BatchClassListToXML(List list)
    {
        String xml = "<BatchClassList>\n";

        // build xml string to return to caller
        for(int i=0;i<list.size();i++)
        {
            BatchClass batchclass = (BatchClass)list.get(i);

            xml += "<BatchClass " +
                    "BatchClassID=\"" + OpenCaptureCommon.getStringValue(batchclass.getBatchClassId()) + "\" " +
                    "Name=\"" + OpenCaptureCommon.getStringValue(batchclass.getBatchClassName()) + "\" " +
                    "Description=\"" + OpenCaptureCommon.getStringValue(batchclass.getDescr()) + "\" " +
                    "ImagePath=\"" + OpenCaptureCommon.getStringValue(batchclass.getImagePath()) + "\" " +
                    " />\n";
        }

        xml += "</BatchClassList>\n";

        return xml;
    }
}
