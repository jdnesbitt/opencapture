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

import net.filterlogic.util.NamedValueList;
import net.filterlogic.util.xml.XMLParser;
import java.util.List;
import java.util.HashMap;

/**
 * CustomProperties class is used to manage custom properties setup for 
 * either batch class configuration ( used by custom module ) or at the
 * document level where they're volitility can be set.
 * @author dnesbitt
 */
public class CustomProperties 
{
    NamedValueList<String,Property> properties = new NamedValueList<String, Property>();

    /**
     * Default constructor.
     */
    public CustomProperties()
    {}

    /**
     * Constructor to load properties from configuration.
     * 
     * @param batch Batch XML object.
     * @param xPath Xpath to custom property section.
     * 
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public CustomProperties(XMLParser batch, String xPath) throws OpenCaptureException
    {
        try
        {
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String name = (String)map.get("Name");
                String value = (String)map.get("Value");
                boolean volitile = map.get("Volitile")!=null ? Boolean.getBoolean((String)map.get("Volitile")) : false;

                // create and fill  field object
                Property property = new Property(name, value, volitile);

                // add ndx field to hash
                properties.put(name, property);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }  
    }

    /**
     * Get named property.
     * @param name Name of property.
     * 
     * @return Property object, else null if doesn't exist.
     */
    public Property getProperty(String name)
    {
        return this.properties.get(name) != null ? (Property)this.properties.get(name) : new Property();
    }

    /**
     * Add property to collection.
     * 
     * @param property Property object to add.
     */
    public void addProperty(Property property)
    {
        this.properties.put(property.getName(), property);
    }

    /**
     * Get XML representation of object.
     * 
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";

        List list = properties.getOrderedNameList();
        
        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            Property property = (Property)properties.get(name);
            String volital = !property.isVolital() ? "N" : "Y";
            
            xml += "<Property Name=\"" + property.getName() + 
                    "\" Value=\"" + property.getValue() + "\" Volital=\"" + volital + "\" />\n";
        }

        return xml;
    }
}
