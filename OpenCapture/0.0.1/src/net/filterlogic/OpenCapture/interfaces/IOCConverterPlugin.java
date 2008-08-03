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

/**
 * IOCConverterPlugin interface was defined for writing custom conversion plugins.
 * 
 * @author Darron Nesbitt
 */
public interface IOCConverterPlugin 
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
     * Get output file's extension.
     * 
     * @return String containing what the output file's extension should be set to.
     */
    public String getOutputExtension();
    
    /**
     * Convert method converts files passed to method to the plugins output
     * file type.
     * 
     * @param inputFiles String array of files.
     * @param outputFile String containing name of output file.
     * 
     * @throws OpenCaptureConversionException 
     */
    public void Convert(String[] inputFiles, String outputFile) throws OpenCaptureConversionException;
}
