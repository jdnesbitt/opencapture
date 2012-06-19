package net.FilterLogic.imaging;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import net.FilterLogic.io.FileIO;
import net.FilterLogic.io.Path;
import net.FilterLogic.util.ByteHelper;

public class ToPNG implements IConversion 
{
	private static final String SUPPORTED_DOCUMENT_FORMAT = "PNG";
	private static final int DEFAULT_DPI = 0;

    private List<BufferedImage> document = null;
    private List<String> fileNames = new ArrayList<String>();
    private Properties documentProperties = null;
    private int totalPages = 0;

    private int horizontalDPI = 0;
    private int verticalDPI = 0;
    
    private byte[] byteData = null;

    /**
     * Default constructor
     */
    public ToPNG()
    {
        document = new ArrayList<BufferedImage>();
        documentProperties = new Properties();
    }
    
    /**
     * Get document's properties
     */
    public Properties getDocumentProperties() 
    {
      	return this.documentProperties;
    }
    
    /**
     * Add page.
     */
    public void addPage(BufferedImage documentPage) throws Exception
    {
        document.add(documentPage);

        totalPages = document.size();
    }

    /**
     * Delete page.
     */
    public void deletePage(int pageNumber)
    {
        if(pageNumber>0)
            pageNumber -= 1;

        document.remove(pageNumber);

        totalPages = document.size();
    }

    /**
     * Get list of buffered images.
     */
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

    public void insertPage(BufferedImage documentPage, int pageNumber) throws Exception
    {
        if(pageNumber>0)
            pageNumber -= 1;

        if(pageNumber<document.size())
            document.add(pageNumber, documentPage);
        
        totalPages = document.size();
    }
    
    public void addDocumentProperty(String propertyName, String propertyValue) throws Exception
    {
        documentProperties.setProperty(propertyName.toUpperCase(), propertyValue);
    }

    public void setDocumentProperties(Properties documentProperties) throws Exception
    {
        this.documentProperties = documentProperties;
    }
    
    /**
     * Rotate image specified degree.
     * 
     * -90.0 -  rotate 90 degrees left
     *  90.0 - rotate 90 degrees right.
     * 
     * @param image BufferedImage to rotate.
     * 
     * @return Rotated BufferedImage.
     */
    public void rotateImage(int page, double degree) throws Exception
    {
        BufferedImage image = getPage(page);
        
        if(image != null)
        {
            Image img = ImageUtils.toImage(image);
            img = ImageUtils.rotateImage(img, degree);
            image = ImageUtils.toBufferedImage(img);
            
            if(page>0)
                page -= 1;
            
            document.remove(page);
            
            document.add(page, image);
        }
    }

    /**
     * Save specified page to file.
     */
    public void savePage(String fileName, int pageNumber) throws Exception
    {
    	try
    	{
	    	// create file object for storing buff img
	    	File file = new File(fileName);
	
	    	FileOutputStream os = new FileOutputStream(file);
	
			// save page
			savePage(os, pageNumber);
    	}
    	catch(Exception e)
    	{
    		throw new Exception("Exception writing page " + pageNumber + " to " + fileName);
    	}
    }

    /**
     * Save page to output stream.
     */
    public void savePage(OutputStream os, int pageNumber) throws Exception
    {
        if(pageNumber>document.size()+1 ||
        		pageNumber<1)
        	throw new Exception("Invalid page number [" + pageNumber + "]");

        int reqPage = pageNumber-1;

        BufferedImage image = null;
        BufferedImage i2 = null;

       try
       {
    	   // get page
    	   image  = (BufferedImage)document.get(reqPage);
    	   //i2 = (BufferedImage)document.get(reqPage);
    	   //image = new BufferedImage(i2.getWidth(),i2.getHeight(),BufferedImage.TYPE_INT_RGB);
    	   //image.getGraphics().drawImage(i2, 0, 0, i2.getWidth(), i2.getHeight(), null);
    	   //image = ImageUtils.convertToRgb(i2);

    	   // write image to file
    	   if(!ImageIO.write(image, SUPPORTED_DOCUMENT_FORMAT, os))
    	   {
    		   throw new Exception("Problem writting PNG!");
    	   }
       }
       catch(Exception e)
       {
    	   throw new Exception("Exception writing page " + pageNumber);
       }
    }

    public int getPageCount()
    {
    	totalPages = document.size();

        return totalPages;
    }

    private void createBufferedImages(byte[] fileBytes) throws Exception
    {
    	boolean resize = false;
    	long RESIZE_LIMIT = 2048000;
    	int RESIZE_WIDTH = 1024;
    	int RESIZE_HEIGHT = 768;
    	long ih = 0;
    	long iw = -5;
    	long fileSize = fileBytes.length;
    	BufferedImage image = null;
    	
    	// get image type reader
//    	Iterator<ImageReader> inReaders = ImageIO.getImageReadersByFormatName(SUPPORTED_DOCUMENT_FORMAT);
//    	ImageReader reader = inReaders.next();
//
//    	// create iis object
//    	ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(fileBytes)); 

    	// if valid reader found, process image
//    	if(reader != null)
//    	{
//    		// set reader input
//	    	reader.setInput(iis);

	    	// get buff image
	        //BufferedImage image = reader.read(0);

    		if(fileBytes.length>RESIZE_LIMIT)
    			resize = true;

    		image = ImageIO.read ( new ByteArrayInputStream ( fileBytes ) );

    		//java.awt.Image img = java.awt.Toolkit.getDefaultToolkit().createImage(fileBytes);

    		// clear space
    		fileBytes = null;

//    		ih = img.getHeight(null);
//    		iw = img.getWidth(null);
//    		ih = img.getHeight(null);
    		
    		ih = image.getHeight();
    		iw = image.getWidth();
    		
    		this.documentProperties.put("IMAGEWIDTH", DEFAULT_DPI);
    		this.documentProperties.put("IMAGEHEIGHT", DEFAULT_DPI);
    		
    		//image = ImageUtils.toBufferedImage(img);

    		// resize image to 1024 by 768 if image resolution/size too high/large
    		if(resize)
    		{
    			//int[] xy = ImageUtils.calculateResize((int)iw, (int)ih, fileSize, RESIZE_LIMIT);

    			String theTime = new Date().toString();
    			this.documentProperties.put("RESIZE_START_TIME", theTime);

				if(iw > ih)
					image = ImageUtils.resizeTrick(image, RESIZE_WIDTH, RESIZE_HEIGHT);
				else
					if(ih > iw)
						image = ImageUtils.resizeTrick(image, RESIZE_HEIGHT, RESIZE_WIDTH);
					else
						image = ImageUtils.resizeTrick(image, RESIZE_WIDTH, RESIZE_WIDTH);

    			//image = ImageUtils.resizeImage(image, RESIZE_WIDTH);
    			theTime = new Date().toString();
    			this.documentProperties.put("RESIZE_FINISH_TIME", theTime);
    			
    			// resized image width/height
    			this.documentProperties.put("RESIZEDIMAGEWIDTH", image.getWidth());
    			this.documentProperties.put("RESIZEDIMAGEHEIGHT", image.getHeight());
    		}

    		// set image dpi
    		setDPI(image);

	        // add page to image list
	        addPage(image);

	        // inc page count
	        totalPages = document.size();
//    	}
//    	else
//    		throw new Exception("No supported image reader found for " + SUPPORTED_DOCUMENT_FORMAT + " image format.");
    }
    
    private void setDPI(BufferedImage image)
    {
    	float dpi = 0;
    	int w = 0;
    	int h = 0;
    	
    	try
    	{
	    	if(image != null)
	    	{
	    		w = image.getWidth();
	    		h = image.getHeight();
	    		
	    		dpi = ((float)w/h) * 100;
	    		
	    		setHorizontalDPI((int)dpi);
	    		setVerticalDPI((int)dpi);
	    		
	    		this.documentProperties.put("XRESOLUTION", String.valueOf(dpi));
	    		this.documentProperties.put("YRESOLUTION", String.valueOf(dpi));
	    	}
	    	else
	    	{
		        // set dpi to -1 to show no dpi
		        setHorizontalDPI(DEFAULT_DPI);
		        setVerticalDPI(DEFAULT_DPI);
		        
	    		this.documentProperties.put("YRESOLUTION", String.valueOf(DEFAULT_DPI));
	    		this.documentProperties.put("YRESOLUTION", String.valueOf(DEFAULT_DPI));
	    	}
    	}
    	catch(Exception e)
    	{
	        // set dpi to -1 to show no dpi
	        setHorizontalDPI(DEFAULT_DPI);
	        setVerticalDPI(DEFAULT_DPI);
	        
    		this.documentProperties.put("YRESOLUTION", String.valueOf(DEFAULT_DPI));
    		this.documentProperties.put("YRESOLUTION", String.valueOf(DEFAULT_DPI));
    	}
    }

	/* 
	 * Get a list of files that have been saved.  This method
	 * will only return file names when savePage or saveDocument methods
	 * have been called passing a file name parameter.
	 */
	public List<String> getFiles() 
	{
		return this.fileNames;
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#getHorizontalDPI()
	 */
	public int getHorizontalDPI()
	{
		return horizontalDPI;
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#getVerticalDPI()
	 */
	public int getVerticalDPI() 
	{
		return verticalDPI;
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#saveDocument(java.io.OutputStream)
	 */
	public void saveDocument(OutputStream os) throws Exception 
	{
		throw new UnsupportedOperationException("Not supported.");
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#saveDocument(java.lang.String)
	 */
	public void saveDocument(String fileName) throws Exception 
	{
		String fileNameWOExt = "";
		String filePath = "";

		try
		{
			// get name of file without extension
			fileNameWOExt = Path.getFileNameWithoutExtension(fileName);
			filePath = Path.getPath(fileName);

			for(int i=0;i<document.size();i++)
			{
				String theFile = filePath + fileNameWOExt + "-" + i + "." + SUPPORTED_DOCUMENT_FORMAT.toLowerCase();

				// save page
				savePage(theFile, i+1);

				// add file name to list
				fileNames.add(theFile);
			}
		}
		catch(Exception e)
		{
			throw new Exception("Problem saving images to file [" + fileName + "]");
		}
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#setDocument(byte[])
	 */
	public void setDocument(byte[] fileData) throws Exception 
	{
		this.byteData = ByteHelper.toBytes(fileData);
		
		createBufferedImages(fileData);
	}

	/**
	 * Get byte array containing document data.
	 */
	public byte[] getBytes() 
	{
		return this.byteData;
	}
	
	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#setDocument(java.io.InputStream)
	 */
	public void setDocument(InputStream is) throws Exception 
	{
		byte[] data = FileIO.readFileToByteArray((FileInputStream)is);
		
		setDocument(data);
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#setDocument(java.util.List)
	 */
	public void setDocument(List imageList) throws Exception 
	{
		if(document == null)
		{
			document = new ArrayList<BufferedImage>(imageList);
		}
		else
			document.addAll(imageList);

        totalPages = document.size();			
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#setDocument(java.lang.String)
	 */
	public void setDocument(String fileName) throws Exception 
	{
		byte[] data = FileIO.readFileToByteArray(fileName);

		setDocument(data);

		// add filename to list
		fileNames.add(fileName);
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#setHorizontalDPI(int)
	 */
	public void setHorizontalDPI(int dpi)
	{
		this.horizontalDPI = dpi;
	}

	/* (non-Javadoc)
	 * @see net.FilterLogic.util.imaging.IConversion#setVerticalDPI(int)
	 */
	public void setVerticalDPI(int dpi) 
	{
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
		String fileName = "C:\\Dell\\E-WTRMRK.GIF";
		
		try
		{
//			Iterator<ImageReader> inReaders = ImageIO.getImageReadersByFormatName("GIF");
//			
//			for(;inReaders.hasNext();)
//			{
//				ImageReader reader = inReaders.next();
//				
//				System.out.println("ReaderName: " + reader.getFormatName());
//			}
			
			ToPNG tg = new ToPNG();
			ToPDF tp = new ToPDF();
			
			tp.setDocument("C:\\temp\\jisql.pdf");
			
			// get image list
			tg.setDocument(tp.getBufferedImages());
			
			tg.saveDocument("C:\\temp\\PNG.png");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
