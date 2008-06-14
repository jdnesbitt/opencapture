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
public class Zone 
{
    private String Name = "";
    private String Type = "";
    private int X = 0;
    private int Y = 0;
    private int W = 0;
    private int H = 0;
    private byte MinAccuracy = 25;
    private String FieldType = "";
    private String IDValue = "";
    private String DocumentName = "";

    public Zone(String Name,String Type,int X, int Y, int W, int H, byte MinAccuracy, String FieldType)
    {
        this.Name = Name;
        this.Type = Type;
        this.X = X;
        this.Y = Y;
        this.H = H;
        this.W = W;
        this.MinAccuracy = MinAccuracy;
        this.FieldType = FieldType;
    }

    public Zone()
    {
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String Name)
    {
        this.Name = Name;
    }

    public String getType()
    {
        return Type;
    }

    public void setType(String Type)
    {
        this.Type = Type;
    }

    public int getX()
    {
        return X;
    }

    public void setX(int X)
    {
        this.X = X;
    }

    public int getY()
    {
        return Y;
    }

    public void setY(int Y)
    {
        this.Y = Y;
    }

    public int getW()
    {
        return W;
    }

    public void setW(int W)
    {
        this.W = W;
    }

    public int getH()
    {
        return H;
    }

    public void setH(int H)
    {
        this.H = H;
    }

    public byte getMinAccuracy()
    {
        return MinAccuracy;
    }

    public void setMinAccuracy(byte MinAccuracy)
    {
        this.MinAccuracy = MinAccuracy;
    }

    public String getFieldType()
    {
        return FieldType;
    }

    public void setFieldType(String FieldType)
    {
        this.FieldType = FieldType;
    }

    /**
     * Get form id value.
     * @return String
     */
    public String getFormIDValue()
    {
        return IDValue;
    }

    /**
     * Set form id value.
     * @param IDValue
     */
    protected void setFormIDValue(String IDValue)
    {
        this.IDValue = IDValue;
    }

    public String getDocumentName()
    {
        return DocumentName;
    }

    public void setDocumentName(String DocumentName)
    {
        this.DocumentName = DocumentName;
    }
}
