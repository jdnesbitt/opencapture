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

/**
 * Page object contains information on each scanned/imported page
 * in the OpenCapture system.
 * 
 * @author Darron Nesbitt <jd_nesbitt@hotmail.com>
 */
public class Page 
{
    private String name = "";
    private int pageNumber = 0;
    private int sequenceNumber = 0;

    /**
     * Default constructor.
     */
    public Page()
    {}
    
    /**
     * Page constructor
     * @param name File name of page(relative path).
     * @param pagenum Page number.
     * @param seqnum Sequence number of page(order of page in pages list).
     */
    public Page(String name, int pagenum, int seqnum)
    {
        this.name = name;
        this.pageNumber = pagenum;
        this.sequenceNumber = seqnum;
    }

    public Page(String name)
    {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
