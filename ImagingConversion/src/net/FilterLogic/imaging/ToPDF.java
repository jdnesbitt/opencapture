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

//import com.ibm.xml.resolver.apps.xparse;
import com.itextpdf.text.Document;
//import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
//import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.Graphics2D;
//import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import net.FilterLogic.io.FileIO;
import net.FilterLogic.util.ByteHelper;

import org.jpedal.PdfDecoder;

/**
 * This class is used to convert to, insert, or delete pages from a PDF
 * document.  The following tag properties are supported:
 *
 * Title
 * Author
 * CreationDate
 * Subject
 * Keywords
 * 
 * ScaleToFit			- true or false
 * Auto-Center			- true or false
 * Auto-Orientation		- true or false
 * 
 * @author Darron Nesbitt
 */
public class ToPDF implements IConversion
{
    private static final String SUPPORTED_DOCUMENT_FORMAT = "PDF";

    private List<BufferedImage> document = null;
    private List<String> fileNames = new ArrayList<String>();
    private Properties documentProperties = null;
    private int totalPages = 0;

    private int horizontalDPI = 0;
    private int verticalDPI = 0;

    private byte[] byteData = null;
    
    private boolean multiPage = false;
    
    public static final String KEY_SCALE_TO_FIT = "SCALETOFIT";
    private static final String KEY_SCALE_TO_FIT_DEFAULT = "true";

    public static final String KEY_AUTO_ORIENTATION = "AUTO-ORIENTATION";
    private static final String KEY_AUTO_ORIENTATION_DEFAULT = "true";

    public static final String KEY_AUTO_CENTER = "AUTO-CENTER";
    private static final String KEY_AUTO_CENTER_DEFAULT = "true";
    
    private static PdfDecoder sdecoder = new PdfDecoder();
    /**
     * Default constructor.
     */
    public ToPDF()
    {
        document = new ArrayList<BufferedImage>();
        documentProperties = new Properties();
    }
    
    /**
     * Get document's properties
     */
    public Properties getDocumentProperties() 
    {
    	if(this.documentProperties == null)
    		this.documentProperties = new Properties();

      	return this.documentProperties;
    }
    
    public void addDocumentProperty(String propertyName, String propertyValue) throws Exception
    {
        documentProperties.setProperty(propertyName.toUpperCase(), propertyValue);
    }

    public void addPage(BufferedImage documentPage) throws Exception
    {
        document.add(documentPage);

        totalPages += 1;
    }

    public void deletePage(int pageNumber)
    {
        if(pageNumber>0)
            pageNumber -= 1;

        document.remove(pageNumber);

        totalPages =- 1;
    }

    public List<BufferedImage> getBufferedImages()
    {
        return document;
    }

    public String getDocumentProperty(String propertyName) throws Exception
    {
        return documentProperties.getProperty(propertyName);
    }

    public String getFormat()
    {
        return SUPPORTED_DOCUMENT_FORMAT;
    }

    public BufferedImage getPage(int pageNumber) throws Exception
    {
        if(pageNumber>0)
            pageNumber -= 1;

        return document.get(pageNumber);
    }

    public int getPageCount()
    {
        return totalPages;
    }

    public void insertPage(BufferedImage documentPage, int pageNumber) throws Exception
    {
        if(pageNumber>0)
            pageNumber -= 1;

        if(pageNumber<document.size())
            document.add(pageNumber, documentPage);
    }

    public void saveDocument(String fileName) throws Exception
    {
        writeMultiPagePDF(fileName);
    }

    public void saveDocument(OutputStream os) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void savePage(String fileName, int pageNumber) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void savePage(OutputStream os, int pageNumber) throws Exception
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDocument(String fileName) throws Exception
    {
        byte[] data = FileIO.readFileToByteArray(fileName);

        setDocument(data);
    }

    public void setDocument(byte[] fileData) throws Exception
    {
    	//this.byteData = ByteHelper.toBytes(fileData);
        this.byteData = fileData;
    	
        createBufferedImages(fileData);
    }

    public void setDocument(List imageList)
    {
		if(document == null)
		{
			document = new ArrayList<BufferedImage>(imageList);
		}
		else
			document.addAll(imageList);

        totalPages = document.size();	
    }

    public void setDocument(InputStream is) throws Exception
    {
        byte[] data = FileIO.readFileToByteArray((FileInputStream)is);

        setDocument(data);
    }
    
	/**
	 * Get byte array containing document data.
	 */
	public byte[] getBytes() 
	{
		return this.byteData;
	}

    public void setDocumentProperties(Properties documentProperties) throws Exception
    {
        this.documentProperties = documentProperties;
    }

    private void createBufferedImages(byte[] fileBytes) throws Exception
    {
        int pageCount = getPDFPageCount(fileBytes);
        // create empty prop object
        this.documentProperties = new Properties();
       
        totalPages = pageCount;

        for(int i=0;i<pageCount;i++)
        {
        	System.out.println("Getting page " + i + " @ " + new Date().toString());
            // image object
            BufferedImage image = loadPDF(fileBytes, i);

            // add page to image list
            addPage(image);
        }
    }

    /**
     * Get number of pages in a PDF.
     *
     * @param byteArray Array containing PDF file contents.
     *
     * @return Number of pages in PDF.
     */
    private int getPDFPageCount(byte[] byteArray) throws Exception
    {
        int pc = 0;
        try
        {
            PdfDecoder pdfDecoder = new PdfDecoder(true);

            pdfDecoder.openPdfArray(byteArray);

            pc = pdfDecoder.getPageCount();

            return pc;
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }

    private BufferedImage loadPDF(byte[] byteArray, int imageToLoad) throws Exception
    {
        BufferedImage image = null;


        try
        {
            PdfDecoder pdfDecoder = new PdfDecoder();
//com.itextpdf.text.Image.getInstance(, null);
            pdfDecoder.openPdfArray(byteArray);

            image = pdfDecoder.getPageAsImage(imageToLoad+1);
            
            return image;
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }

    public List<BufferedImage> getBufferedImagesAsBW() throws Exception
    {
        List<BufferedImage> bi = new ArrayList<BufferedImage>();
        
        for(int i=0;i<this.document.size();i++)
        {
            BufferedImage image = this.document.get(i);
            
            image = processImage(image);
            
            bi.add(image);
        }
        
        return bi;
    }
    public BufferedImage processImage(BufferedImage image) {

        BufferedImage newImage=null;


            //black and white conversion
            newImage=new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D newG2bw=newImage.createGraphics();
            newG2bw.setPaint(Color.WHITE);
            newG2bw.fillRect(0,0,image.getWidth(), image.getHeight());
            newG2bw.drawImage(image,0,0,null);

            //grayscale conversion
//            newImage=new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//            Graphics2D newG2=newImage.createGraphics();
//            newG2.setPaint(Color.WHITE);
//            newG2.fillRect(0,0,image.getWidth(), image.getHeight());
//            newG2.drawImage(image,0,0,null);



        return newImage;
    }
    
    /**
     * Iterate through properties and set each value for PDF document.
     */
    private void setDocumentProperties(Document pdf)
    {


        for(Enumeration e = this.documentProperties.keys();e.hasMoreElements();)
        {
            String name = e.nextElement().toString().toUpperCase();
            String value = this.documentProperties.getProperty(name);

            if(name.equals("TITLE"))
                pdf.addTitle(value);

            if(name.equals("AUTHOR"))
                pdf.addAuthor(value);

//            if(name.equals("CREATOR"))
//                pdf.addCreator(value);

            if(name.equals("CREATIONDATE"))
                pdf.addCreationDate();

            if(name.equals("SUBJECT"))
                pdf.addSubject(value);

            if(name.equals("KEYWORDS"))
                pdf.addKeywords(value);
        }

    }

	public List<String> getFiles() 
	{
    	return fileNames;
	}
    
    private void writeMultiPagePDF(String fileName) throws Exception
    {
        float STD_WIDTH = 620;
        float STD_HEIGHT = 775;
        float newWidth = 0;
        float newHeight = 0;
        float xPos = 0;
        float yPos = 0;
        
        boolean scaleImage = true;
        boolean pdfAutoOrientation = true;
        boolean autoCenter = false;
        boolean portrait = true;

        BufferedImage img = null;
        Document pdf;

        PdfWriter writer;

        try
        {
        	// scale image
        	String si = this.documentProperties.getProperty(KEY_SCALE_TO_FIT, KEY_SCALE_TO_FIT_DEFAULT);
        	scaleImage = Boolean.parseBoolean(si);

        	// auto-orientation
        	String ao = this.documentProperties.getProperty(KEY_AUTO_ORIENTATION,KEY_AUTO_ORIENTATION_DEFAULT);
        	pdfAutoOrientation = Boolean.parseBoolean(ao);

        	// auto-center
        	String ac = this.documentProperties.getProperty(KEY_AUTO_CENTER, KEY_AUTO_CENTER_DEFAULT);
        	autoCenter = Boolean.parseBoolean(ac);

            if(document.size()>0)
                img = document.get(0);

            if(img != null)
            {
           	
                // if dpi set, calculate new width/height
                if(horizontalDPI>0 && verticalDPI>0)
                {
                    float xd = (float)horizontalDPI / 100;
                    float yd = (float)verticalDPI / 100;

                    newWidth = img.getWidth() / xd;
                    newHeight = img.getHeight() / yd;
                }
                else
                {
                    newWidth = img.getWidth();
                    newHeight = img.getHeight();
                }

                // if image width or height changed, scale
                if(newWidth != img.getWidth() || newHeight != img.getHeight())
                	scaleImage = true;

                // if auto orientation, set portrait or landscape 
                if(pdfAutoOrientation)
                {
	                if(newWidth >= newHeight)
	                {
	                    pdf = new Document(PageSize.LETTER.rotate());
	                    portrait = false;
	                }
	                else
	                {
	                    pdf = new Document(PageSize.LETTER);
	                    portrait = true;
	                }
                }
                else
                {
                	// else, always portrait
                	pdf = new Document(PageSize.LETTER);
                	portrait = true;
                }

                writer = PdfWriter.getInstance(pdf,new FileOutputStream(fileName));

                writer.setFullCompression();

                pdf.open();

                // set document props
                setDocumentProperties(pdf);

                int t = 0;
                
            	float pdfPageWidth = pdf.getPageSize().getWidth();
            	float pdfPageHeight = pdf.getPageSize().getHeight();

                // if new image larger than standard size, override and enable image scaling
                if(newWidth > pdfPageWidth || newHeight > pdfPageHeight)
                {
                	scaleImage = true;
                	
                	if(newWidth > pdfPageWidth)
                		newWidth = pdfPageWidth;

                	if(newHeight > pdfPageHeight)
                		newHeight = pdfPageHeight;
                }

                // break out each page to single file
                while(t<totalPages)
                {
                	PdfContentByte cb = writer.getDirectContent();
                    com.itextpdf.text.Image pdfImage ;

                    if (img != null)
                    {
                        pdfImage = com.itextpdf.text.Image.getInstance(img,null);

                        // calculate center
                    	if(autoCenter)
                    	{
                    		if(portrait)
                    		{
	                    		xPos = (pdfPageWidth - newWidth) /2;
	                    		yPos = (pdfPageHeight - newHeight) /2;
                    		}
                    		else
                    		{
	                    		//xPos = ((pdfPageHeight * (float)1.60) - newWidth) / 2;
                    			xPos = (pdfPageHeight - newWidth) / 2;
	                    		yPos = (pdfPageWidth - newHeight) / 2;
                    		}
                    	}
                    	else
                    	{
                    		// if not scaling, set image to top left
                    		if(!scaleImage)
                    		{
	                    		xPos = 0;
	                    		// calculate top left corner
	                    		yPos = pdfPageWidth - newHeight;                    			
                    		}
                    		else
                    		{
	                    		xPos = 0;
	                    		yPos = 0;
                    		}
                    	}

                    	// check if x and y pos >=0
                    	if(xPos<0)
                    		xPos = 0;
                    	if(yPos<0)
                    		yPos = 0;

                        if(scaleImage)
                        {

	                        if(!portrait)
	                        {
	                            pdfImage.scaleToFit(newHeight,newWidth);
	                        }
	                        else
	                        {
	                            pdfImage.scaleToFit(newWidth,newHeight);
	                        }

	                        // check is scaled height/width match new width/height
	                        // if not, recalculate center if autcenter enabled.
	                        if(newWidth != pdfImage.getScaledWidth() || newHeight != pdfImage.getScaledHeight())
	                        {
	                        	newWidth = pdfImage.getScaledWidth();
	                        	newHeight = pdfImage.getScaledHeight();

	                        	// calculate center
	                        	if(autoCenter)
	                        	{
    	                    		xPos = (pdfPageWidth - newWidth) /2;
    	                    		yPos = (pdfPageHeight - newHeight) /2;

	    	                    	// check if x and y pos >=0
		                        	if(xPos<0)
		                        		xPos = 0;
		                        	if(yPos<0)
		                        		yPos = 0;
	                        	}
	                        }

	                        pdfImage.setAbsolutePosition(xPos,yPos);
                        }
                        else
                        {
                        	pdfImage.setAbsolutePosition(xPos, yPos);
                        }

                        cb.addImage(pdfImage);

                        // inc counter
                        ++t;

                        if(t<totalPages)
                        {
                            img = document.get(t);
                            
                            // if dpi set, calculate new width/height
                            if(horizontalDPI>0 && verticalDPI>0)
                            {
                                float xd = (float)horizontalDPI / 100;
                                float yd = (float)verticalDPI / 100;

                                newWidth = img.getWidth() / xd;
                                newHeight = img.getHeight() / yd;
                            }
                            else
                            {
                                newWidth = img.getWidth();
                                newHeight = img.getHeight();
                            }

                            // if auto orientation, set portrait or landscape 
                            if(pdfAutoOrientation)
                            {
            	                if(newWidth >= newHeight)
            	                {
            	                    pdf.setPageSize(PageSize.LETTER.rotate());
            	                    portrait = false;
            	                }
            	                else
            	                {
            	                    pdf.setPageSize(PageSize.LETTER);
            	                    portrait = true;
            	                }
                            }
                            else
                            {
                            	// else, always portrait
                            	pdf.setPageSize(PageSize.LETTER);
                            	portrait = true;
                            }

                            // create new page.  must happen after setting page orientation
                            pdf.newPage();
                            writer.newPage();

                            // get new pages width/height
                        	pdfPageWidth = pdf.getPageSize().getWidth();
                        	pdfPageHeight = pdf.getPageSize().getHeight();
                        	
                        	// set width/height to something normal
                        	if(newWidth > pdfPageWidth)
                        		newWidth = pdfPageWidth;

                        	if(newHeight > pdfPageHeight)
                        		newHeight = pdfPageHeight;
                        }
                    }

                }

                pdf.close();
                
                // add file name to list
                fileNames = new ArrayList<String>();
                fileNames.add(fileName);
            }
        }
        catch(Exception e)
        {
            throw new Exception(e);
        }
    }

    public int getHorizontalDPI()
    {
        return horizontalDPI;
    }

    public int getVerticalDPI()
    {
        return verticalDPI;
    }

    public void setHorizontalDPI(int dpi) {
        this.horizontalDPI = dpi;
    }

    public void setVerticalDPI(int dpi) {
        this.verticalDPI = dpi;
    }

	@Override
	protected void finalize() throws Throwable 
	{
		try
		{
			this.document.clear();
			this.document = null;
			
			this.fileNames.clear();
			this.fileNames = null;
			
			this.byteData = null;
			
			super.finalize();
		}
		catch(Exception e)
		{
			// do nothing
			e.printStackTrace();
		}
	}

    public static void main(String[] args)
    {
        try
        {
        	System.out.println(java.lang.Runtime.getRuntime().maxMemory());
        	System.out.println("Start: " + new Date().toString());
        	
        	//ToGIF tg = new ToGIF();
        	ToJPG tg = new ToJPG();
        	ToTIF tt = new ToTIF();
            ToPDF tp = new ToPDF();

            //tg.setDocument("C:\\temp\\1010907-0.gif");
            //tg.setDocument("C:\\temp\\1010690-0.gif");
            //tg.setDocument("C:\\temp\\1010850-0.jpg");
            //tt.setDocument("c:\\images\\2.tif");
            tt.setDocument("C:\\temp\\1010965-0.tif");

            //tp.setHorizontalDPI(tg.getHorizontalDPI());
            //tp.setVerticalDPI(tg.getVerticalDPI());
            //tt.saveDocument("C:\\temp\\test.tif");
            System.out.println(tt.getHorizontalDPI() + " / " + tt.getVerticalDPI());

            tp.setHorizontalDPI(tt.getHorizontalDPI());
            tp.setVerticalDPI(tt.getVerticalDPI());

            tp.setDocument(tt.getBufferedImages());

            tg = null;

            //tp.setDocument("C:\\Temp\\1010673-0.pdf");
            tp.addDocumentProperty(ToPDF.KEY_SCALE_TO_FIT, "false");
            tp.addDocumentProperty(ToPDF.KEY_AUTO_CENTER, "true");
            tp.addDocumentProperty(ToPDF.KEY_AUTO_ORIENTATION, "false");

            tp.saveDocument("C:\\temp\\1010907-0.gif.pdf");

            System.out.println("Finished: " + new Date().toString());

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
