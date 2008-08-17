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

package net.filterlogic.OpenCapture.interfaces;

/**
 * Interface defined for writing custom OCDelivery plugins.  Delivery plugins should be written
 * for each repository documents will be delivered to.
 * 
 * The delivery plugin will implement this interface.  The plugin will be dynamically loaded by the OCDelivery 
 * module each time a batch is configured to delivery to the specified repository.
 * 
 * @author Darron Nesbitt
 */
public interface IOCDeliveryPlugin 
{
    /**
     * Get name of OC Delivery plugin.
     * @return String name of plugin.
     */
    public String getName();
    
    /**
     * Get the OC Delivery plugin description.  The description should contain
     * information about where plugin is configured to deliver documents.
     * @return String description of plugin configuration.
     */
    public String getDescription();
    
    /**
     * Set batch object in plugin.  This method makes batch available to plugin.
     * @param batch Batch to be delivered.
     */
    public void setBatch(net.filterlogic.OpenCapture.Batch batch);
    
    /**
     * Open a connection to the repository.  This method is called once when delivery
     * module loads the delivery plugin.
     * @return Boolean set to true if connection to repository is opened successfully, else false.
     */
    public void OpenRepository() throws OpenCaptureDeliveryException;
    
    /**
     * Deliver document is called for each document in the batch.  This method is called
     * once for each document in the batch.
     * @param document Document to be delivered.
     * @return Boolean set to true if document delivered successfully, else false.
     */
    public void DeliverDocument(net.filterlogic.OpenCapture.Document document) throws OpenCaptureDeliveryException;
    
    /**
     * Closes the connection to the repository.  This method is called once when all documents have
     * been delivered or exception occurs.
     */
    public void CloseRepository() throws OpenCaptureDeliveryException;
}
