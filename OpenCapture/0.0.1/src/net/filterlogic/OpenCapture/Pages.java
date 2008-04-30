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

import java.util.List;
import java.util.HashMap;
import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.NamedValueList;

/**
 *
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
public class Pages 
{
    private NamedValueList<String,Page> pages = new NamedValueList<String, Page>();
    
    /**
     * Constructor to add a page to pages object.
     * @param page Page to add.
     */
    public Pages(Page page)
    {
        pages.put(page.getName(),page);
    }
    
    /**
     * Pagte constructor.
     * @param batch Batch
     * @param xPath XPath to pages section.
     */
    public Pages(XMLParser batch, String xPath) throws OpenCaptureException
    {
        try
        {
            List list = batch.getNodeList(xPath);
            
            for(int i=0;i<list.size();i++)
            {
                HashMap map = (HashMap)list.get(i);
                
                String pageName = (String)map.get("Name");

                // create and fill ndx field object
                Page page = new Page(pageName);

                // add ndx field to hash
                pages.put(pageName, page);
            }
        }
        catch(Exception e)
        {
            throw new OpenCaptureException(e.toString());
        }  
    }
    
    /**
     * Move page (change page order);
     * @param currentSequence Current page index.
     * @param newSequence New page index.
     * @throws net.filterlogic.OpenCapture.OpenCaptureException
     */
    public void MovePage(int currentSequence, int newSequence) throws OpenCaptureException
    {
        try
        {
            String name = (String)pages.getOrderedNameList().remove(currentSequence);

            pages.getOrderedNameList().add(newSequence, name);
        }
        catch(Exception e)
        {
            throw new OpenCaptureException("Unable to move page[" + currentSequence + "]. " + e.toString());
        }
    }

    /**
     * Add page to pages object.
     * @param page Page to add.
     */
    public void addPage(Page page)
    {
        pages.put(page.getName(), page);
    }

    /**
     * Delete named page from pages collection.
     * @param pageName Name of page to delete.
     * @return Page named.  Returns an empty page object if page doesn't exist.
     */
    public Page deletePage(String pageName)
    {
        return pages.containsKey(pageName) ? (Page)pages.remove(pageName) : new Page();
    }
    
    /**
     * Number of pages.
     * @return Integer of number of pages.
     */
    public int Count()
    {
        return pages.size();
    }
    
    /**
     * Return batch xml.
     * @return String containing XML.
     */
    public String getXML()
    {
        String xml = "";
        
        List list = pages.getOrderedNameList();
        
        for(int i=0;i<list.size();i++)
        {
            String name = (String)list.get(i);
            Page page = (Page)pages.get(name);

            xml += "<Page Name=\"" + page.getName() + "\" />\n";
        }

        return xml;
    }
}
