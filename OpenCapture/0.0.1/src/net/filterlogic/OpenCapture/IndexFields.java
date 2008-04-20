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
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author dnesbitt
 */
public class IndexFields 
{
    private HashMap <String,IndexField> indexFields = new HashMap<String,IndexField>();

    /**
     * Default constructor.
     */
    public IndexFields()
    {
    }
    
    /**
     * Constructor 
     * @param batch XMLParser object.
     * @param documentName
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public IndexFields(XMLParser batch,String xPath) throws OpenCaptureException
    {
        try
        {
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String ndxName = (String)map.get("Name");
                String type = map.get("Type") != null ? (String)map.get("Type") : "";
                String value = (String)map.get("Value");
                String s = ((String)map.get("Stickey")).toUpperCase();
                
                boolean stickey = s.toUpperCase().equals("N") || s.length()<1 ? false : true;
                
                // create and fill ndx field object
                IndexField ndxField = new IndexField(ndxName,type,value,stickey);

                // add ndx field to hash
                indexFields.put(ndxName, ndxField);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
    
    public IndexField getIndexField(String indexFieldName)
    {
        IndexField ndxField = (IndexField)indexFields.get(indexFieldName);
        
        if(ndxField == null)
            ndxField = new IndexField();
        
        return ndxField;
    }
    
    public void setIndexField(IndexField indexField) throws OpenCaptureException
    {
        String indexFieldName = indexField.getName();

        if(indexFieldName.trim().length()<1)
            throw new OpenCaptureException("IndexField name not set!");

        indexFields.put(indexFieldName, indexField);
    }
    
    public int Count()
    {
        return indexFields.size();
    }
}
