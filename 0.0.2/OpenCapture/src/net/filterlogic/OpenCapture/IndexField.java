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
    private boolean Visible = true;
    private String label = "";
    private String keyValue = "";
    
    // added validator to support custom code at the field level.
    private String Validator = "";
    
    private boolean dirty = false;

    private FieldType fieldType = new FieldType();

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

        fieldType = new FieldType(type);
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

        fieldType = new FieldType(type);
    }

    public IndexField(String name,String type, String value, String validator, boolean stickey, boolean visible)
    {
        this.Name=name;
        this.Value=value;
        this.Type=type;
        this.Validator=validator;
        this.Stickey=stickey;
        this.Visible = visible;

        fieldType = new FieldType(type);
    }

    public IndexField(String name,String type, String value, String validator, boolean stickey, boolean visible, String label)
    {
        this.Name=name;
        this.Value=value;
        this.Type=type;
        this.Validator=validator;
        this.Stickey=stickey;
        this.Visible = visible;
        this.label = label;

        fieldType = new FieldType(type);
    }

    public IndexField(String name,String type, String value, String validator, boolean stickey, boolean visible, String label, String keyValue)
    {
        this.Name=name;
        this.Value=value;
        this.Type=type;
        this.Validator=validator;
        this.Stickey=stickey;
        this.Visible = visible;
        this.label = label;
        this.keyValue = keyValue;

        fieldType = new FieldType(type);
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


    public void setValidator(String Validator) {
        this.Validator = Validator;
    }

    public String getValidator() {
        return Validator;
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisible(boolean Visible) {
        this.Visible = Visible;
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

    public FieldType getFieldType()
    {
        return fieldType;
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

    @Override
    public String toString()
    {
        String stickey = isStickey() ? "N" : "Y";
        String visible = isVisible() ? "N" : "Y";

        String xml = "<IndexField Name=\"" + getName() + "\" Type=\"" + getType() +
                    "\" Value=\"" + getValue() + "\" Stickey=\"" + stickey + "\" Visible=\"" + visible + "\" Validator=\"" + getValidator() + "\" KeyValue=\"" + keyValue + "\" />\n";

        return xml;
    }



    /**
     * FieldType class holds the index field type data from the type
     * attribute of the IndexField object.
     */
    public class FieldType
    {
        private String dataType = "";
        private float dataLength = 0;
        private String mask = "";

        public FieldType()
        {}

        public FieldType(String unparsedTypeData)
        {
            String[] data = unparsedTypeData.split("\\|");

            if(data.length>1)
            {
                this.dataType = data[0];
                this.dataLength = data[1] != null ? Float.valueOf(data[1]) : 0;

                if(data.length>2)
                    this.mask = data[2];
            }
            else
                this.dataType = data[0];
        }

        public FieldType(String dataType, float dataLength, String mask)
        {
            this.dataType = dataType;
            this.dataLength = dataLength;
            this.mask = mask;
        }

        public float getDataLength() {
            return dataLength;
        }

        public void setDataLength(float dataLength) {
            this.dataLength = dataLength;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getMask() {
            return mask;
        }

        public void setMask(String mask) {
            this.mask = mask;
        }

        @Override
        public String toString()
        {
            return dataType + "|" + String.valueOf(dataLength) + "|" + mask;
        }


    }
}
