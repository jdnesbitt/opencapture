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

package net.filterlogic.OpenCapture.converters;

import net.filterlogic.OpenCapture.interfaces.IOCConverterPlugin;
import net.filterlogic.OpenCapture.interfaces.OpenCaptureConversionException;
import net.filterlogic.util.imaging.OpenCaptureImagingException;
import net.filterlogic.util.imaging.ToPDF;

/**
 *
 * @author Darron Nesbitt
 */
public class PDFConversion implements IOCConverterPlugin
{
    private String name = "OpenCapture PDF Converter";
    private String description = "This plugin converts TIFFs to PDF.";
    private String pluginID = "PDF";
    private String outputExtension = "PDF";

    public String getName() 
    {
        return name;
    }

    public String getPluginID() 
    {
        return pluginID;
    }

    public String getDescription() 
    {
        return description;
    }

    public String getOutputExtension() 
    {
        return outputExtension;
    }

    public void Convert(String[] inputFiles, String outputFile) throws OpenCaptureConversionException 
    {
        try
        {
            ToPDF.ToPDF(inputFiles, outputFile);
        }
        catch(OpenCaptureImagingException e)
        {
            throw new OpenCaptureConversionException(e.toString());
        }
    }
}
