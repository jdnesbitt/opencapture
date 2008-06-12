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

package net.filterlogic.OpenCapture.module;

import net.filterlogic.OpenCapture.*;
import net.filterlogic.OpenCapture.interfaces.OpenCaptureReaderException;

import net.filterlogic.io.Path;
import net.filterlogic.util.imaging.*;

import org.apache.log4j.*;

import java.util.Properties;
import java.util.List;

import java.io.FileInputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import net.filterlogic.OpenCapture.interfaces.IZoneReader;

/**
 *
 * @author dnesbitt
 */
public class OCCognizance 
{
    static Logger myLogger = Logger.getLogger(OCImport.class.getName( ));

    private String configFile = "";
    private String log4j = "";
    private Properties prop = new Properties();
    private String OC_HOME = "";
    private String moduleID = "OCCognizance";

    public OCCognizance(String configFile) throws Exception
    {
        this.configFile = configFile;

        // load property config file.
        FileInputStream fis = new FileInputStream(new File(configFile));
        prop.load(fis);
        fis.close();

        OC_HOME = prop.getProperty("oc_home");
        log4j = prop.getProperty("log4j");

        PropertyConfigurator.configure(log4j);

        myLogger.info("OCCognizance started..");
    }

    /**
     * 
     * @throws java.lang.Exception
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
                // get number of loose pages
                int loosePageCount = batch.getLoosePageCount();
                String imagePath = batch.getImageFilePath();
                List zones = batch.getConfigurations().getFormIDZones();

                // create document object.
                Document document = null;
                Page page = null;
                
                int documentCount = 0;
                int pageIndex = 0;

                boolean docFound = false;
                
                for(int i = 0;i<loosePageCount;i++)
                {
                    // get name of page file
                    String pageName = batch.getPageFileName(i);
                    page = batch.getPage(i);
                    String fullPagePath = Path.FixPath(imagePath) + pageName;

                    // get file object
                    File file = new File(fullPagePath);
                    BufferedImage image = ToTIFF.loadTIFF(file, 0);

                    String formIDMethod = ((Zone)zones.get(0)).getType();

                    String className = batch.getOcConfig().getReaderClass(formIDMethod);

                    String zoneValue = "";

                    try
                    {
                        // load reader
                        Class c = Class.forName(className);

                        // create reader object.
                        IZoneReader zr = (IZoneReader)c.newInstance();

                        // set image object in reader
                        zr.setImage(image);

                        // loop through zones and check image
                        for(int x=0;x<zones.size();x++)
                        {
                            Zone zone = (Zone)zones.get(x);

                            try
                            {
                                zoneValue = zr.ReadZone(zone);
                            }
                            catch(OpenCaptureReaderException ocre)
                            {
                                myLogger.error("Error reading zone[" + zone.getName() + "].  " + ocre.toString());
                                zoneValue = "";
                            }

                            // if document cover found, create doc
                            if(zone.getFormIDValue().equals(zoneValue))
                            {
                                if(docFound)
                                {
                                    Document doc = document.newInstanceOf(document);
                                    batch.addDocument(doc);
                                }
                                else
                                {
                                    docFound = true;
                                }

                                // increment doc count
                                ++documentCount;

                                // create new doc
                                document = new Document();

                                document.setFormID(zoneValue);
                                document.setNumber(documentCount);
                                document.setName(zone.getDocumentName());

                                // read index fields from image
                                Configuration config = batch.getConfigurations().getDocument(zone.getDocumentName());

                                // get zones for zone reader
                                IndexFields ndxFields = readIndexFields(zr, config.getZones(), config);

                                // add read index field values.
                                document.setIndexFields(ndxFields);

                                // exit for loop
                                break;
                            }
                        }
                        
                        // if document found
                        if(docFound)
                        {
                            document.addPage(page);

                            // delete loose page
                            batch.deleteLoosePage(String.valueOf(i));
                        }
                        else
                            throw new OpenCaptureException("First page in batch isn't a cover page.");

                    }
                    catch(Exception ex)
                    {
                        myLogger.error(ex.toString());

                        batch.getLog().setMessage(ex.toString());
                        // close batch with exception
                        //batch.CloseBatch(true, ex.toString());
                    }
                }
                
                // if docs found, add last separated document to batch
                if(docFound)
                {
                    document.addPage(page);

                    // delete loose page
                    batch.deleteLoosePage(String.valueOf(loosePageCount));

                    batch.addDocument(document);
                }


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

    public IndexFields readIndexFields(IZoneReader zr,Zones zones,Configuration document)
    {
        IndexFields indexFields = new IndexFields();

        List list = zones.getZoneNames();

        String zoneValue = "";

        for(int i=0;i<list.size();i++)
        {
            String zoneName = (String)list.get(i);

            if(!zoneName.equals("FORMID"))
            {
                // get names zone.
                Zone zone = zones.getZone(zoneName);

                try
                {
                    // read zone
                    zoneValue = zr.ReadZone(zone);
                }
                catch(OpenCaptureReaderException ocre)
                {
                    myLogger.info(ocre.toString());
                    zoneValue = "";
                }

                // grab config'd index field
                IndexField configIndexField = document.getIndexFields().getIndexField(zoneName);

                // create new index field object with config'd values
                IndexField indexField = new IndexField(zoneName,configIndexField.getType() , zoneValue, configIndexField.isStickey());

                // add index field to list
                indexFields.addIndexField(indexField);
            }
        }

        return indexFields;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try
        {
            if(args.length<1)
            {
                System.out.println("Must pass path to configuration file.");
                System.exit(0);
            }

            OCCognizance ocCognizance = new OCCognizance(args[0]);

            ocCognizance.ProcessBatches();
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
