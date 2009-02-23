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

import org.alfresco.webservice.types.Predicate;
import org.alfresco.webservice.types.Reference;
import org.alfresco.webservice.types.Store;
import org.alfresco.webservice.util.Constants;
import org.alfresco.webservice.util.WebServiceFactory;

/**
 *
 * @author dnesbitt
 */
public class AlfrescoDeliveryBase 
{
    /** Admin user name and password used to connect to the repository */
    protected String USERNAME = "";
    protected String PASSWORD = "";
    
    protected String AD_STORE_NAME = "";
    protected String AD_COMPANY_HOME = "";
    protected String AD_FOLDER_NAME = "";
    
    /** The store used throughout the samples */
    protected Store STORE = new Store(Constants.WORKSPACE_STORE, AD_STORE_NAME);
    
    protected Reference AD_FOLDER = new Reference(STORE, null, "/app:" + AD_COMPANY_HOME + "/cm:" + AD_FOLDER_NAME); 

    protected void getRepository() throws Exception
    {
        try
        {
            // Check to see if the sample folder has already been created or not
            WebServiceFactory.getRepositoryService().get(new Predicate(new Reference[]{AD_FOLDER}, STORE, null));
        }
        catch (Exception e)
        {
            throw new Exception("Failed to connect to folder! " + e.toString());
        }
    }

}
