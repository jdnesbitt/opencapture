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
import net.filterlogic.io.Path;
/**
 *
 * @author dnesbitt
 */
public class BatchClass 
{
    private long ID = -1;
    private String BatchClassName = "";
    private String Description = "";
    private String ImagePath = "";
    private String Version = "";
    private String Priority = "";
    
    public BatchClass(XMLParser newBatch) throws OpenCaptureException
    {
        this.BatchClassName = newBatch.getValue(OpenCaptureCommon.BATCH_CLASS_NAME);
        this.ImagePath = newBatch.getValue(OpenCaptureCommon.BATCH_CLASS_IMAGE_PATH);
        this.Version = newBatch.getValue(OpenCaptureCommon.BATCH_CLASS_VERSION);
        this.Priority = newBatch.getValue(OpenCaptureCommon.BATCH_CLASS_PRIORITY);

        if(this.ImagePath.length()<1)
            throw new OpenCaptureException("Image path not set.");
        
        if(!Path.ValidatePath(ImagePath))
            throw new OpenCaptureException("Image path doesn't exist.[" + this.ImagePath + "].");
        else
            this.ImagePath = Path.FixPath(ImagePath);
    }
    
    public BatchClass(long id, String batchClassName,String desc, String imagePath)
    {
        this.ID = id;
        this.BatchClassName = batchClassName;
        this.Description = desc;
        this.ImagePath = imagePath;
    }
    
    public String getXML()
    {
        return "<BatchClass Name=\"" + this.BatchClassName + "\" ImagePath=\"" + this.ImagePath + "\" version=\"" + this.Version + "\" Priority=\"" + this.Priority + "\">\n";
    }
    public long getID()
    {
        return ID;
    }

    protected void setID(long ID)
    {
        this.ID = ID;
    }

    public String getBatchClassName()
    {
        return BatchClassName;
    }

    protected void setBatchClassName(String BatchClassName)
    {
        this.BatchClassName = BatchClassName;
    }

    public String getDescription()
    {
        return Description;
    }

    protected void setDescription(String Description)
    {
        this.Description = Description;
    }

    public String getImagePath()
    {
        return ImagePath;
    }

    protected void setImagePath(String ImagePath)
    {
        this.ImagePath = ImagePath;
    }

    public String getVersion()
    {
        return Version;
    }

    protected void setVersion(String Version)
    {
        this.Version = Version;
    }

    public String getPriority() {
        return Priority;
    }

    public void setPriority(String Priority) {
        this.Priority = Priority;
    }
    
    
}
