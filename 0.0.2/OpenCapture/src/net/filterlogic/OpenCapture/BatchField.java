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
public class BatchField 
{
    private String Name = "";
    private String Type = "";
    private String Value = "";
    
    private boolean dirty = false;

    public BatchField()
    {
    }

    public BatchField(String Name, String Type, String Value)
    {
        this.Name = Name;
        this.Type = Type;
        this.Value = Value;
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

    public boolean isDirty()
    {
        return dirty;
    }

}
