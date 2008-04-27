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

import java.util.HashMap;
import java.util.List;
import net.filterlogic.util.xml.XMLParser;

/**
 *
 * @author dnesbitt
 */
public class Document 
{
    private IndexFields indexFields;
    private IndexFields indexDataFields;
    private Zones zones;
    private Pages pages;

    private String Name = "";
    private String formID = "";

    /**
     * Constructor used to parse specified document.
     * @param batch XML batch document.
     * @param documentName Name of document to be loaded.
     * @param formID FormID value.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public Document(XMLParser batch,String documentName,String formID) throws OpenCaptureException
    {
        this.Name = documentName;
        this.formID = formID;

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
            indexFields = new IndexFields(batch,xPath);

            xPath = OpenCaptureCommon.INDEX_DATA_FIELDS.replaceAll("<1>",documentName);

            // get index data fields for this document.
            indexDataFields = new IndexFields(batch, xPath);

            xPath = OpenCaptureCommon.ZONES.replaceAll("<1>", documentName);

            // get zones for this document
            zones = new Zones(batch, xPath);
            
            // get pages
            xPath = OpenCaptureCommon.PAGES.replaceAll("<1>", documentName);
            pages = new Pages(batch, xPath);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    /**
     * Default Document constructor.
     */
    public Document()
    {
    }

    public IndexFields getIndexFields()
    {
        return indexFields;
    }

    public IndexFields getIndexDataFields()
    {
        return indexDataFields;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public String getFormID() {
        return formID;
    }

    public void setFormID(String formID) {
        this.formID = formID;
    }

    public Zones getZones() {
        return zones;
    }

    public Pages getPages() {
        return pages;
    }
}
