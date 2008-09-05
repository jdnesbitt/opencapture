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

import net.filterlogic.OpenCapture.*;

import net.filterlogic.io.Path;
import net.filterlogic.util.imaging.*;
import net.filterlogic.OpenCapture.interfaces.*;

import org.apache.log4j.*;

import java.util.Properties;
import java.util.List;

import java.io.FileInputStream;
import java.io.File;
import java.awt.image.BufferedImage;

/**
 * This is a template for creating OpenCapture modules.  There are TODOs that
 * define what needs to be changed.  The default constructor javadoc contains 
 * info. about configuration files required and minimum properties on the 
 * module configuration file.
 * 
 * @author Darron Nesbitt
 */
public class OCModuleTemplate 
{
    // TODO: Change class name in following line from OCModuleTemplate to your class name.
    static Logger myLogger = Logger.getLogger(OCModuleTemplate.class.getName( ));
    
    private IndexFields stickeyFields = new IndexFields();

    private String configFile = "";
    private String log4j = "";
    private Properties prop = new Properties();
    private String OC_HOME = "";

    /* TODO: VERY IMPORTANT -  Change the following line and replace value with registered name of you module.  
       This is used to pickup batches from this modules queue.
    */
    private String moduleID = "OCModuleTemplate";

    /**
     * Default constructor must be passed a properties style config file.  Look
     * at the configuration files for the other modules.  Minimum properties are:
     * 
     * oc_home: Path to OpenCapture home directory.
     * log4j:   Path to log4j configuration file for this module.
     * 
     * @param configFile
     * @throws java.lang.Exception
     */
    public OCModuleTemplate(String configFile) throws Exception
    {
        this.configFile = configFile;

        // load property config file.
        FileInputStream fis = new FileInputStream(new File(configFile));
        prop.load(fis);
        fis.close();

        OC_HOME = prop.getProperty("oc_home");
        log4j = prop.getProperty("log4j");

        PropertyConfigurator.configure(log4j);

        // TODO:  Change the following line for the startup message in the log file.
        myLogger.info("OCModuleTemplate started..");
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

                try
                {
                    // get next batch
                    batch.getNextBatch(moduleID);

                    // batchname empty, no more work
                    if(batch.getBatchName().length()>0)
                    {
                        // TODO: Add module code here to process batch

                        myLogger.info("NextBatch: " + batch.getBatchName());

                        // load converter plugin
                        String converterID = batch.getConfigurations().getQueues().getCurrentQueue().getPluginID();
                        String className = batch.getOcConfig().getConverterClass(converterID);
                        // load converter plugin
                        Class c = Class.forName(className);
                        // create converter object.
                        IOCDeliveryPlugin deliveryPlugin = (IOCDeliveryPlugin)c.newInstance();

                        // close batch
                        batch.CloseBatch();
                    }
                    else
                    {
                            moreWork = false;
                            batch = null;
                    }
                }
                catch(Exception e)
                {
                    myLogger.error(net.filterlogic.util.StackTraceUtil.getStackTrace(e));
                    batch.CloseBatch(true, e.toString());
                }

            }
        }
        catch(OpenCaptureException oce2)
        {
            myLogger.error(net.filterlogic.util.StackTraceUtil.getStackTrace(oce2));
        }
    }
    
    public static void main(String[] args) 
    {
        try
        {
            if(args.length<1)
            {
                System.out.println("Must pass path to configuration file.");
                System.exit(0);
            }

            OCModuleTemplate ocDelivery = new OCModuleTemplate(args[0]);

            ocDelivery.ProcessBatches();
        }
        catch(OpenCaptureException oce)
        {
            myLogger.fatal(net.filterlogic.util.StackTraceUtil.getStackTrace(oce));
        }
        catch(Exception e)
        {
            myLogger.fatal(net.filterlogic.util.StackTraceUtil.getStackTrace(e));
        }
    }
}
