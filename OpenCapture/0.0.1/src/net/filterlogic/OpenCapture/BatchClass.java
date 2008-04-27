/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
        return "<BatchClass Name\"" + this.BatchClassName + "\" ImagePath=\"" + this.ImagePath + "\" version=\"" + this.Version + "\">\n";
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
