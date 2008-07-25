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
 * @author Darron Nesbitt
 */
public class OCCognizance 
{
    static Logger myLogger = Logger.getLogger(OCCognizance.class.getName( ));
    
    private IndexFields stickeyFields = new IndexFields();

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
                                myLogger.error("Error reading zone[" + zone.getName() + "] in batch[" + batch.getBatchName() + "].  " + ocre.toString());
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
                                IndexFields confIndexFields = batch.getConfigurations().getIndexFields();

                                // get zones for zone reader
                                IndexFields ndxFields = readIndexFields(zr, config.getZones(), confIndexFields);

                                // TODO: Add Sticky Value code here
                                
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
                        throw new OpenCaptureException("Error processing batch[" + batch.getBatchName() + "]" + ex.toString());
                    }
                }
                
                // if docs found, add last separated document to batch
                if(docFound)
                {
                    // add loose page to document
                    document.addPage(page);

                    // delete loose page
                    batch.deleteLoosePage(String.valueOf(loosePageCount));

                    // add document to batch
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

    /**
     * Read defined index fields using the configured zones.
     * 
     * @param zr Reader that implements the IZoneReader Interface.
     * @param zones Zones object that contains all zones to be read.
     * @param document Configuration object.
     * 
     * @return Returns an IndexFields object containing read index field values.
     */
    public IndexFields readIndexFields(IZoneReader zr,Zones zones,IndexFields confIndexFields) throws Exception
    {
        IndexFields indexFields = new IndexFields();

        List list = zones.getZoneNames();

        String zoneValue = "";

        try
        {
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
                    IndexField configIndexField = confIndexFields.getIndexField(zoneName);

                    // create new index field object with config'd values
                    IndexField indexField = new IndexField(zoneName,configIndexField.getType() , zoneValue, configIndexField.isStickey());

                    // if no stickey fields exists and current is stickey, add to stickey fields
                    if(stickeyFields.Count()<1 && indexField.isStickey())
                        stickeyFields.addIndexField(indexField);

                    IndexField stickeyZone = stickeyFields.getIndexField(zoneName);

                    // if index field is stickey and stickey zone name is empty, add to stickey.
                    if(indexField.isStickey() && stickeyZone.getName().length()<1)
                        stickeyFields.addIndexField(indexField);
                    else
                    {
                        // update stickey value
                        if(stickeyZone.getName().equals(zoneName) && 
                                stickeyZone.isStickey())
                        {
                            // delete old stickey field
                            stickeyFields.deleteIndexField(zoneName);

                            // set new stickey field value
                            stickeyZone.setValue(zoneValue);

                            // add new stickey field zone
                            stickeyFields.addIndexField(stickeyZone);
                        }
                    }

                    // add index field to list
                    indexFields.addIndexField(indexField);
                }
            }

            // add the rest of the fields values from stickey values.
            for(int i=0;i<confIndexFields.Count();i++)
            {
                IndexField confField = confIndexFields.get(i);

                String fieldName = confField.getName();

                IndexField ndxField = indexFields.getIndexField(fieldName);

                if(ndxField.getName().length()>0)
                {
                    if(ndxField.getValue().length()<1)
                    {
                        IndexField stickeyFld = stickeyFields.getIndexField(fieldName);

                        if(stickeyFld.getName().length()>0)
                        {
                            indexFields.getIndexField(fieldName).setValue(stickeyFld.getValue());
                        }
                    }
                }
                else
                {
                    // if configured field is stickey
                    if(confField.isStickey())
                    {
                        // get stickey field from list
                        IndexField stickeyFld = stickeyFields.getIndexField(fieldName);

                        // if valid stickey field
                        if(stickeyFld.getName().length()>0)
                            // add to index fields list for document
                            indexFields.addIndexField(stickeyFld);
                        else
                            // else add configured index field to index list for document
                            indexFields.addIndexField(confField);
                    }
                    else
                        // else just add configured index field to index list for document
                        indexFields.addIndexField(confField);
                }
            }

            return indexFields;
        }
        catch(Exception e)
        {
            throw new Exception(e.toString());
        }
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
