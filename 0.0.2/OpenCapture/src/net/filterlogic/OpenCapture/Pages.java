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
        pages.put(String.valueOf(page.getPageNumber()),page);
    }
    
    public Pages()
    {
        
    }
    
    /**
     * Page constructor.
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
                int pageNumber = map.get("PageNumber")!=null ? Integer.parseInt((String)map.get("PageNumber")) : i;
                int sequenceNumber = map.get("Sequence")!=null ? Integer.parseInt((String)map.get("Sequence")) : i;

                // create and fill ndx field object
                Page page = new Page(pageName,pageNumber, sequenceNumber);

                // add ndx field to hash
                pages.put(String.valueOf(pageNumber), page);
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
    public void addPage(Page page) throws OpenCaptureException
    {
        if(page.getPageNumber()<=0)
            throw new OpenCaptureException("Invalid Page object.  PageNumber must be set.");

        pages.put(String.valueOf(page.getPageNumber()), page);
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
     * Get specified page number.
     * @param pageNumber
     * @return Page object. Null returned if page number doesn't exist.
     */
    public Page getPage(int pageNumber)
    {
        String v = (String)pages.getOrderedNameList().get(pageNumber);
        return (Page)pages.get(v);
    }

    /**
     * Get page using page number as an id.
     * 
     * @return Populated page object if id exists, else empty page object.
     */
    public Page getPage(String pageID)
    {
        return pages.get(pageID)!=null ? (Page)pages.get(pageID) : new Page();
    }

    /**
     * Get page using page sequence number.
     *
     * @param sequenceNumber
     *
     * @return Populated page object if sequence valid, else empty page object returned.
     */
    public Page getPageBySequence(int sequenceNumber)
    {
        Page page = null;
        boolean pageFound = false;

        // if sequence greater than number of pages
        if(sequenceNumber>this.pages.size())
            page = new Page();
        else
        {
            List<String> pageList = this.pages.getOrderedNameList();

            for(int i=0;i<this.pages.size();i++)
            {
                // get page name
                String pageName = pageList.get(i);
                // get page object
                page = (Page)this.pages.get(pageName);

                // check sequence
                if(page.getSequenceNumber() == sequenceNumber)
                {
                    pageFound = true;
                    break;
                }

            }
        }

        // if page not found, create empty page object
        if(!pageFound)
            page = new Page();

        return page;

    }
    
    /**
     * Get list of file names
     * 
     * @return List of file names without path.
     */
    public List getPageFileList()
    {
        return pages.getOrderedNameList();
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

            if(page != null)
                xml += "<Page Name=\"" + page.getName() + "\" PageNumber=\"" + String.valueOf(page.getPageNumber()) + "\" Sequence=\"" + String.valueOf(page.getSequenceNumber()) + "\" />\n";
        }

        return xml;
    }
}
