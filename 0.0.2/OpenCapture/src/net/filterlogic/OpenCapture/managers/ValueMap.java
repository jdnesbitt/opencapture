/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.managers;

/**
 *
 * @author dnesbitt
 */
public class ValueMap
{
    private String id = "";
    private String type = "";
    private String value = "";
    private String keyValue = "";

    public ValueMap()
    {}
    
    public ValueMap(String id, String type, String value, String keyValue)
    {
        this.id = id;
        this.type = type;
        this.value = value;
        this.keyValue = keyValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }



}
