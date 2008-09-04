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

package net.filterlogic.OpenCapture;

import java.util.HashMap;
import java.util.List;

import net.filterlogic.io.Path;
import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.NamedValueList;

/**
 * Configuration class for OpenCapture core.  
 * 
 * @author Darron Nesbitt
 */
public class OpenCaptureConfig 
{
    private static final String OC_CONFIG_FILE_NAME = "OpenCaptureConfig.xml";
    private static String OC_READER_PLUGINS = "//OpenCapture/ReaderPlugins/Plugin";
    private static String OC_READER_PLUGIN = "//OpenCapture/ReaderPlugins/Plugin[PluginID=\"<1>\"]";

    private static String OC_DELIVERY_PLUGINS = "//OpenCapture/DeliveryPlugins/Plugin";
    private static String OC_DELIVERY_PLUGIN = "//OpenCapture/DeliveryPlugins/Plugin[PluginID=\"<1>\"]";

    private static String OC_CONVERTER_PLUGINS = "//OpenCapture/ConverterPlugins/Plugin";
    private static String OC_CONVERTER_PLUGIN = "//OpenCapture/ConverterPlugins/Plugin[PluginID=\"<1>\"]";
    
    private static String OC_CLASS_ATTR = "Class";
    private static String OC_NAME_ATTR = "Name";
    private static String OC_DESCRIPTION_ATTR = "Description";

    private String m_OCPath = "";
    private String m_OCConfigPath = "";
    private String m_OCConfigFile = "";
    private XMLParser m_XmlConfig = new XMLParser();
    
    private NamedValueList<String,HashMap> readerPlugins;
    private NamedValueList<String,HashMap> deliveryPlugins;
    private NamedValueList<String,HashMap> converterPlugins;
    
    /**
     * Default constructor for OpenCapture Configuration class.
     * 
     * This class called when instantiating any core OpenCapture class.
     * Core OpenCapture configuration settings are loaded when this class
     * is instantiated.
     * 
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    protected OpenCaptureConfig() throws OpenCaptureException
    {
        // save oc path
        m_OCPath = OpenCaptureCommon.getRootPath();
        m_OCConfigPath =  m_OCPath + "config";

        // set config path
        if(!Path.ValidatePath(m_OCConfigPath))
            throw new OpenCaptureException("OpenCapture configuration path doesn't exist[" + m_OCConfigPath + "]");

        // set and fix oc config path.
        m_OCConfigPath = Path.FixPath(m_OCConfigPath);
        // set oc config file path & name
        m_OCConfigFile = m_OCConfigPath + OC_CONFIG_FILE_NAME;

        // validate oc config file
        if(!Path.ValidatePath(m_OCConfigFile))
            throw new OpenCaptureException("OpenCapture configuration file doesn't exist[" + m_OCConfigFile + "]");

        try
        {
            // load oc config file
            m_XmlConfig.loadDocument(m_OCConfigFile);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }
        
        // load reader plugins
        readerPlugins = readConfiguration(OC_READER_PLUGINS, "PluginID");

        // load delivery plugins
        deliveryPlugins = readConfiguration(OC_DELIVERY_PLUGINS, "PluginID");
        
        // load converter plugins
        converterPlugins = readConfiguration(OC_CONVERTER_PLUGINS, "PluginID");
    }
    
    /**
     * Read configuration section from OpenCapture configuration file.
     * @param xPath XPath to configuration to read.
     * @param keyFieldName Key field name to be used in named value list.
     * @return
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    private NamedValueList<String,HashMap> readConfiguration(String xPath, String keyFieldName) throws OpenCaptureException
    {
        NamedValueList<String,HashMap> conf = new  NamedValueList<String, HashMap>();
        List list = null;

        try
        {
            // read xml and return list of maps.
            list = m_XmlConfig.getNodeList(xPath);

            // loop through list and get each map.
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);

                // get value of key field.
                String key = (String)map.get(keyFieldName);

                conf.put(key, map);
            }
            
            return conf;
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }   
        finally
        {
            if(list != null)
                list.clear();

            list = null;
        }
    }

    /**
     * Get reader plugin class.
     * @param pulginID
     * @return Reader class.
     */
    public String getReaderClass(String pluginID)
    {
        HashMap map = (HashMap)readerPlugins.get(pluginID);
        return (String)map.get(OC_CLASS_ATTR);
    }

    /**
     * Get reader name.
     * @param pluginID
     * @return Reader name.
     */
    public String getReaderName(String pluginID)
    {
        HashMap map = (HashMap)readerPlugins.get(pluginID);
        return (String)map.get(OC_NAME_ATTR);
    }

    /**
     * Get reader description.
     * @param pluginID
     * @return Reader description.
     */
    public String getReaderDescription(String pluginID)
    {
        HashMap map = (HashMap)readerPlugins.get(pluginID);
        return (String)map.get(OC_DESCRIPTION_ATTR);
    }

    /**
     * Get delivery class.
     * @param pluginID
     * @return Delivery class.
     */
    public String getDeliveryClass(String pluginID)
    {
        HashMap map = (HashMap)deliveryPlugins.get(pluginID);
        return (String)map.get(OC_CLASS_ATTR);
    }

    /**
     * Get delivery name.
     * @param pluginID
     * @return Delivery name.
     */
    public String getDeliveryName(String pluginID)
    {
        HashMap map = (HashMap)deliveryPlugins.get(pluginID);
        return (String)map.get(OC_NAME_ATTR);
    }

    /**
     * Get delivery description.
     * @param pluginID
     * @return Delivery description.
     */
    public String getDeliveryDescription(String pluginID)
    {
        HashMap map = (HashMap)deliveryPlugins.get(pluginID);
        return (String)map.get(OC_DESCRIPTION_ATTR);
    }
    
    /**
     * Get converter classs name.
     * 
     * @param pluginID
     * 
     * @return String containing converter class name.
     */
    public String getConverterClass(String pluginID)
    {
        HashMap map = (HashMap)converterPlugins.get(pluginID);
        return (String)map.get(OC_CLASS_ATTR);        
    }
    
    /**
     * Get converter name.
     * 
     * @param pluginID
     * 
     * @return String containing converter name.
     */
    public String getConverterName(String pluginID)
    {
        HashMap map = (HashMap)converterPlugins.get(pluginID);
        return (String)map.get(OC_NAME_ATTR);
    }
    
    /**
     * Get converter desctiption.
     * 
     * @param pluginID
     * 
     * @return String containing converter description.
     */
    public String getConverterDescription(String pluginID)
    {
        HashMap map = (HashMap)converterPlugins.get(pluginID);
        return (String)map.get(OC_DESCRIPTION_ATTR);
    }
}
