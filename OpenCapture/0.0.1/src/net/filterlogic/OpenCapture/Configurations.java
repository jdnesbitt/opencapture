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
import java.util.ArrayList;
import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.NamedValueList;

/**
 *
 * @author dnesbitt
 */
public class Configurations 
{
    //private Hashtable <String,Document> documents = new Hashtable<String,Document>();
    private NamedValueList<String,Configuration> documents = new NamedValueList<String,Configuration>();
    private Queues queues = null;
    private BatchFields  batchFields = null;
    private IndexFields indexFields = null;
    private CustomProperties customProperties = null;

    /**
     * Constructor to add new document.
     * @param document
     */
    public Configurations(Document document)
    {
        documents.put(document.getName(), document);
    }
    
    public Configurations(XMLParser batch) throws OpenCaptureException
    {
        try
        {
            // get batch fields
            String xPath = OpenCaptureCommon.CONF_BATCH_FIELDS;
            setBatchFields(new BatchFields(batch, xPath));

            xPath = OpenCaptureCommon.CONF_INDEX_FIELDS;

            // get index fields for this document.
            setIndexFields(new IndexFields(batch, xPath));

            // get custom config properties
            xPath = OpenCaptureCommon.CUSTOM_CONFIGURATION_PROPERTIES;
            customProperties = new CustomProperties(batch, xPath);

            // set queues
            setQueues(new Queues(batch));

            // get document elements back
            List docs = batch.getNodeList(OpenCaptureCommon.CONF_DOCUMENTS);
            
            // loop through config documents and create document obj.
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
    
    public Queues getQueues() {
        return queues;
    }

    public void setQueues(Queues queues) {
        this.queues = queues;
    }

    public void addQueues(Queue queue) 
    {
        if(queues == null)
            queues = new Queues(queue);
        else
            this.queues.addQueue(queue);
    }
    
    public BatchFields getBatchFields() {
        return batchFields;
    }

    public void setBatchFields(BatchFields batchFields) {
        this.batchFields = batchFields;
    }
    
    /**
     * Get the named document.
     * @param documentName Name of the document.
     * @return Document object.
     */
    public Configuration getDocument(String documentName)
    {
        Configuration document = (Configuration)getDocuments().get(documentName);
        
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
    public String getXML() throws OpenCaptureException
    {
        String xml = "<Configuration>\n";

        xml += "<CustomProperties>\n";
        xml += getCustomProperties().getXML();
        xml += "</CustomProperties>";

        // get config index fields
        xml += "<IndexFields>\n";
        xml += getIndexFields().getXML();
        xml += "</IndexFields>\n";

        // add queues
        xml += "<Queues CurrentQueue=\"" + this.getQueues().getCurrentQueue().getQueueName() + "\">\n";
        xml += queues.getXML() + "\n";
        xml += "</Queues>\n";
        
        // add batch fields
        xml += "<BatchFields>\n";
        xml += batchFields.getXML();
        xml += "</BatchFields>\n";

        List list = getDocuments().getOrderedNameList();

        xml += "<Documents>\n";

        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            Configuration document = (Configuration)getDocuments().get(name);

            xml += "<Document Name=\"" + document.getName() + "\" FormID=\"" + document.getFormID() + "\" Number=\"" + String.valueOf(document.getNumber()) + "\">\n";
            xml += "<Zones>\n";
            xml += document.getZones().getXML();
            xml += "</Zones>\n";
            xml += "</Document>\n";
        }

        xml += "</Documents>\n";
        xml += "</Configuration>\n";

        return xml;
    }

    /**
     * Get the names of all configured documents.
     * @return Returns a list of document names.
     */
    public List getDocumentNames()
    {
        return documents.getOrderedNameList();
    }
    
    public NamedValueList<String, Configuration> getDocuments() {
        return documents;
    }

    public void setDocuments(NamedValueList<String, Configuration> documents) {
        this.documents = documents;
    }
    
    /**
     * Add document to document collection.
     * @param document Document to add.
     */
    public void addDocument(Configuration document)
    {
        documents.put(document.getName(), document);
    }
    
    /**
     * Delete document from documents collection.
     * @param documentName Name of document to delete.
     * @return Delete document.  Returns an empty document if documentName doesn't exist.
     */
    public Document deleteDocument(String documentName)
    {
        return documents.containsKey(documentName) ? (Document)documents.remove(documentName) : new Document();
    }
    
    /**
     * Get a list of form id zone objects.
     * @return List containing zone objects.
     */
    public List getFormIDZones()
    {
        List documentNames = getDocumentNames();
        List<Zone> zones = null;
        
        for(int i=0;i<documentNames.size();i++)
        {
            String documentName = (String)documentNames.get(i);
            
            // get config object.  
            // calling it document is confusing but Configuration object is
            // a document object renamed with a few changes.
            Configuration document = getDocument(documentName);
            
            // get zone object.
            Zone zone = document.getZones().getZone("FORMID");

            // value reader will search for.  if reader find this value
            // new document should be created.
            zone.setFormIDValue(document.getFormID());
            // set document name in zone object
            zone.setDocumentName(document.getName());

            // create list if it doesn't exist
            if(zones == null)
                zones = new ArrayList<Zone>();

            zones.add(zone);
        }

        return zones;
    }
    
    public IndexFields getIndexFields()
    {
        return indexFields;
    }

    public void setIndexFields(IndexFields indexFields) {
        this.indexFields = indexFields;
    }
    
    public void addIndexField(IndexField indexField) 
    {
        if(indexField == null)
            indexFields = new IndexFields();
        else
            this.indexFields.addIndexField(indexField);
    }

    public CustomProperties getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(CustomProperties customProperties) {
        this.customProperties = customProperties;
    }

}
