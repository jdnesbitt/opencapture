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

package net.filterlogic.OpenCapture.module;

import net.filterlogic.OpenCapture.*;

import net.filterlogic.io.Path;
import net.filterlogic.util.imaging.*;

import org.apache.log4j.*;

import java.util.Properties;
import java.util.List;

import java.io.FileInputStream;
import java.io.File;
import java.awt.image.BufferedImage;

/**
 *
 * @author Darron Nesbitt
 */
public class OCConverter 
{
    static Logger myLogger = Logger.getLogger(OCConverter.class.getName( ));
    
    private IndexFields stickeyFields = new IndexFields();

    private String configFile = "";
    private String log4j = "";
    private Properties prop = new Properties();
    private String OC_HOME = "";
    private String moduleID = "OCConverter";

    public OCConverter(String configFile) throws Exception
    {
        this.configFile = configFile;

        // load property config file.
        FileInputStream fis = new FileInputStream(new File(configFile));
        prop.load(fis);
        fis.close();

        OC_HOME = prop.getProperty("oc_home");
        log4j = prop.getProperty("log4j");

        PropertyConfigurator.configure(log4j);

        myLogger.info("OCConverter started..");
    }

    /**
     * Process batches in this queue.
     * 
     * @throws OpenCaptureException
     */
    public void ProcessBatches() throws OpenCaptureException
    {
        // create batch object
        Batch batch = null;

        boolean moreWork = true;
        boolean coverFound = false;
        
        try
        {
        // while more batches to process
        while(moreWork)
        {
            batch = new Batch();

            // get next batch
            batch.getNextBatch(moduleID);
            
            // batchname empty, no more work
            if(batch.getBatchName().length()>0)
            {
                // TODO: Add module code here to process batch

                // close batch
                batch.CloseBatch();
            }
            else
            {
                    moreWork = false;
                    batch = null;
            }
            
            
        }
        }
        catch(OpenCaptureException oce2)
        {
            myLogger.error(oce2.toString());
        }
    }
}
