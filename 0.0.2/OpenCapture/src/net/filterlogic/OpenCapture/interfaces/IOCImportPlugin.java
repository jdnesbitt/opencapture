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

package net.filterlogic.OpenCapture.interfaces;

import net.filterlogic.OpenCapture.managers.ImportSettings;

/**
 * IOCConverterPlugin interface was defined for writing custom conversion plugins.
 * 
 * @author Darron Nesbitt
 */
public interface IOCImportPlugin 
{
    /**
     * Get name of OC Converter plugin.
     * @return String name of plugin.
     */
    public String getName();

    /**
     * Get plugin id.
     * 
     * @return String containing ID.
     */
    public String getPluginID();

    /**
     * Get descriptconverterIDion of OC Converter plugin.
     * 
     * @return String containing description.
     */
    public String getDescription();
    
    /**
     * Convert method converts files passed to method to the plugins output
     * file type.
     * 
     * @param inputFiles String array of files.
     * @param outputFile String containing name of output file.
     * 
     * @return Returns a string value that is stored in the custom property created for the converted document. The returned 
     * value is stored in the Value attribute.
     * 
     * @throws OpenCaptureConversionException 
     */
    public void importImages(ImportSettings impSettings) throws OpenCaptureConversionException;
}
