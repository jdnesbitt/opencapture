/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author dnesbitt
 */
public class Mapping
{
    private String source = "";
    private String destination = "";
    private Map<String,ValueMap> valueMapList= null;

    public Mapping()
    {
        valueMapList=new HashMap<String, ValueMap>();
    }

    public Mapping(String source, String destination)
    {
        this.source = source;
        this.destination = destination;
        valueMapList=new HashMap<String, ValueMap>();
    }

    public Mapping(String source, String destination, Map<String,ValueMap> valueMap)
    {
        this.source = source;
        this.destination = destination;
        this.valueMapList = valueMapList;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String,ValueMap> getValueMapList() {
        return valueMapList;
    }

    public void setValueMapList(Map<String,ValueMap> valueMapList) {
        this.valueMapList = valueMapList;
    }

    public void addValueMap(ValueMap valueMap)
    {
        if(valueMapList == null)
            valueMapList = new HashMap<String, ValueMap>();

        valueMapList.put(valueMap.getId(),valueMap);
    }

    public ValueMap findValueMap(String id)
    {
        ValueMap vm = valueMapList.get(id);

        return vm;
    }
}
