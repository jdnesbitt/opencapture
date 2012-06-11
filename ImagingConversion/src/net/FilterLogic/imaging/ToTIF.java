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

//import com.ibm.converters.UnsignedMath;
//import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
//import com.sun.media.imageio.stream.RawImageInputStream;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.ImageCodec;
//import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFDirectory;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.TIFFField;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
import java.util.ArrayList;
//import java.util.Date;
import java.util.Enumeration;
//import java.util.Iterator;
import java.util.List;
import java.util.Properties;
//import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
//import javax.imageio.ImageReader;
//import javax.imageio.ImageWriter;
//import javax.imageio.metadata.IIOMetadata;
//import javax.imageio.stream.FileImageInputStream;
//import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.*;
import net.FilterLogic.io.FileIO;
import net.FilterLogic.io.Path;
import net.FilterLogic.util.ByteHelper;

/**
 * This class is used to convert, insert, or delete pages from a TIF
 * image.  The following TIF tag properties are supported:
 *
 * ResolutionUnit
 * XResolution
 * YResolution
 * BitsPerSample
 * RowsPerStrip
 * PhotometricInterpretation
 * FillOrder
 * Compression
 *      COMPRESSION_NONE
 *      COMPRESSION_PACKBITS
 *      COMPRESSION_GROUP3_1D
 *      COMPRESSION_GROUP3_2D
 *      COMPRESSION_GROUP4
 *      COMPRESSION_LZW
 *      COMPRESSION_JPEG_TTN2
 *      COMPRESSION_DEFLATE
 *
 * ImageDescription
 * Orientation
 *      ORIENTATION_TOPLEFT = 1;
 *      ORIENTATION_TOPRIGHT = 2;
 *      ORIENTATION_BOTRIGHT = 3;
 *      ORIENTATION_BOTLEFT = 4;
 *      ORIENTATION_LEFTTOP = 5;
 *      ORIENTATION_RIGHTTOP = 6;
 *      ORIENTATION_RIGHTBOT = 7;
 *      ORIENTATION_LEFTBOT = 8;
 *
 * Software
 * DateTime
 * Artist
 * HostComputer
 * Copyright
 * ImageLength
 * ImageWidth
 * 
 * @author Darron Nesbitt
 */
public class ToTIF implements IConversion
{
    private static final String SUPPORTED_DOCUMENT_FORMAT = "TIF";
    public static final String MULTIPAGE_PROPERTY_NAME = "MULTI-PAGE";

    /**
     * Supported TIF tags
     */
    public static final int TAG_BIT_SAMPLE = 258;
    public static final int TAG_PHOTOMETRIC_INTERPRETATION = 262;
    public static final int TAG_FILL_ORDER = 266;
    public static final int TAG_COMPRESSION = 259;
    public static final int TAG_ROW_STRIPS = 278;
    public static final int TAG_X_RESOLUTION = 282;
    public static final int TAG_Y_RESOLUTION = 283;
    public static final int TAG_RESOLUTION_UNIT = 296;
    public static final int TAG_IMAGE_WIDTH = 256;
    public static final int TAG_IMAGE_LENGTH = 257;
    public static final int TAG_SOFTWARE = 305;
    public static final int TAG_DATE_TIME = 306;
    public static final int TAG_ARTIST = 315;
    public static final int TAG_HOST_COMPUTER = 316;
    public static final int TAG_ORIENTATION = 274;
    public static final int TAG_IMAGE_DESCRIPTION = 270;
    public static final int TAG_COPYRIGHT = 33432;

    private List<BufferedImage> document = null;
    private List<String> fileNames = new ArrayList<String>();
    private Properties documentProperties = null;
    private int totalPages = 0;

    private int horizontalDPI = 0;
    private int verticalDPI = 0;
    
    private byte[] byteData = null;

    private boolean multiPage = false;

    //Constants...
    /** Value of the ResolutionUnit TIFF tag that indicated measurement in
     * inches. */
    //public static final char[] INCH_RESOLUTION_UNIT = new char[] {2};
    /** For use in the XResolution TIFF tag. */
    //public static final long[][] X_DPI_RESOLUTION = new long[][] {{200, 1}};
    /** For use in the YResolution TIFF tag. */
    //public static final long[][] Y_DPI_RESOLUTION = new long[][] {{200, 1}};
    /** For use in the BitsPerSample TIFF tag. */
    //public static final char[] BITS_PER_SAMPLE = new char[] {1};
    /**  For use in the Compression TIFF tag. */
//    public static final char[] COMPRESSION =
//            new char[] {BaselineTIFFTagSet.COMPRESSION_LZW};

    /** The image height in pixels of a TIFF image with a resolution of 200 DPI
     * on an 8.5" x 11" page */
    protected static final int HEIGHT = 2150;


    /**
     * Default constructor.
     */
    public ToTIF()
    {
        document = new ArrayList<BufferedImage>();
        documentProperties = new Properties();
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

    public void insertPage(BufferedImage documentPage, int pageNumber) throws Exception
    {
        if(pageNumber>0)
            pageNumber -= 1;

        if(pageNumber<document.size())
            document.add(pageNumber, documentPage);
    }

    public void saveDocument(String fileName) throws Exception
    {
        fileNames.clear();

        writeTIF(fileName);
    }

    public void saveDocument(OutputStream os) throws Exception
    {
        throw new UnsupportedOperationException("Not supported in ToTIFF.");
    }

    public void setDocument(String fileName) throws Exception
    {
        byte[] data = FileIO.readFileToByteArray(fileName);

        setDocument(data);
    }

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

    public void addDocumentProperty(String propertyName, String propertyValue) throws Exception
    {
        documentProperties.setProperty(propertyName, propertyValue);
    }

    public void setDocumentProperties(Properties documentProperties) throws Exception
    {
        this.documentProperties = documentProperties;
    }

    public Properties getDocumentProperties()
    {
        return this.documentProperties;
    }
    
    public void savePage(String fileName, int pageNumber) throws Exception
    {
        throw new UnsupportedOperationException("Not supported in ToTIFF.");
    }

    public void savePage(OutputStream os, int pageNumber) throws Exception
    {
        throw new UnsupportedOperationException("Not supported in ToTIFF.");
    }

    public int getPageCount()
    {
        return totalPages;
    }

    private void createBufferedImages(byte[] fileBytes) throws Exception
    {
        int pageCount = getTIFFPageCount(fileBytes);

        for(int i=0;i<pageCount;i++)
        {
            // image object
            BufferedImage image = loadTIFF(fileBytes, i);

            // add page to image list
            addPage(image);
        }
    }

    /**
     * Load a TIFF page.
     *
     * This method does a better job of loading problematic TIFFs.
     *
     * @param byteArray Byte array containing TIFF file contents.
     * @param imageToLoad Page to load.
     * @return Specified page is returned as a BufferedImage.
     */
    private BufferedImage loadTIFF(byte[] byteArray, int imageToLoad) throws Exception
    {
      try
      {
        //BufferedImage wholeImage = ImageIO.read(file);
        BufferedImage wholeImage = null;

        ByteArraySeekableStream s = new ByteArraySeekableStream(byteArray);
        //PlanarImage pi= JAI.create("tiff", s);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

        String[] p = dec.decodeAsRenderedImage().getPropertyNames();

        if(documentProperties.size()<=0)
        {
            documentProperties = getTIFFProperties(dec);
        }

        // Which of the multiple images in the TIFF file do we want to load
        // 0 refers to the first, 1 to the second and so on.
        if(imageToLoad<0 || imageToLoad>dec.getNumPages())
            imageToLoad = 0;

        RenderedImage op =
            new NullOpImage(dec.decodeAsRenderedImage(imageToLoad),
                            null,
                            OpImage.OP_IO_BOUND,
                            null);

        wholeImage = renderedToBuffered(op);

        //ImageDecodeParam idp = dec.getParam();

        //Object o = wholeImage.getProperty(String.valueOf(TAG_X_RESOLUTION));

        return wholeImage;

      }
      catch(Exception e)
      {
          throw new Exception(e);
      }

    }
    
    public boolean isValidEndianTag(int endian)
    {
    	return ((endian == 0x4949) || (endian == 0x4d4d));
    }

    /**
     * Get number of pages in a TIFF.
     * @param byteArray Array containing TIFF file contents.
     * @return Number of pages in TIFF.
     */
    private int getTIFFPageCount(byte[] byteArray) throws Exception
    {
      try
      {
//    	int l=byteArray.length;
//    	
//        ByteBuffer bbuf = ByteBuffer.wrap(byteArray);
//        bbuf.order(ByteOrder.LITTLE_ENDIAN);
//
//        //bbuf.flip();
//
//        byteArray = null;
//        
//        byteArray = new byte[bbuf.capacity()];
//        
//        //bbuf.get(byteArray,0,byteArray.length);
//        //bbuf.clear();
//        //bbuf.compact();
//        
//        bbuf = null;
        
        ByteArraySeekableStream s = new ByteArraySeekableStream(byteArray);
        
//        int endian = s.readUnsignedShort();
//        int magic = s.readUnsignedShort();
//        
//        boolean be = ( endian == 0x4d4d);
//        
//        boolean ok = isValidEndianTag(endian);
//        
//        if(!ok)
//        {
//        	System.out.println("Endian: " + endian + " / " + ok);
//        
//        	byteArray[0] = 0x49;
//        	byteArray[1] = 0x49;
//
//        	s = new ByteArraySeekableStream(byteArray);
//        }
//        
//        if(magic != 42)
//        {
//        	System.out.println("Magic: " + magic);
//        	
//        	byteArray[2] = 0x2A;
//        	byteArray[3] = 0x00;
//
//        	s = new ByteArraySeekableStream(byteArray);
//        }
//        
////        endian = s.readUnsignedShort();
////        magic = s.readUnsignedShort();
//        
//        System.out.println("Magic: " + magic);
//        System.out.println("Endian: " + endian);
        
        PlanarImage pi= JAI.create("tiff", s);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

        int pc = 1;
        
        try
        {
        	pc = dec.getNumPages();
        }
        catch(Exception x)
        {
        	if(x.getMessage().toUpperCase().contains("BAD ENDIANESS"))
        		System.out.println("Bad endianess value");
        	else
        		System.out.println(x);
        }
        
//        if(!ok || magic != 42 || pc<1)
//        	pc = 1;

        return pc;
      }
      catch(Exception e)
      {
          throw new Exception(e);
      }
    }

    private int readUnsignedShort(ByteArraySeekableStream s, boolean isBigEndian) throws IOException
    {
    	if(isBigEndian)
    	{
    		return s.readUnsignedShort();
    	}
    	else
    		return s.readUnsignedShortLE();
    }
    
    /**
     * Convert RenderedImage to BufferedImage
     * @param img
     * @return BufferedImage
     */
    private BufferedImage renderedToBuffered(RenderedImage img) throws Exception
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        RenderedImageAdapter imageAdapter = new RenderedImageAdapter(img);
        BufferedImage bufImage = imageAdapter.getAsBufferedImage();
        return bufImage;
    }

    private void writeTIF(String fileName) throws Exception
    {
        String multiP = "";

        if(documentProperties.containsKey(MULTIPAGE_PROPERTY_NAME))
        {
            multiP = documentProperties.getProperty(MULTIPAGE_PROPERTY_NAME, "false");

            try
            {
                multiPage = Boolean.valueOf(multiP);
            }
            catch(Exception mpe)
            {
                multiPage = false;
            }
        }

        ImageOutputStream ios = null;
        BufferedImage img = null;

        // if single page
        if(multiPage)
            writeMultiPageTIF(fileName);
        else
            writeSinglePageTIF(fileName);
    }

    /** 
     * Get list of saved files.
     */
    public List<String> getFiles() 
    {
    	return fileNames;
    }
    
    /**
     * 
     * @param fileName
     * @throws Exception
     */
    private void writeMultiPageTIF(String fileName) throws Exception
    {
    	fileNames = new ArrayList<String>();
        totalPages = document.size();

        ImageOutputStream ios = null;
        BufferedImage img = null;

        // SET ENCODER PARAMS
        TIFFEncodeParam tep = setEncoder();

        FileOutputStream file = new FileOutputStream(new File(fileName));
        ios = ImageIO.createImageOutputStream(file);
        ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", file, tep);

        List<BufferedImage> imageList = new ArrayList<BufferedImage>();

        // break out each page to single file
        for(int t=1;t<totalPages;t++)
        {
            imageList.add(document.get(t));
        }

        img = document.get(0);

        tep.setExtraImages(imageList.iterator());

        encoder.encode(img);

        img.flush();
        img = null;
        ios.flush();
        ios.close();
        ios = null;

        file.close();
        file = null;

        imageList.clear();
        imageList = null;

        // add filename to file name list
        fileNames.add(fileName);

    }

    /**
     * Write single page TIF files for current document.
     *
     * @param fileName
     *
     * @throws Exception
     */
    private void writeSinglePageTIF(String fileName) throws Exception
    {
        ImageOutputStream ios = null;
        BufferedImage img = null;

        // break out each page to single file
        for(int t=0;t<totalPages;t++)
        {
            // format filenumber
            String destPath = Path.getPath(fileName);
            String name = Path.getFileNameWithoutExtension(fileName);
            String tifName = destPath + name + "-" + String.valueOf(t) + ".tif";

            FileOutputStream file = new FileOutputStream(new File(tifName));

            img = document.get(t);
            
//            img = convertRGBToGrayscaleImage(img).getAsBufferedImage();
//            img = convertGrayscaleToBlackWhiteImage(img).getAsBufferedImage();

            // SET ENCODER PARAMS
            TIFFEncodeParam tep = setEncoder();

            ios = ImageIO.createImageOutputStream(file);

            ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", file, tep);

            encoder.encode(img);

            img.flush();

            ios.flush();
            ios.close();
            ios = null;
            img = null;

            file.close();
            file = null;
            
            // add file to file name list
            fileNames.add(tifName);
        }
    }
    
    /**
     * convert grayscale image to bilevel image
     * @param ri
     * @return
     */
    public static synchronized RenderedOp convertGrayscaleToBlackWhiteImage(RenderedImage ri) 
    {
        // Generate a histogram.
        Histogram histogram = (Histogram)JAI.create("histogram",ri).getProperty("histogram");

        // Get a threshold equal to the median.
        double[] threshold = histogram.getPTileThreshold(0.5);

        // if background and foreground could not be separated
        if (threshold[0] == 0.0 || threshold[0] == 1.0) 
        {
            threshold[0] = 127.5;
        }

        return JAI.create("binarize", ri, new Double(threshold[0]));
    }

    /**
    * convert rgb image to grayscale
    *
    * typical weights for converting RGB to Grayscale
    * gray = 0.3*red + 0.59*green + 0.11*blue
    */
    public static synchronized RenderedOp convertRGBToGrayscaleImage(RenderedImage ri) 
    {
        double[][] matrix = { { 0.3D, 0.59D, 0.11D, 0D } };
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(ri);
        pb.add(matrix);
        return JAI.create("BandCombine", pb, null);
    }

    private TIFFEncodeParam setEncoder() throws Exception
    {
        TIFFEncodeParam tep = new TIFFEncodeParam();
        ArrayList<TIFFField> tagList = new ArrayList<TIFFField>();

        //BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

        for(Enumeration en = documentProperties.propertyNames();en.hasMoreElements();)
        {
            String name = en.nextElement().toString();
            String propertyName = name.toUpperCase();
            String propertyValue = documentProperties.getProperty(name).trim();

            TIFFField tifField = null;

            if(propertyName.equals("RESOLUTIONUNIT"))
            {
                if(propertyValue.length()>0)
                {
                    char[] value = propertyValue.toCharArray();
                    tifField = new TIFFField(TAG_RESOLUTION_UNIT, TIFFTag.TIFF_SHORT, 1, value);
                }
            }

            if(propertyName.equals("XRESOLUTION"))
            {
                if(propertyValue.length()>0)
                {
                    int dpi = Integer.parseInt(propertyValue);
                    
                    long[][] X_DPI = new long[][] {{dpi, 1}};
                    tifField = new TIFFField(TAG_X_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, X_DPI);

                }
            }

            if(propertyName.equals("YRESOLUTION"))
            {
                if(propertyValue.length()>0)
                {
                    int dpi = Integer.parseInt(propertyValue);

                    long[][] Y_DPI = new long[][] {{dpi, 1}};
                    tifField = new TIFFField(TAG_Y_RESOLUTION, TIFFTag.TIFF_RATIONAL, 1, Y_DPI);

                }
            }

            if(propertyName.equals("BITSPERSAMPLE"))
            {
                if(propertyValue.length()>0)
                {
                    char[] BPS = propertyValue.toCharArray();
                    tifField = new TIFFField(TAG_BIT_SAMPLE, TIFFTag.TIFF_SHORT, 1, BPS);
                }
            }

            if(propertyName.equals("ROWSPERSTRIP"))
            {
                if(propertyValue.length()>0)
                {
                    long value = Long.parseLong(propertyValue);
                    long [] RPS = new long[] {value};
                    tifField = new TIFFField(TAG_ROW_STRIPS, TIFFTag.TIFF_LONG, 1, RPS);
                }
            }

            if(propertyName.equals("PHOTOMETRICINTERPRETATION"))
            {
                if(propertyValue.length()>0)
                {
                    char[] PI = propertyValue.toCharArray();
                    tifField = new TIFFField(TAG_PHOTOMETRIC_INTERPRETATION, TIFFTag.TIFF_SHORT, 1, PI);
                }
            }

            if(propertyName.equals("FILLORDER"))
            {
                if(propertyValue.length()>0)
                {
                    char[] FO = propertyValue.toCharArray();
                    tifField = new TIFFField(TAG_FILL_ORDER, TIFFTag.TIFF_SHORT, 1, FO);
                }
            }

            if(propertyName.equals("COMPRESSION"))
            {
                if(propertyValue.length()>0)
                {
                    int cm = getCompressionMethod(propertyValue);
                    if(cm == -1)
                        throw new Exception("Invalid Compression type set: " + propertyValue);

                    tep.setCompression(cm);
                }
            }

            if(propertyName.equals("IMAGEWIDTH"))
            {
                if(propertyValue.length()>0)
                {
                    long value = Long.parseLong(propertyValue);
                    long[] IW = new long[] {value};

                    tifField = new TIFFField(TAG_IMAGE_WIDTH, TIFFTag.TIFF_LONG, 1, IW);
                }
            }

            if(propertyName.equals("IMAGELENGTH"))
            {
                if(propertyValue.length()>0)
                {
                    long value = Long.parseLong(propertyValue);
                    long[] IL = new long[] {value};

                    tifField = new TIFFField(TAG_IMAGE_WIDTH, TIFFTag.TIFF_LONG, 1, IL);
                }
            }

            if(propertyName.equals("IMAGEDESCRIPTION"))
            {
                if(propertyValue.length()>0)
                {
                    tifField = new TIFFField(TAG_IMAGE_DESCRIPTION, TIFFTag.TIFF_ASCII, 1, new String[] {propertyValue});
                }
            }

            if(propertyName.equals("ORIENTATION"))
            {
                if(propertyValue.length()>0)
                {
                    char[] O = propertyValue.toCharArray();
                    tifField = new TIFFField(TAG_ORIENTATION, TIFFTag.TIFF_SHORT, 1, O);
                }
            }

            if(propertyName.equals("SOFTWARE"))
            {
                if(propertyValue.length()>0)
                {
                    tifField = new TIFFField(TAG_SOFTWARE, TIFFTag.TIFF_ASCII, 1, new String[] {propertyValue});
                }
            }

            if(propertyName.equals("DATETIME"))
            {
                if(propertyValue.length()>0)
                {
                    tifField = new TIFFField(TAG_DATE_TIME, TIFFTag.TIFF_ASCII, 1, new String[] {propertyValue});
                }
            }

            if(propertyName.equals("ARTIST"))
            {
                if(propertyValue.length()>0)
                {
                    tifField = new TIFFField(TAG_ARTIST, TIFFTag.TIFF_ASCII, 1, new String[] {propertyValue});
                }
            }

            if(propertyName.equals("HOSTCOMPUTER"))
            {
                if(propertyValue.length()>0)
                {
                    tifField = new TIFFField(TAG_HOST_COMPUTER, TIFFTag.TIFF_ASCII, 1, new String[] {propertyValue});
                }
            }

            if(tifField != null)
               tagList.add(tifField);
        }

        if(tagList.size()>0)
        {
            TIFFField[] tifFields = new TIFFField[tagList.size()];

            for(int i=0;i<tagList.size();i++)
                tifFields[i] = tagList.get(i);

            tep.setExtraFields(tifFields);
        }

        return tep;
    }

    private int getCompressionMethod(String compressionTypeName)
    {
        int value = -1;

        compressionTypeName = compressionTypeName.toUpperCase();

        if(compressionTypeName.equals("COMPRESSION_NONE"))
            value = TIFFEncodeParam.COMPRESSION_NONE;

        if(compressionTypeName.equals("COMPRESSION_PACKBITS"))
            value = TIFFEncodeParam.COMPRESSION_NONE;

        if(compressionTypeName.equals("COMPRESSION_GROUP3_1D"))
            value = TIFFEncodeParam.COMPRESSION_GROUP3_1D;

        if(compressionTypeName.equals("COMPRESSION_GROUP3_2D"))
            value = TIFFEncodeParam.COMPRESSION_GROUP3_2D;

        if(compressionTypeName.equals("COMPRESSION_GROUP4"))
            value = TIFFEncodeParam.COMPRESSION_GROUP4;

        if(compressionTypeName.equals("COMPRESSION_LZW"))
            value = TIFFEncodeParam.COMPRESSION_LZW;

        if(compressionTypeName.equals("COMPRESSION_JPEG_TTN2"))
            value = TIFFEncodeParam.COMPRESSION_JPEG_TTN2;

        if(compressionTypeName.equals("COMPRESSION_DEFLATE"))
            value = TIFFEncodeParam.COMPRESSION_DEFLATE;

        return value;
    }

    private Properties getTIFFProperties(ImageDecoder decoder) throws Exception
    {
        Properties props = new Properties();

        try
        {
            TIFFDirectory td = (TIFFDirectory)decoder.decodeAsRenderedImage().getProperty("tiff_directory");

            TIFFField[] tfs = td.getFields();
            for(int i=0;i<tfs.length;i++)
            {
                TIFFField tf = tfs[i];

                int tagID = tf.getTag();
                String name = String.valueOf(tagID);
                int type = tf.getType();
                String value = "";

                switch(tagID)
                {
                    case TAG_ARTIST:
                        name = "Artist";
                        value = getASCIIValue(tf);
                        break;

                    case TAG_BIT_SAMPLE:
                        name = "BitsPerSample";
                        value = getShortValue(tf);
                        break;

                    case TAG_COMPRESSION:
                        name = "Compression";
                        value = getShortValue(tf);
                        break;

                    case TAG_COPYRIGHT:
                        name = "Copyright";
                        value = getASCIIValue(tf);
                        break;

                    case TAG_DATE_TIME:
                        name = "DateTime";
                        value = getASCIIValue(tf);
                        break;

                    case TAG_FILL_ORDER:
                        name = "FillOrder";
                        value = getShortValue(tf);
                        break;

                    case TAG_HOST_COMPUTER:
                        name = "HostComputer";
                        value = getASCIIValue(tf);
                        break;

                    case TAG_IMAGE_DESCRIPTION:
                        name = "ImageDescription";
                        value = getASCIIValue(tf);
                        break;

                    case TAG_IMAGE_LENGTH:
                        name = "ImageLength";
                        value = getLongValue(tf);
                        break;

                    case TAG_IMAGE_WIDTH:
                        name = "ImageWidth";
                        value = getLongValue(tf);
                        break;

                    case TAG_ORIENTATION:
                        name = "Orientation";
                        value = getShortValue(tf);
                        break;

                    case TAG_PHOTOMETRIC_INTERPRETATION:
                        name = "PhotometricInterpretation";
                        value = getShortValue(tf);
                        break;

                    case TAG_RESOLUTION_UNIT:
                        name = "ResolutionUnit";
                        value = getShortValue(tf);
                        break;

                    case TAG_ROW_STRIPS:
                        name = "RowsPerStrip";
                        value = getLongValue(tf);
                        break;

                    case TAG_SOFTWARE:
                        name = "Software";
                        value = getASCIIValue(tf);
                        break;

                    case TAG_X_RESOLUTION:
                        name = "XResolution";
                        long[] x = tf.getAsRational(0);
                        value = String.valueOf(x[0]) + "/ "+ String.valueOf(x[1]);
                        //horizontalDPI = calculateDPI(value);
                        horizontalDPI = (int)x[0] / (int)x[1];
                        //value += " = " + horizontalDPI;
                        value = String.valueOf(horizontalDPI);

                        break;

                    case TAG_Y_RESOLUTION:
                        name = "YResolution";
                        long[] y = tf.getAsRational(0);
                        value = String.valueOf(y[0]) + "/ "+ String.valueOf(y[1]);
                        //verticalDPI = calculateDPI(value);
                        verticalDPI = (int)y[0] / (int)y[1];

                        //value += " = " + verticalDPI;
                        value = String.valueOf(verticalDPI);

                        break;

                }
                
                props.put(name, value);
            }

            return props;
        }
        catch(Exception e)
        {
            throw new Exception("Error retrieving TIFF properties: " + e.toString());
        }
    }

    private int calculateDPI(String value)
    {
    	int res = 0;

    	try
    	{
    		int o = value.indexOf("/");
    		String p1 = value.substring(0, o);
    		String p2 = value.substring(o+1);
    		
    		int i1 = Integer.parseInt(p1);
    		int i2 = Integer.parseInt(p2);

    		res = i1 / i2;
    		
    		System.out.println(res);
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
    	return res;
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

    private String getRationalValue(TIFFField tf)
    {
        String value = "";

        try
        {
            long[] x = tf.getAsRational(0);
            for(int i=0;i<x.length;i++)
            {
                if(value.length()>0)
                    value += "/";

                value = String.valueOf(x[0]);
            }
        }
        catch(Exception e)
        {

        }

        return value;
    }

    private String getSRationalValue(TIFFField tf)
    {
        String value = "";

        try
        {
            int[] x = tf.getAsSRational(0);
            for(int i=0;i<x.length;i++)
            {
                if(value.length()>0)
                    value += "/";

                value = String.valueOf(x[0]);
            }
        }
        catch(Exception e)
        {

        }

        return value;
    }

    private String getASCIIValue(TIFFField tf)
    {
        String value = "";

        try
        {
            value = tf.getAsString(0);
        }
        catch(Exception e)
        {

        }

        return value;
    }

    private String getByteValue(TIFFField tf)
    {
        String value = "";

        try
        {
            byte[] x = tf.getAsBytes();
            for(int i=0;i<x.length;i++)
            {
                if(value.length()>0)
                    value += "/";

                value = String.valueOf(x[0]);
            }
        }
        catch(Exception e)
        {

        }

        return value;
    }

    @SuppressWarnings("empty-statement")
    private String getShortValue(TIFFField tf)
    {
        String value = "";

        try
        {
            int x = tf.getAsInt(0);
//            for(int i=0;i<x.length;i++)
//            {
//                if(value.length()>0)
//                    value += "/";

                value = String.valueOf(x);
//            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return value;
    }

    private String getLongValue(TIFFField tf)
    {
        String value = "";

        try
        {
            long x = tf.getAsLong(0);
            value = String.valueOf(x);

        }
        catch(Exception e)
        {

        }

        return value;
    }

    private String getSLongValue(TIFFField tf)
    {
        String value = "";

        try
        {
            long[] x = tf.getAsLongs();
            for(int i=0;i<x.length;i++)
            {
                if(value.length()>0)
                    value += "/";

                value = String.valueOf(x[0]);
            }
        }
        catch(Exception e)
        {

        }

        return value;
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
        TIFFEncodeParam tep = new TIFFEncodeParam();

        try
        {
//            Class c = Class.forName("com.sun.media.jai.codec.TIFFEncodeParam");
//            Field m[] = c.getDeclaredFields();
//            for (int i = 0; i < m.length; i++)
//            {
//                try
//                {
//                    String name = m[i].getName().toUpperCase();
//                    if(name.startsWith("COMPRESSION"))
//                        System.out.println(name + " / " + m[i].getInt("slot"));
//                }
//                catch(Exception x)
//                {
//                    //x.printStackTrace();
//                }
//            }

        	int c = TIFFEncodeParam.COMPRESSION_GROUP3_1D;
        	c=TIFFEncodeParam.COMPRESSION_GROUP4;
        	c=TIFFEncodeParam.COMPRESSION_LZW;
        	c=TIFFEncodeParam.COMPRESSION_DEFLATE;
        	c=TIFFEncodeParam.COMPRESSION_GROUP3_2D;
        	c=TIFFEncodeParam.COMPRESSION_JPEG_TTN2;
        	c=TIFFEncodeParam.COMPRESSION_PACKBITS;
        	c=TIFFEncodeParam.COMPRESSION_NONE;
        	
            ToTIF tt = new ToTIF();

            tt.setDocument("C:\\Temp\\50002079-0.tif");
Properties dp = tt.getDocumentProperties();

            for(Enumeration e = dp.elements(); e.hasMoreElements();)
            {
            	String name = (String)e.nextElement();
            	System.out.println(name + " = " + dp.getProperty(name));
            }
//            tt.addDocumentProperty("ImageWidth", "1700");
//            tt.addDocumentProperty("ImageHeight", "2150");
//            tt.addDocumentProperty("BitsPerSample", "1");
//            tt.addDocumentProperty("Compression", "COMPRESSION_PACKBITS");
//            tt.addDocumentProperty("PhotometricInterpretation", "1");
//            tt.addDocumentProperty("FillOrder", "1");
//            tt.addDocumentProperty("Make", "Fax: 4969222215914");
//            tt.addDocumentProperty("RowsPerStrip", "1");
//            tt.addDocumentProperty("XResolution", "200");
//            tt.addDocumentProperty("YResolution", "200");
//            tt.addDocumentProperty("Software", "GDR 3.1");
//            tt.addDocumentProperty("Artist", "Darron Nesbitt");
//            tt.addDocumentProperty("DateTime", new Date().toString());
//
//            tt.saveDocument("C:\\Fax\\Faxes\\RET.tif");

         }
         catch (Exception e)
         {
            e.printStackTrace();
         }



    }
}
