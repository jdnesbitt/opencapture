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

import java.io.FileNotFoundException;
import java.io.IOException;
import net.filterlogic.OpenCapture.Batch;
import net.filterlogic.OpenCapture.BatchField;
import net.filterlogic.OpenCapture.IndexField;
import net.filterlogic.OpenCapture.CustomProperties;
import net.filterlogic.OpenCapture.Property;
import net.filterlogic.OpenCapture.Document;
import net.filterlogic.OpenCapture.interfaces.IOCDeliveryPlugin;

import net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException;
import net.filterlogic.io.Path;
import net.filterlogic.io.FileAccess;
import net.filterlogic.OpenCapture.OpenCaptureCommon;

import java.util.List;
import net.filterlogic.OpenCapture.OpenCaptureException;

/**
 * The XMLDelivery plugin writes images and XML index data files to the 
 * configured destination path.
 * 
 * @author Darron Nesbitt
 */
public class XMLDelivery implements IOCDeliveryPlugin
{
    // the following 3 values are based on what this plugin supports.
    private final int DELIVER_TIFF_MULTI = 1;
    private final int DELIVER_TIFF_SINGLE = 2;
    private final int DELIVER_PDF = 3;
    
    /**
     * PDF_PROPERTY_NAME comes from knowing the Converter plugin that created the documents.
     */
    private final String PDF_PROPERTY_NAME = "PDF";

    private String name = "XMLDelivery";
    private Batch batch;
    private String description = "XML delivery plugin writes XML index data file and image file to the filesystem.";
    
    private int deliveryFileFormat=-1;
    private String workingPath = "";
    private String deliveryPath = "";
    
    private String batchID = "";
    private int docCount = 0;
    
    private Document document;
    
    /**
     * Open connection to repository.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    public void OpenRepository() throws OpenCaptureDeliveryException
    {
        try
        {
            // check if working and deliery path exist.
            CustomProperties customProps = batch.getConfigurations().getCustomProperties();
            String tempPath = System.getProperty("java.io.tmpdir");

            this.deliveryFileFormat = customProps.getProperty("OC_DELIVERY_FORMAT").getValue().length()>0 ? 
                Integer.parseInt(customProps.getProperty("OC_DELIVERY_FORMAT").getValue()) : 0;
            
            if(deliveryFileFormat == 0)
                throw new OpenCaptureDeliveryException("Invalid delivery format configured.");

            this.workingPath = customProps.getProperty("OC_WORKING_PATH").getValue().length()>0 ? customProps.getProperty("OC_WORKING_PATH").getValue() : tempPath;
            this.deliveryPath = customProps.getProperty("OC_DELIVERY_PATH").getValue();

            // validate delivery path
            if(!Path.ValidatePath(deliveryPath))
                throw new OpenCaptureDeliveryException("Invalid delivery path set for XMLDelivery[" + deliveryPath + "]");

            // add trailing slash to paths
            this.workingPath = Path.FixPath(this.workingPath);
            this.deliveryPath = Path.FixPath(this.deliveryPath);
            

            long id = Long.parseLong(batch.getID());
            batchID = OpenCaptureCommon.getBatchFolderName(id);
        }
        catch(OpenCaptureDeliveryException ocde)
        {
            throw new OpenCaptureDeliveryException(ocde.toString());
        }

    }

    /**
     * Close repository connection.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    public void CloseRepository() throws OpenCaptureDeliveryException
    {
    }

    /**
     * Deliver document and XML index data file.
     * 
     * @param document Document to deliver.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    public void DeliverDocument(Document document) throws OpenCaptureDeliveryException
    {
        this.document = document;

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String filename = String.valueOf(document.getNumber());

        try
        {
            
            // write image file
            String pages = WriteDocument();
            
            xml += "<Document>";
            
            List<String> batchFieldNames = (List<String>)batch.getBatchFields().getNameList();
            
            xml += "<BatchFields>";
            //xml += batch.getBatchFields().getXML();
            // get batch fields
            for(int i=0;i<batchFieldNames.size();i++)
            {
                String name = (String)batchFieldNames.get(i);
                
                BatchField bf = (BatchField)batch.getBatchFields().getBatchField(name);
                
                xml += "<BatchField Name=\"" + name + "\" Value=\"" + bf.getValue() + "\" />";
                
                bf = null;
            }
            
            xml += "</BatchFields>";

            // get index fields
            List<String> indexFieldNames = (List<String>)document.getIndexFields().getNameList();
            
            xml += "<IndexFields>";
            //xml += document.getIndexFields().getXML();

            // get batch fields
            for(int i=0;i<indexFieldNames.size();i++)
            {
                String name = (String)indexFieldNames.get(i);

                IndexField ndx = (IndexField)document.getIndexFields().getIndexField(name);

                xml += "<IndexField Name=\"" + name + "\" Value=\"" + ndx.getValue() + "\" />";

                ndx = null;
            }

            xml += "</IndexFields>";

            // add pages to xml
            xml += pages;

            // close document tag
            xml += "</Document>";

            // write xml file
            WriteXML(xml, filename + ".xml");
        }
        catch(Exception e)
        {
            throw new OpenCaptureDeliveryException(e.toString());
        }
        finally
        {
            ++docCount;
        }
    }
    
    /**
     * Write document to filesystem.
     * 
     * @param filename Name of output file.  
     * 
     * @return String containing Page tag.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    private String WriteDocument() throws OpenCaptureDeliveryException
    {
        String pagesXML = "<Pages>";

        switch(this.deliveryFileFormat)
        {
            case DELIVER_PDF:
                pagesXML += WritePDF();

                break;

            case DELIVER_TIFF_MULTI:
                throw new OpenCaptureDeliveryException("Delivery of multi-page TIFFs not yet supported!");

            case DELIVER_TIFF_SINGLE:
                throw new OpenCaptureDeliveryException("Delivery of single page TIFFs not yet supported!");
        }


        pagesXML += "</Pages>";

        return pagesXML;
    }

    /**
     * Write PDF file.
     * 
     * @param Name of destination PDF file.
     * 
     * @return Name and path to destination PDF.
     * 
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureDeliveryException
     */
    private String WritePDF() throws OpenCaptureDeliveryException
    {
        String page = "";
        // get property containg PDF document info.
        Property property = document.getCustomProperties().getProperty(PDF_PROPERTY_NAME);
        
        // check if valid property object
        if(property.getName().length()<1)
            throw new OpenCaptureDeliveryException("PDF document property doesn't exist.");
        
        // get pdf filename from value attribute.
        String pdfName = property.getValue();
        
        String sourceDir;
        
        try
        {
            // set sourdir with PDF dir added.
            sourceDir = Path.FixPath(batch.getImageFilePath()) + "PDF" + OpenCaptureCommon.PATH_SEPARATOR;
        }
        catch(OpenCaptureException oce)
        {
            throw new OpenCaptureDeliveryException("WritePDF: Unable to add trailing slash to image path.");
        }

        try
        {
            // create output folder
            if(!Path.ValidatePath(this.deliveryPath + this.batchID))
                if(!Path.createPath(this.deliveryPath + this.batchID))
                    throw new OpenCaptureDeliveryException("Unable to create delivery directory[" + this.deliveryPath + this.batchID + "]");
            
            String dPath = Path.FixPath(this.deliveryPath + this.batchID) + pdfName;

            // copy pdf file to new file.
            FileAccess.CopyFile(sourceDir + pdfName, dPath);
        }
        catch(Exception e)
        {
            throw new OpenCaptureDeliveryException("Cannont copy " + sourceDir + pdfName + " to " + this.deliveryPath + pdfName);
        }
        
        // set page tag
        page = "<Page Name=\"" + pdfName + "\" />";

        return page;
    }
    
    private void WriteTIFFSingle(String filename)
    {
        
    }

    /**
     * Write XML to file.
     * @param xml String containing XML
     * @param filename Name of XML file without path.
     */
    private void WriteXML(String xml,String filename) throws OpenCaptureDeliveryException
    {
        try
        {
            String dPath = Path.FixPath(this.deliveryPath + this.batchID) + filename;

            net.filterlogic.io.ReadWriteTextFile.setContents(new java.io.File(dPath), xml);
        }
        catch(FileNotFoundException fnfe)
        {
            throw new OpenCaptureDeliveryException("FileNoteFound Exception writing XML index file[" + filename + "]");
        }
        catch(IOException ioe)
        {
            throw new OpenCaptureDeliveryException("IOException writing XML index file[" + filename + "]");
        }
    }
    
    /**
     * Get delivery plugin description
     * 
     * @return String containing description.
     */
    public String getDescription() 
    {
        return description;
    }

    /**
     * Get plugin name.
     * 
     * @return String containing plugin name.
     */
    public String getName() 
    {
        return name;
    }

    /**
     * Set batch object in delivery plugin.
     * 
     * @param batch Batch object to set.
     */
    public void setBatch(Batch batch) 
    {
        this.batch = batch;
    }
}
