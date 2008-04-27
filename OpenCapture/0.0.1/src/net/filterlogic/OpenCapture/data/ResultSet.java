/**
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.data;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 *
 * @author d106931
 */
public class ResultSet extends HashMap
{

    private ArrayList<Object> fieldList;
    
    public ResultSet(Object resultSet)
    {
        fieldList = new ArrayList<Object>();
    }
    
    @Override
    public Object put(Object key, Object value) 
    {
        // if key doesn't exists, add to list.
        if(!super.containsKey(key))
        {
            fieldList.add(key);
        }

        return super.put(key, value);
    }

    @Override
    public Object get(Object key) 
    {
        return super.get(key);
    }

    @Override
    public void clear() 
    {
        fieldList.clear();
        super.clear();
    }

    @Override
    public Object clone() 
    {
        return this.clone();
        //return super.clone();
    }
    
    public List getFieldList()
    {
        return this.fieldList;
    }
}
