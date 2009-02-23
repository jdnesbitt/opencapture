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
import net.filterlogic.OpenCapture.Document;
import net.filterlogic.OpenCapture.interfaces.IOCDeliveryPlugin;
import net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException;
import net.filterlogic.OpenCapture.Configurations;
import net.filterlogic.OpenCapture.Configuration;
import net.filterlogic.OpenCapture.CustomProperties;
import net.filterlogic.OpenCapture.Property;

import java.io.InputStream;

import org.alfresco.webservice.content.Content;
import org.alfresco.webservice.content.ContentServiceSoapBindingStub;
import org.alfresco.webservice.repository.UpdateResult;
import org.alfresco.webservice.types.CML;
import org.alfresco.webservice.types.CMLCreate;
import org.alfresco.webservice.types.ContentFormat;
import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ParentReference;
import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.util.AuthenticationUtils;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.ContentUtils;
import org.alfresco.webservice.util.Utils;
import org.alfresco.webservice.util.WebServiceFactory;

import org.alfresco.webservice.authentication.AuthenticationFault;
/**
 * This Alfresco Delivery Plugin uses the web services 2.1 interface to upload
 * documents to the repository.
 * 
 * To configure this delivery plugin add the following entries to the batch
 * xml configuration file in the Configuration/CustomProperties section.  Each
 * entry will be a Property in the CustomProperties.
 * 
 * OC_ALFRESCO_DELIVERY_USER        = Alfreso login user name.
 * OC_ALFRESCO_DELIVERY_USER_PWD    = Afresco user's password
 * OC_ALFRESCO_DELIVERY_STORE       = Name of store to connect to.
 * OC_ALFRESCO_DELIVERY_CONAME      = Name of home to deliver documents to.
 * OC_ALFRESCO_DELIVERY_FOLDER      = Name of folder to deliver documents to.
 * 
 * @author dnesbitt
 */
public class AlfrescoDelivery extends AlfrescoDeliveryBase implements IOCDeliveryPlugin
{
    private static final String DELIVERY_USERNAME = "OC_ALFRESCO_DELIVERY_USER";
    private static final String DELIVERY_USER_PWD = "OC_ALFRESCO_DELIVERY_USER_PWD";
    private static final String DELIVERY_STORE = "OC_ALFRESCO_DELIVERY_STORE";
    private static final String DELIVERY_CONAME = "OC_ALFRESCO_DELIVERY_CONAME";
    private static final String DELIVERY_FOLDER = "OC_ALFRESCO_DELIVERY_FOLDER";

    private String name = "AlfrescoDelivery";
    private Batch batch;
    private String description = "Alfresco delivery plugin loads image file to configured Alfresco.";

    private int deliveryFileFormat=-1;
    private String workingPath = "";
    private String deliveryPath = "";

    private String batchID = "";
    private int docCount = 0;

    private Document document;

    /**
     * Close connection to Alfresco repository.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    public void CloseRepository() throws OpenCaptureDeliveryException 
    {
        // End the session
        AuthenticationUtils.endSession();
    }

    /**
     * Delivery document to Alfresco repository.
     * 
     * @param document Document to deliver.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    public void DeliverDocument(Document document) throws OpenCaptureDeliveryException 
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Open a connection to the Alfresco repository.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    public void OpenRepository() throws OpenCaptureDeliveryException 
    {
        try
        {
            // Start the session
            AuthenticationUtils.startSession(USERNAME, PASSWORD);
            getRepository();
        }
        catch(AuthenticationFault af)
        {
            throw new OpenCaptureDeliveryException(this.name + ": Failed to connect to the repository! " + af.dumpToString());
        }
        catch(Exception e)
        {
            throw new OpenCaptureDeliveryException(this.name + ":" + e.toString());
        }
    }

    /**
     * Get the description of the delivery plugin.
     * @return String containing plugin description.
     */
    public String getDescription() 
    {
        return this.description;
    }

    /**
     * Get the name of the delivery plugin.
     * @return String containing delivery plugin name.
     */
    public String getName() 
    {
        return this.name;
    }

    /**
     * Set reference to batch being processed for delivery.
     * @param batch Batch object for batch to deliver.
     */
    public void setBatch(Batch batch) 
    {
        this.batch = batch;

        // get custom properties from configuration
        CustomProperties custProps = this.batch.getConfigurations().getCustomProperties();

        // retrieve values for connecting to repository.
        USERNAME = custProps.getProperty(DELIVERY_USERNAME).getValue();
        PASSWORD = custProps.getProperty(DELIVERY_USER_PWD).getValue();
        AD_STORE_NAME = custProps.getProperty(DELIVERY_STORE).getValue();
        AD_COMPANY_HOME = custProps.getProperty(DELIVERY_CONAME).getValue();
        AD_FOLDER_NAME = custProps.getProperty(DELIVERY_FOLDER).getValue();
    }

}
