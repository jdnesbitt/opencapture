/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.filterlogic.util.xml.XMLParser;
import org.w3c.dom.Node;

/**
 *
 * @author dnesbitt
 */
public class MappingFile
{
    private XMLParser xmlParser=null;
    private String trigger = "";
    private String delimiter = "";
    private int skipLines = 0;
    private boolean triggerValuesSet = false;

    private static String TRIGGER_CONTENT_TYPE_XPATH = "/MappingFile/Trigger/@ContentType";
    private static String TRIGGER_DELIMITER_XPATH = "/MappingFile/Trigger/@Delimiter";
    private static String TRIGGER_SKIP_LINES_XPATH = "/MappingFile/Trigger/@SkipFirstLines";

    private static String MAPPINGS_NODE_LIST_XPATH = "/MappingFile/Mappings/Mapping";
    private static String MAPPING_SOURCE_XPATH = "Source";
    private static String MAPPING_DESTINATION_XPATH = "Destination";

    private static String VALUE_MAPPINGS_NODE_LIST_XPATH = "ValueMappings/ValueMap";
    private static String VALUE_MAPPING_ID_XPATH = "@ID";
    private static String VALUE_MAPPING_TYPE_XPATH = "@Type";
    private static String VALUE_MAPPING_VALUE_XPATH = "@Value";

    private List<Mapping> mappings;

    public MappingFile(String fileName) throws Exception
    {
        if(new File(fileName).exists())
        {
            xmlParser = new XMLParser();
            xmlParser.loadDocument(fileName);
        }
        else
            throw new Exception("MappingFile doesn't exist: " + fileName);

        mappings = new ArrayList<Mapping>();

        setTriggerInfo();

        setMappings();
    }

    /**
     * Set trigger values
     */
    private void setTriggerInfo()
    {
        // get trigger file type
        trigger = xmlParser.getValue(TRIGGER_CONTENT_TYPE_XPATH, "").toUpperCase();
        // get field delimiter.
        delimiter = xmlParser.getValue(TRIGGER_DELIMITER_XPATH, "");

        // get number lines to skip in trigger file
        String skip = xmlParser.getValue(TRIGGER_SKIP_LINES_XPATH, "0");
        skipLines = Integer.parseInt(skip);

        triggerValuesSet = true;

    }

    private void setMappings() throws Exception
    {
        List list = xmlParser.getNodeList(MAPPINGS_NODE_LIST_XPATH,false);
        List valueMaps;

        for(int i=0;i<list.size();i++)
        {
            Node node = (Node)list.get(i);

            String source = xmlParser.getValue(MAPPING_SOURCE_XPATH, node, "");
            String destination = xmlParser.getValue(MAPPING_DESTINATION_XPATH, node, "");

            Mapping mapping = new Mapping(source, destination);

            try
            {
                valueMaps = xmlParser.getNodes(VALUE_MAPPINGS_NODE_LIST_XPATH, node);

                for(int v=0;v<valueMaps.size();v++)
                {
                    Node vmNode = (Node)valueMaps.get(v);

                    String id = xmlParser.getValue(VALUE_MAPPING_ID_XPATH, vmNode);
                    String type = xmlParser.getValue(VALUE_MAPPING_TYPE_XPATH, vmNode);
                    String value = xmlParser.getValue(VALUE_MAPPING_VALUE_XPATH, vmNode);

                    ValueMap valueMap = new ValueMap(id, type, value);
                    // add value map to mapping
                    mapping.addValueMap(valueMap);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                valueMaps = new ArrayList();
            }

            // add mapping to mappings list
            addMapping(mapping);
        }
    }

    public void addMapping(Mapping mapping)
    {
        if(mappings == null)
            mappings = new ArrayList<Mapping>();

        mappings.add(mapping);
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public List<Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public long getSkipLines() {
        return skipLines;
    }

    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public boolean isTriggerValuesSet() {
        return triggerValuesSet;
    }

    public void setTriggerValuesSet(boolean triggerValuesSet) {
        this.triggerValuesSet = triggerValuesSet;
    }

    /**
     * Find mapping by destination
     * 
     * @param destination
     * @return
     */
    public Mapping findMappingByDestination(String destination)
    {
        for(int i=0;i<mappings.size();i++)
        {
            Mapping mapping = mappings.get(i);

            if(mapping.getDestination().toUpperCase().equals(destination.toUpperCase()))
                return mapping;
        }

        return new Mapping();
    }

    /**
     * Find mapping by source.
     * 
     * @param source
     * @return
     */
    public Mapping findMappingBySource(String source)
    {
        for(int i=0;i<mappings.size();i++)
        {
            Mapping mapping = mappings.get(i);

            if(mapping.getSource().toUpperCase().equals(source.toUpperCase()))
                return mapping;
        }

        return new Mapping();
    }

    public static void main(String[] args)
    {
        try
        {
            MappingFile mf = new MappingFile("C:\\OCData\\Templates\\TripPakMappingTemplate.xml");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

}
