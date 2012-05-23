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
package net.filterlogic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
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

    /**
     * Put new item on list at specified index in ordered list.
     * 
     * @param key
     * @param value
     * @param index
     * @return
     */
    public Object put(Object key, Object value, int index)
    {
        // if key doesn't exists, add to list.
        if(!super.containsKey(key))
        {
            nameList.add(index, key);
        }

        return super.put(key, value);
    }

    @Override
    public Object get(Object key) 
    {
        return super.get(key);
    }

    @Override
    public Object remove(Object key) 
    {

        for(int i=0;i<nameList.size();i++)
        {
            String name = (String)nameList.get(i);

            if(name.equals(key.toString()))
            {
                nameList.remove(i);
                break;
            }
        }
        
        return super.remove(key);
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
