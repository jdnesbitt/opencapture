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

/**
 * Property object is used to hold a piece of data.
 * 
 * @author dnesbitt
 */
public class Property 
{
    private String name = "";
    private String value = "";
    private boolean volital = false;

    /**
     * Default property constructor.
     */
    public Property()
    {}

    /**
     * Property to set.
     * @param name Name of property
     * @param value Value of property
     * @param volitile Volitile
     */
    public Property(String name, String value, boolean volital)
    {
        this.name = name;
        this.value = value;
        this.volital = volital;
    }

    /**
     * Get name of property.
     * @return String containing property name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set property name.
     * @param name Name of property.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get property value.
     * @return String containing value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Set property value.
     * @param value String value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Is property volitile.
     * @return Boolean
     */
    public boolean isVolital() {
        return volital;
    }

    /**
     * Set property volitility.
     * @param volitile True or false.
     */
    public void setVolitile(boolean volital) {
        this.volital = volital;
    }
}
