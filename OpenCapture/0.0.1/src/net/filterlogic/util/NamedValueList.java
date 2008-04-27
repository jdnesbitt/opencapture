/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author D106931
 */
public class NamedValueList<K,V> extends HashMap
{
    private ArrayList<Object> nameList;

    public NamedValueList()
    {
        nameList = new ArrayList<Object>();
    }

    @Override
    public Object put(Object key, Object value) 
    {
        // if key doesn't exists, add to list.
        if(!super.containsKey(key))
        {
            nameList.add(key);
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
        nameList.clear();
        super.clear();
    }

    @Override
    public Object clone() 
    {
        return this.clone();
        //return super.clone();
    }
    
    public List getOrderedNameList()
    {
        return this.nameList;
    }
}
