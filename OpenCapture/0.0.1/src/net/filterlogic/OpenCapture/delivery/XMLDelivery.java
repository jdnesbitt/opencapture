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
import net.filterlogic.OpenCapture.Configuration;
import net.filterlogic.OpenCapture.interfaces.IOCDeliveryPlugin;

/**
 *
 * @author Darron Nesbitt
 */
public class XMLDelivery implements IOCDeliveryPlugin
{

    private String name = "XMLDelivery";
    private Batch batch;
    private String description = "XML delivery plugin writes XML index data file and image file to the filesystem.";
    

    public boolean OpenRepository() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean CloseRepository() 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean DeliverDocument(Configuration document) 
    {
        throw new UnsupportedOperationException("Not supported yet.");
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
