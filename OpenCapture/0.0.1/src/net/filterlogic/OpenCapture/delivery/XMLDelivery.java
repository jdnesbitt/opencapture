/*
Copyright 2008 Filter Logic

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://wwwthis.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package net.filterlogic.OpenCapture.delivery;

import net.filterlogic.OpenCapture.Batch;
import net.filterlogic.OpenCapture.CustomProperties;
import net.filterlogic.OpenCapture.Document;
import net.filterlogic.OpenCapture.interfaces.IOCDeliveryPlugin;

import net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException;
import net.filterlogic.io.Path;
import net.filterlogic.OpenCapture.OpenCaptureCommon;

/**
 *
 * @author Darron Nesbitt
 */
public class XMLDelivery implements IOCDeliveryPlugin
{

    private String name = "XMLDelivery";
    private Batch batch;
    private String description = "XML delivery plugin writes XML index data file and image file to the filesystem.";
    
    private boolean isPdfDelivery=false;
    private boolean isTiffDelivery=false;
    private String workingPath = "";
    private String deliveryPath = "";
    
    private String batchID = "";
    private int docCount = 0;
    
    public void OpenRepository() throws OpenCaptureDeliveryException
    {
        try
        {
            // check if working and deliery path exist.
            CustomProperties customProps = batch.getConfigurations().getCustomProperties();
            String tempPath = System.getProperty("java.io.tmpdir");

            this.isPdfDelivery = customProps.getProperty("OC_PDF_DELIVERY").getValue().equals("Y") ? true : false;
            this.isTiffDelivery = customProps.getProperty("OC_TIFF_DELIVERY").getValue().equals("Y") ? true : false;
            this.workingPath = customProps.getProperty("OC_WORKING_PATH").getValue().length()>0 ? customProps.getProperty("OC_WORKING_PATH").getValue() : tempPath;
            this.deliveryPath = customProps.getProperty("OC_DELIVERY_PATH").getValue();

            // validate delivery path
            if(!Path.ValidatePath(deliveryPath))
                throw new OpenCaptureDeliveryException("Invalid delivery path set for XMLDelivery[" + deliveryPath + "]");

            long id = Long.parseLong(batch.getID());
            batchID = OpenCaptureCommon.getBatchFolderName(id);
        }
        catch(OpenCaptureDeliveryException ocde)
        {
            throw new OpenCaptureDeliveryException(ocde.toString());
        }

    }

    public void CloseRepository() throws OpenCaptureDeliveryException
    {
    }

    public void DeliverDocument(Document document) throws OpenCaptureDeliveryException
    {
        
        try
        {
            
        }
        catch(Exception e)
        {
            
        }
    }

    public String getDescription() 
    {
        return description;
    }

    public String getName() 
    {
        return name;
    }

    public void setBatch(Batch batch) 
    {
        this.batch = batch;
    }
}
