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
public class Configuration 
{
    //private IndexFields indexFields = null;
    //private IndexFields indexDataFields = null;
    private Zones zones = null;
    //private Pages pages = null;

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
    public Configuration(XMLParser batch,String documentName,String formID, int number) throws OpenCaptureException
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
//            String xPath = (OpenCaptureCommon.CONF_INDEX_FIELDS).replaceAll("<1>",documentName);
//
//            // get index fields for this document.
//            setIndexFields(new IndexFields(batch, xPath));

//            xPath = OpenCaptureCommon.INDEX_DATA_FIELDS.replaceAll("<1>",documentName);
//
//            // get index data fields for this document.
//            setIndexDataFields(new IndexFields(batch, xPath));

            String xPath = OpenCaptureCommon.ZONES.replaceAll("<1>", documentName);

            // get zones for this document
            setZones(new Zones(batch, xPath));
            
//            // get pages
//            xPath = OpenCaptureCommon.PAGES.replaceAll("<1>", documentName);
//            setPages(new Pages(batch, xPath));
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
    }

    /**
     * Default Configuration constructor.
     */
    public Configuration()
    {
        
    }

//    public IndexFields getIndexFields()
//    {
//        return indexFields;
//    }

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

//    public void setIndexFields(IndexFields indexFields) {
//            public void addIndexFields(IndexField indexField) 
//    {
//        if(indexField == null)
//            indexFields = new IndexFields();
//        else
//            this.indexFields.addIndexField(indexField);
//    }this.indexFields = indexFields;
//    }

    public void setZones(Zones zones) {
        this.zones = zones;
    }

//---------------------------------------------------------------
    
//    public void addIndexFields(IndexField indexField) 
//    {
//        if(indexField == null)
//            indexFields = new IndexFields();
//        else
//            this.indexFields.addIndexField(indexField);
//    }

    public void addZones(Zone zone) throws OpenCaptureException
    {
        if(zones == null)
            zones = new Zones(zone);
        else
            this.zones.addZone(zone);
    }
}
