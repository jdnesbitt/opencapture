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

import org.apache.log4j.*;

import java.util.Properties;
import java.util.List;

import java.io.FileInputStream;
import java.io.File;
import net.filterlogic.OpenCapture.interfaces.IOCConverterPlugin;
import net.filterlogic.OpenCapture.interfaces.OpenCaptureConversionException;

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
        boolean docConverted = false;

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
                        myLogger.info("NextBatch: " + batch.getBatchName());

                        // load converter plugin
                        String converterID = batch.getConfigurations().getQueues().getCurrentQueue().getPluginID();
                        String className = batch.getOcConfig().getConverterClass(converterID);
                        // load converter plugin
                        Class c = Class.forName(className);
                        // create converter object.
                        IOCConverterPlugin converterPlugin = (IOCConverterPlugin)c.newInstance();
                        // get extension of output file
                        String outputExt = converterPlugin.getOutputExtension();

                        int docCount = batch.getDocuments().Count();
                        myLogger.info("Documents: " + docCount);

                        for(int docs=1;docs<=docCount;docs++)
                        {
                            // get document
                            Document document = batch.getDocuments().getDocument(docs);
                            myLogger.info("Document type: " + document.getName());

                            // check doc custom props to see if converterID property already exists.
                            Property property = document.getCustomProperties().getProperty(converterID);
                            myLogger.info(converterID + " Property: " + property.getName());

                            // if property doesn't exist, process document
                            if(property.getName().length()<1)
                            {
                                // get list of page file names for this document
                                List pageList = document.getPages().getPageFileList();

                                String[] files = new String[pageList.size()];

                                myLogger.info("Files to convert: " + files.length);

                                String imagePath = Path.FixPath(batch.getImageFilePath());

                                myLogger.info("ImagePath: " + imagePath);

                                // get each page in batch and build a string array with each page path and name.
                                for(int i=0;i<pageList.size();i++)
                                {
                                    String pageNum = (String)pageList.get(i);

                                    Page page = (Page)document.getPages().getPage(pageNum);
                                    
                                    files[i] = imagePath + page.getName();
                                }

                                // set path to pdf folder
                                String OutPath = Path.FixPath(imagePath + outputExt);

                                myLogger.info("OutputPath: " + OutPath);

                                // create PDF folder
                                if(!Path.ValidatePath(OutPath))
                                    if(!Path.createPath(OutPath))
                                        throw new OpenCaptureException("Can't create coversion folder for batch[" + batch.getBatchName() + "]: " + OutPath);

                                String FileName = String.valueOf(docs) + "." + outputExt;

                                myLogger.info(outputExt + " file: " + OutPath + FileName);

                                try
                                {
                                    converterPlugin.Convert(files, OutPath + FileName);

                                    docConverted = true;
                                }
                                catch(OpenCaptureConversionException oce)
                                {
                                    docConverted = false;

                                    myLogger.error("Error creating document in batch[" + batch.getBatchName() + "] document[" + 
                                            document.getNumber() + "/" + document.getName() + "]: " + oce.toString());
                                }

                                if(docConverted)
                                {
                                    myLogger.info("Document converted to " + outputExt + ".");
                                    // create new volital property to document
                                    document.getCustomProperties().addProperty(new Property(converterID, FileName, true));
                                }
                                else
                                {
                                    // add doc exception
                                    document.setException("Failed to convert document to " + outputExt + " format.");
                                    batch.setException(true);
                                    myLogger.info("Document failed to converted to " + outputExt + ".");
                                }
                            }

                        }

                        // close batch
                        if(batch.hasException())
                            batch.CloseBatch(true, "Error occured converting a document to " + outputExt + " format.");
                        else
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
                     myLogger.error(e.toString());
                    batch.CloseBatch(true, e.toString());              
                }
            }
        }
        catch(OpenCaptureException oce2)
        {
            myLogger.error(oce2.toString());
            batch.CloseBatch(true, oce2.toString());
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

            OCConverter ocConverter = new OCConverter(args[0]);

            ocConverter.ProcessBatches();
        }
        catch(OpenCaptureException oce)
        {
            myLogger.fatal(oce.toString());
        }
        catch(Exception e)
        {
            myLogger.fatal(e.toString());
        }
    }
}
