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

    public ValueMap()
    {}
    
    public ValueMap(String id, String type, String value)
    {
        this.id = id;
        this.type = type;
        this.value = value;
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

}
