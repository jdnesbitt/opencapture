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
import net.filterlogic.util.NamedValueList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author dnesbitt
 */
public class BatchFields 
{
    private NamedValueList <String,BatchField> batchFields = new NamedValueList<String,BatchField>();

    public BatchFields(XMLParser batch,String xPath) throws OpenCaptureException
    {
        try
        {
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String batchFieldName = (String)map.get("Name");
                String type = map.get("Type") != null ? (String)map.get("Type") : "";
                String value = (String)map.get("Value");

                // create and fill ndx field object
                BatchField batchField = new BatchField(batchFieldName,type,value);

                // add ndx field to hash
                batchFields.put(batchFieldName, batchField);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }

    }

    public BatchField getBatchField(String batchFieldName)
    {
        BatchField batchField = (BatchField)batchFields.get(batchFieldName);
        
        if(batchField == null)
            batchField = new BatchField();
        
        return batchField;
    }

    public void setBatchField(BatchField batchField) throws OpenCaptureException
    {
        String batchFieldName = batchField.getName();

        if(batchFieldName.trim().length()<1)
            throw new OpenCaptureException("BatchField name not set!");

        batchFields.put(batchFieldName, batchField);
    }

    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";

        List list = batchFields.getOrderedNameList();

        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            BatchField batchField = (BatchField)batchFields.get(name);

            xml += "<BatchField Name=\"" + batchField.getName() + "\" Type=\"" + batchField.getType() + 
                    "\" Value=\"" + batchField.getValue() + "\" />\n";
        }

        return xml;
    }

    public int Count()
    {
        return batchFields.size();
    }
}
