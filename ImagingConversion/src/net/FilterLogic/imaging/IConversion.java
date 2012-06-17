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

package net.FilterLogic.imaging;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

/**
 * Provides a common interface for performing document
 * conversions.
 *
 * @author Darron Nesbitt
 */
public interface IConversion
{
    /**
     * Rotate image specified degree.
     * 
     * @param image BufferedImage to rotate.
     * @param degree Number of degrees to rotate image.
     * 
     * @return Rotated BufferedImage.
     */
    public BufferedImage rotateImage(BufferedImage image, double degree);
    
    /**
     * Get List containing all file names after saveDocument called.
     * 
     * @return List of String file names.
     */
    public List<String> getFiles();

    /**
     * Returns the extension of the document format handled by
     * this component.
     *
     * @return String
     */
    public String getFormat();

    /**
     * Set the document to load.
     *
     * @param fileName
     */
    public void setDocument(String fileName) throws Exception;

    /**
     * Set document to load.
     *
     * @param fileData
     */
    public void setDocument(byte[] fileData) throws Exception;

    /**
     * Set document to load.
     *
     * @param is
     */
    public void setDocument(InputStream is) throws Exception;

    /**
     * Set document to List of BufferedImage objects.
     * 
     * @param imageList
     * 
     * @throws Exception
     */
    public void setDocument(List imageList) throws Exception;

    /**
     * Get specified page number.
     *
     * @param pageNumber
     *
     * @return BufferedImage containing specified page.
     */
    public BufferedImage getPage(int pageNumber) throws Exception;

    /**
     * Add page to current document.
     *
     * @param documentPage
     *
     * @throws Exception
     */
    public void addPage(BufferedImage documentPage) throws Exception;

    /**
     * Delete page from current document.
     * 
     * @param pageNumber
     */
    public void deletePage(int pageNumber) throws Exception;

    /**
     * Insert page into current document.
     *
     * @param documentPage
     * @param pageNumber
     *
     * @throws Exception
     */
    public void insertPage(BufferedImage documentPage, int pageNumber) throws Exception;

    /**
     * Add document property in current doucment such as DPI.
     *
     * @param propertyName
     * @param propertyValue
     *
     * @throws Exception
     */
    public void addDocumentProperty(String propertyName, String propertyValue) throws Exception;

    /**
     * Set document properties for current doucment.
     *
     * @param documentProperties
     * 
     * @throws Exception
     */
    public void setDocumentProperties(Properties documentProperties) throws Exception;

    /**
     * Get document property value.
     *
     * @param propertyName
     *
     * @return String containing property value.
     *
     * @throws Exception
     */
    public String getDocumentProperty(String propertyName) throws Exception;
    
    /**
     * Get document's properties.
     * 
     * @return Properties object containing all of the document's properties.
     */
    public Properties getDocumentProperties();

    /**
     * Save current document.
     *
     * @param fileName
     *
     * @throws Exception
     */
    public void saveDocument(String fileName) throws Exception;

    /**
     * Save current document.
     *
     * @param os
     *
     * @throws Exception
     */
    public void saveDocument(OutputStream os) throws Exception;

    /**
     * Save specified page to file name.
     *
     * @param fileName
     * @param pageNumber
     *
     * @throws Exception
     */
    public void savePage(String fileName, int pageNumber) throws Exception;

    /**
     * Save specified page using output stream.
     *
     * @param os
     * @param pageNumber
     * 
     * @throws Exception
     */
    public void savePage(OutputStream os, int pageNumber) throws Exception;

    /**
     * Get number of pages in document.
     *
     * @return int containing number of pages.
     */
    public int getPageCount();

    /**
     * Get a List of BufferedImage objects after document has been loaded.
     *
     * @return List of BufferedImage objects.
     */
    public List<BufferedImage> getBufferedImages();
    
    /**
     * Get byte array containing document data.
     * 
     * @return byte array
     */
    public byte[] getBytes();

    /**
     * Get the horizontal (X) DPI.
     *
     * @return int
     */
    public int getHorizontalDPI();

    /**
     * Get the vertical (Y) DPI.
     *
     * @return int
     */
    public int getVerticalDPI();

    /**
     * Set the horizontal DPI.
     *
     * @param dpi
     */
    public void setHorizontalDPI(int dpi);

    /**
     * Set the vertical DPI.
     * 
     * @param dpi
     */
    public void setVerticalDPI(int dpi);

}
