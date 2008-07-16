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
    //private Hashtable <String,Configuration> documents = new Hashtable<String,Configuration>();
    private NamedValueList<String,Configuration> documents = new NamedValueList<String,Configuration>();
    
    /**
     * Constructor to add new document.
     * @param document
     */
    public Documents(Document document)
    {
        documents.put(String.valueOf(document.getNumber()), document);
    }
    
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
                int number = ((String)map.get(OpenCaptureCommon.OC_DOCUMENT_NUMBER_TAG)).trim().length()>0 
                        ? Integer.parseInt((String)map.get(OpenCaptureCommon.OC_DOCUMENT_NUMBER_TAG)) : 0;
                
                Configuration document = new Configuration(batch, documentName, formID,number);
                
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
     * @return Configuration object.
     */
    public Document getDocument(String documentNumber)
    {
        Document document = (Document)getDocuments().get(documentNumber);
        
        return document;
    }

    /**
     * Get count of documents.
     * @return Count of documents.
     */
    public int Count()
    {
        return getDocuments().size();
    }

    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";
        
        List list = getDocuments().getOrderedNameList();
        
        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            Document document = (Document)getDocuments().get(name);

            xml += "<Document Name=\"" + document.getName() + "\" FormID=\"" + document.getFormID() + "\" Number=\"" + String.valueOf(document.getNumber()) + "\">\n";
            xml += "<Pages>\n";
            xml += document.getPages().getXML();
            xml += "</Pages>\n";
            xml += "<IndexFields>\n";
            xml += document.getIndexFields().getXML();
            xml += "</IndexFields>\n";
            xml += "</Document>";
        }
        
        return xml;
    }

    public NamedValueList<String, Configuration> getDocuments() {
        return documents;
    }

    public void setDocuments(NamedValueList<String, Configuration> documents) {
        this.documents = documents;
    }
    
    /**
     * Add document to document collection.
     * @param document Configuration to add.
     */
    public void addDocument(Document document)
    {
        documents.put(String.valueOf(document.getNumber()), document);
    }
    
    /**
     * Delete document from documents collection.
     * @param documentName Name of document to delete.
     * @return Delete document.  Returns an empty document if documentName doesn't exist.
     */
    public Document deleteDocument(String documentNumber)
    {
        return documents.containsKey(documentNumber) ? (Document)documents.remove(documentNumber) : new Document();
    }
}
