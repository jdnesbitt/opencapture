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

import java.util.Hashtable;
import java.util.HashMap;
import java.util.List;
import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.NamedValueList;

/**
 *
 * @author dnesbitt
 */
public class Documents 
{
    //private Hashtable <String,Document> documents = new Hashtable<String,Document>();
    private NamedValueList<String,Document> documents = new NamedValueList<String,Document>();
    
    public Documents(XMLParser batch) throws OpenCaptureException
    {
        try
        {
            // get document elements back
            List docs = batch.getNodeList(OpenCaptureCommon.DOCUMENTS);
            
            // loop through documents and create document obj.
            for(int i=0;i<docs.size();i++)
            {
                HashMap map = (HashMap)docs.get(i);
                String documentName = (String)map.get(OpenCaptureCommon.OC_NAME_TAG);
                String formID = (String)map.get(OpenCaptureCommon.OC_DOCUMENT_FORMID_TAG);
                
                Document document = new Document(batch, documentName, formID);
                
                // add document obj to documents hash
                documents.put(documentName, document);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }
    
    /**
     * Get the named document.
     * @param documentName Name of the document.
     * @return Document object.
     */
    public Document getDocument(String documentName)
    {
        Document document = (Document)documents.get(documentName);
        
        return document;
    }

    /**
     * Get count of documents.
     * @return Count of documents.
     */
    public int Count()
    {
        return documents.size();
    }

    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";
        
        List list = documents.getOrderedNameList();
        
        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            Document document = (Document)documents.get(name);

            xml += "<Document Name=\"" + document.getName() + "\" FormID=\"" + document.getFormID() + "\">\n";
            xml += "<IndexDataFields>\n";
            xml += document.getIndexDataFields().getXML();
            xml += "</IndexDataFields>";
            xml += "<Pages>\n";
            xml += document.getPages().getXML();
            xml += "</Pages>\n";
            xml += "<IndexFields>\n";
            xml += document.getIndexFields().getXML();
            xml += "</IndexFields>\n";
            xml += "<Zones>\n";
            xml += document.getZones().getXML();
            xml += "</Zones>\n";
            xml += "</Document>";
        }
        
        return xml;
    }
}
