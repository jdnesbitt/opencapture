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

/**
 *
 * @author dnesbitt
 */
public class Document 
{
    private IndexFields indexFields = null;
    private Pages pages = null;

    private String Name = "";
    private String formID = "";
    private int number = 0;

    /**
     * Constructor used to parse specified document.
     * @param batch XML batch document.
     * @param documentName Name of document to be loaded.
     * @param formID FormID value.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public Document(XMLParser batch,String documentName,String formID, int number) throws OpenCaptureException
    {
        this.Name = documentName;
        this.formID = formID;
        this.number = number;

        loadDocument(batch, documentName);
    }

    /**
     * Load document method is used to parse batch xml and load document related
     * data.
     * @param batch XML batch document.
     * @param documentName Name of document to be loaded.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void loadDocument(XMLParser batch, String documentName) throws OpenCaptureException
    {
        try
        {
            String xPath = (OpenCaptureCommon.INDEX_FIELDS).replaceAll("<1>",documentName);

            // get index fields for this document.
            setIndexFields(new IndexFields(batch, xPath));

            // get pages
            xPath = OpenCaptureCommon.PAGES.replaceAll("<1>", documentName);
            setPages(new Pages(batch, xPath));
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    /**
     * Default Configuration constructor.
     */
    public Document()
    {
        
    }
    
    public Document(IndexFields indexFields, Pages pages, String documentName, String formID,int documentNumber)
    {
        this.indexFields = IndexFields.newInstanceOf(indexFields);
        this.pages = pages;
        this.Name = documentName;
        this.formID = formID;
        this.number = documentNumber;
    }

    public static Document newInstanceOf()
    {
        return new Document();
    }
  
    public static Document newInstanceOf(Document document)
    {
        return new Document(document.getIndexFields(),document.getPages(),document.getName(), document.getFormID(), document.getNumber());
    }

    public IndexFields getIndexFields()
    {
        return indexFields;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    /**
     * String containng form id value.
     * @return String
     */
    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public Pages getPages() {
        return pages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Create new instance (clone) of passed index fields object.
     * @param indexFields
     */
    public void setIndexFields(IndexFields indexFields) 
    {
        this.indexFields =  IndexFields.newInstanceOf(indexFields);
    }

    public void setPages(Pages pages) {
        this.pages = pages;
    }
    
//---------------------------------------------------------------
    
    public void addIndexField(IndexField indexField) 
    {
        if(indexField == null)
            indexFields = new IndexFields();
        else
            this.indexFields.addIndexField(indexField);
    }

    public void addPage(Page page) throws OpenCaptureException 
    {
        if(pages == null)
            pages = new Pages(page);
        else
            this.pages.addPage(page);
    }
}
