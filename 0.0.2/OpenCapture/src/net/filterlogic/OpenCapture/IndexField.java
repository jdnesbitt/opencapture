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
 *
 * @author dnesbitt
 */
public class IndexField 
{
    private String Name = "";
    private String Type = "";
    private String Value = "";
    private boolean Stickey = false;
    
    // added validator to support custom code at the field level.
    private String Validator = "";
    
    private boolean dirty = false;

    /**
     * Default constructor.
     */
    public IndexField()
    {
    }

    /**
     *
     * @param name
     * @param type
     * @param value
     * @param stickey
     */
    public IndexField(String name,String type, String value, boolean stickey)
    {
        this.Name=name;
        this.Value=value;
        this.Type=type;
        this.Stickey=stickey;
    }

    /**
     * 
     * @param name
     * @param type
     * @param value
     * @param validator
     * @param stickey
     */
    public IndexField(String name,String type, String value, String validator, boolean stickey)
    {
        this.Name=name;
        this.Value=value;
        this.Type=type;
        this.Validator=validator;
        this.Stickey=stickey;
    }


    public void setValidator(String Validator) {
        this.Validator = Validator;
    }

    public String getValidator() {
        return Validator;
    }

    public void setName(String Name)
    {
        if(!this.Name.equals(Name))
        {
            this.Name = Name;
            this.dirty = true;
        }
    }

    public void setType(String Type)
    {
        if(!this.Type.equals(Type))
        {
            this.Type = Type;
            this.dirty = true;
        }
    }

    public void setValue(String Value)
    {
        if(!this.Value.equals(Value))
        {
            this.Value = Value;
            this.dirty = true;
        }
    }

    public void setStickey(boolean Stickey)
    {
        if(this.Stickey != Stickey)
        {
            this.Stickey = Stickey;
            this.dirty = true;
        }
    }

    public String getName()
    {
        return Name;
    }

    public String getType()
    {
        return Type;
    }

    public String getValue()
    {
        return Value;
    }

    public boolean isStickey()
    {
        return Stickey;
    }

    public boolean isDirty()
    {
        return dirty;
    }
    
    
}
