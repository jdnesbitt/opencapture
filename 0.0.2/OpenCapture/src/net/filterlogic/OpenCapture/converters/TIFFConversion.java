/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.converters;

import java.io.File;
import java.util.List;
import net.filterlogic.OpenCapture.interfaces.IOCConverterPlugin;
import net.filterlogic.OpenCapture.interfaces.OpenCaptureConversionException;
import net.filterlogic.imaging.ToTIF;

/**
 *
 * @author dnesbitt
 */
public class TIFFConversion implements IOCConverterPlugin
{

    private String name = "OpenCapture TIFF Converter";
    private String description = "This plugin converts TIFFs to multi-page TIFF.";
    private String pluginID = "TIF";
    private String outputExtension = "TIF";

    @Override
    public String Convert(String[] inputFiles, String outputFile) throws OpenCaptureConversionException
    {
        String result = "";
        ToTIF tt = null;

        try
        {
            tt = new ToTIF();

            for(int i=0;i<inputFiles.length;i++)
            {
                String fileName = inputFiles[i];

                File file = new File(fileName);

                if(!file.exists())
                    throw new Exception(fileName + " does not exist!");

                // add current tif to tt object
                tt.setDocument(fileName);
                
                System.out.println("Added file: " + fileName + " / " + tt.getFiles().size());
            }

            // force compression to group 4 tif
            tt.addDocumentProperty("Compression", "COMPRESSION_GROUP4");
            // force multi-page tif creation
            tt.addDocumentProperty(ToTIF.MULTIPAGE_PROPERTY_NAME, "true");

            // write tif file
            tt.saveDocument(outputFile);

            List fileList = tt.getFiles();

            if(fileList.size()>0)
                result = fileList.get(0).toString();
            else
                throw new Exception("Output TIFF file not created: " + outputFile);

            // return the name of the TIFF file created.  This will be stored in
            // the custom property
            return result;
        }
        catch(Exception e)
        {
            throw new OpenCaptureConversionException(e.toString());
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public String getPluginID()
    {
        return pluginID;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getOutputExtension()
    {
        return outputExtension;
    }

}
