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

/**
 *
 * @author Darron Nesbitt
 */
package net.filterlogic.util.imaging;

import net.filterlogic.io.Path;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.ByteArraySeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.TIFFDecodeParam;
import com.sun.media.jai.codec.TIFFEncodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.ImageCodec;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import java.awt.image.RenderedImage;
import javax.media.jai.RenderedImageAdapter;

import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
//import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import com.sun.media.imageio.plugins.tiff.BaselineTIFFTagSet;
//import com.sun.media.jai.codec.TIFFDirectory;
//import com.sun.media.imageioimpl.plugins.tiff.TIFFField;
import com.sun.media.jai.codec.TIFFField;
import com.sun.media.imageio.plugins.tiff.TIFFTag;
import javax.media.jai.JAI;
import javax.media.jai.Histogram;
import javax.media.jai.RenderedOp;
import java.awt.image.RenderedImage;
import javax.media.jai.PlanarImage;

//import com.sun.media.imageioimpl.plugins.tiff.TIFFImageMetadata;
import java.util.ArrayList;
import java.util.List;

import java.text.NumberFormat;
import java.text.DecimalFormat;

import org.apache.log4j.*;

import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ToTIFF 
{
  
    //Constants...
    /** Value of the ResolutionUnit TIFF tag that indicated measurement in
     * inches. */
    public static final char[] INCH_RESOLUTION_UNIT = new char[] {2};
    /** For use in the XResolution TIFF tag. */
    public static final long[][] X_DPI_RESOLUTION = new long[][] {{200, 1}};
    /** For use in the YResolution TIFF tag. */
    public static final long[][] Y_DPI_RESOLUTION = new long[][] {{200, 1}};
    /** For use in the BitsPerSample TIFF tag. */
    public static final char[] BITS_PER_SAMPLE = new char[] {1};
    /**  For use in the Compression TIFF tag. */
//    public static final char[] COMPRESSION = 
//            new char[] {BaselineTIFFTagSet.COMPRESSION_LZW};

    /** The image height in pixels of a TIFF image with a resolution of 200 DPI 
     * on an 8.5" x 11" page */
    protected static final int HEIGHT = 2150;

    /**
     * Convert multipage TIFF to single page TIFF.
     * @param srcFiles Array of source files to convert.
     * @param destPath Folder to store single page TIFFs in.
     * @param archivePath Path to move source TIFF files to after single page TIFFs created.
     * @param pattern Pattern of single page TIFF file names.  Java NumberFormatter used with page number to create file name.
     * @param multipage Set to true if source TIFFs should be coverted to multi-page TIFF.
     * @param dpi DPI to set TIFFs to.
     * @return Returns a list of files in destination path.
     * @throws net.filterlogic.util.imaging.OpenCaptureImagingException
     */
    public static List toTIFF(String[] srcFiles, String destPath, String archivePath, String pattern, boolean multipage, int dpi) throws OpenCaptureImagingException
    {
        String pathSep = System.getProperty("file.separator");

        int pageCount = 0;

        // make sure destpath has trailing slash.
        if(destPath.lastIndexOf(pathSep) != destPath.length()-1)
                destPath += pathSep;

        // create path if doesn't exist
        if(!Path.ValidatePath(destPath))
            if(!Path.createPath(destPath))
                throw new OpenCaptureImagingException("Unable to create destination path for imported images [" + destPath + "]");

        // make sure archivePath has trailing slash
        if(archivePath.lastIndexOf(pathSep) != archivePath.length()-1)
            archivePath += pathSep;

        if(!Path.ValidatePath(archivePath))
            if(!Path.createPath(archivePath))
                throw new OpenCaptureImagingException("Unable to create archive path for imported images [" + archivePath + "]");

        // set a default pattern if one not passed.
        if(pattern.trim().length()<1)
            pattern = "#";

        NumberFormat formatter = new DecimalFormat(pattern);

        ArrayList<String> list = new ArrayList<String>();

        for(int i=0;i<srcFiles.length;i++)
        {
            try
            {
                
                File f = new File(srcFiles[i]);
                
                ImageIO.setUseCache(false);
                ImageInputStream imageInputStream = ImageIO.createImageInputStream(f);
                java.util.Iterator readers = ImageIO.getImageReaders(imageInputStream);
                ImageReader reader1 = (ImageReader)readers.next();
                //ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(f));
                reader1.setInput(imageInputStream);

                pageCount = reader1.getNumImages(true);
                Iterator writers = ImageIO.getImageWritersByFormatName("tiff");
                ImageWriter writer = (ImageWriter)writers.next();

                //ImageWriteParam param = writer.getDefaultWriteParam();

                //param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

                //String[] legalTypes = param.getCompressionTypes();

                //param.setCompressionType("PackBits");

                //ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ImageOutputStream ios = null;
                BufferedImage img = null;

                // break out each page to single file
                for(int t=0;t<pageCount;t++)
                {
                    // format filenumber
                    String tifName = destPath + formatter.format(t) +  ".tif";

                    FileOutputStream file = new FileOutputStream(new File(tifName));

                    img = reader1.read(t);
                    IIOImage iioimg = reader1.readAll(t, null);
                    ios = ImageIO.createImageOutputStream(file);
                    //IIOMetadata iiom = getMetadata(writer, img, null, 200);

                    TIFFEncodeParam tep = setEncoder(TIFFEncodeParam.COMPRESSION_PACKBITS,200);

                    ImageEncoder encoder = ImageCodec.createImageEncoder("TIFF", file, tep);

                    encoder.encode(img);
                    //boolean ok = ImageIO.write(img, "tiff", ios);
                    //writer.setOutput(ios);
                    //writer.write(iiom, iioimg, null);

                    img.flush();

                    ios.flush();
                    ios.close();
                    ios = null;
                    iioimg = null;
                    //iiom = null;
                    img = null;
                    //writer.dispose();
                    //byteOut.flush();
                    file.close();

                    file = null;

                    //System.out.println("Add file!");

                    list.add(tifName);
                }

                reader1.dispose();
                readers = null;

                writer.dispose();
                writers = null;

                imageInputStream.flush();
                imageInputStream.close();
                imageInputStream = null;

                f = null;

                if(!net.filterlogic.io.FileAccess.Move(srcFiles[i], archivePath))
                    throw new Exception("Unable to move input file to archive path [" + srcFiles[i] + "] to [" + archivePath + "]");

            }
            catch(Exception e)
            {
                throw new OpenCaptureImagingException(e.toString());
            }
        }
        
        return list;
    }
    
    /**
     * 
     * @param fileName
     */
    public void test6(String fileName)
    {
        try
        {
            File f = new File(fileName);

            ImageInputStream imageInputStream = ImageIO.createImageInputStream(f);
            java.util.Iterator readers = ImageIO.getImageReaders(imageInputStream);
            ImageReader reader1 = (ImageReader)readers.next();
            ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(f));
            reader1.setInput(iis);

            int number = reader1.getNumImages(true);
            Iterator writers = ImageIO.getImageWritersByFormatName("tiff");
            ImageWriter writer = (ImageWriter)writers.next();

            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ImageOutputStream ios = null;
            BufferedImage img = null;

            for(int i = 0; i<number;i++)
            {
                img = reader1.read(i);
                ios = ImageIO.createImageOutputStream(byteOut);
                writer.setOutput(ios);
                writer.write(img);
                ios.flush();
                img.flush();
                byteOut.flush();
            }
        
        }
        catch(Exception e)
        {
            System.out.println(e.toString());
        }
        
        
    }
    
    /**
     * Get number of pages in a TIFF.
     * @param byteArray Array containing TIFF file contents.
     * @return Number of pages in TIFF.
     */
    public static int getTIFFPageCount(byte[] byteArray)
    {
      try
      {
        ByteArraySeekableStream s = new ByteArraySeekableStream(byteArray);
        PlanarImage pi= JAI.create("tiff", s);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

        return dec.getNumPages();
    
      }
      catch(Exception e)
      {
          System.out.println(e.toString());
          
          return 0;
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
    public static BufferedImage loadTIFF(byte[] byteArray, int imageToLoad)
    {
      try
      {
        //BufferedImage wholeImage = ImageIO.read(file);
        BufferedImage wholeImage = null;

        ByteArraySeekableStream s = new ByteArraySeekableStream(byteArray);
        //PlanarImage pi= JAI.create("tiff", s);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

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

        return wholeImage;

      }
      catch(Exception e)
      {
          System.out.println(e.toString());
          
          return null;
      }

    }


    /**
     * Load TIFF into image decoder
     * @param byteArray
     * @return ImageDecoder
     */
    public static ImageDecoder loadTIFF(byte[] byteArray)
    {
      try
      {
        ByteArraySeekableStream s = new ByteArraySeekableStream(byteArray);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

        return dec;
      }
      catch(Exception e)
      {
          System.out.println(e.toString());

          return null;
      }
    }
        
    /**
     * Load specified tiff page and return as buffered image.
     * @param file
     * @param imageToLoad Page to load
     * @return BufferedImage
     */
    public static BufferedImage loadTIFF(File file, int imageToLoad)
    {
      try
      {
        BufferedImage wholeImage = ImageIO.read(file);
 
        SeekableStream s = new FileSeekableStream(file);

        TIFFDecodeParam param = null;

        ImageDecoder dec = ImageCodec.createImageDecoder("tiff", s, param);

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

        return wholeImage;
    
      }
      catch(Exception e)
      {
          System.out.println(e.toString());
          
          return null;
      }

    }
    
    /**
     * Convert RenderedImage to BufferedImage
     * @param img
     * @return BufferedImage
     */
    private static BufferedImage renderedToBuffered(RenderedImage img) 
    {
        if (img instanceof BufferedImage) 
        {
            return (BufferedImage) img;
        }

        RenderedImageAdapter imageAdapter = new RenderedImageAdapter(img);
        BufferedImage bufImage = imageAdapter.getAsBufferedImage();
        return bufImage;
    }

    /**
     * 
     * @param image
     * @param dpi
     * @return
     */
//    public static byte[] TIFF(BufferedImage image,int dpi)
//    {
//        RenderedImage out = null;
//
//        try
//        {
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
//            boolean foundWriter = false;
//            Iterator writerIter = ImageIO.getImageWritersByFormatName("tiff");
//
//            foundWriter = true;
//            ImageWriter writer = (ImageWriter)writerIter.next();
//            writer.setOutput(ios);
//
//            ImageWriteParam param = writer.getDefaultWriteParam();
//
//            writer.prepareWriteSequence(null);
//
//            IIOImage iioImage = new IIOImage(image, null,null);
//
//            IIOMetadata iioMetadata = getMetadata(writer,image,null,dpi);
//
//            iioImage.setMetadata(iioMetadata);
//
//            writer.writeToSequence(iioImage, null);
//            iioImage.getRenderedImage();
//
//            ios.flush();
//            writer.dispose();
//            ios.close();
//            out = iioImage.getRenderedImage();
//
//            return baos.toByteArray();
//        }
//        catch(Exception e)
//        {
//            System.out.println(e.toString());
//            
//            return null;
//        }
//
//    }

    /**
     * Set TIFF encoder parameters.
     * @param COMPRESSION_TYPE Type of compression to use.
     * @param dpi DPI setting.
     * @return Returns TIFFEncoderParam containing encoder settings.
     */
    private static TIFFEncodeParam setEncoder(int COMPRESSION_TYPE,int dpi)
    {
        TIFFEncodeParam tep = new TIFFEncodeParam();

        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();

        int tagBitSample = 258;
        int tagPI = 262;
        int tagFO = 266;
        int tagCompression = 259;
        int tagRowStrips = 278;
        int tagXRes = 282;
        int tagYRes = 283;
        int tagResUnit = 296;

        TIFFField resUnit = new TIFFField(tagResUnit, TIFFTag.TIFF_SHORT, 1, INCH_RESOLUTION_UNIT);

        // set X DPI
        long[][] X_DPI = new long[][] {{dpi, 1}};
        TIFFField xDPI = new TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL, 1, X_DPI);
        // set Y DPI
        long[][] Y_DPI = new long[][] {{dpi, 1}};
        TIFFField yDPI = new TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL, 1, Y_DPI);
        
        TIFFField bitSample = new TIFFField(tagBitSample, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE);

        TIFFField rowStrips = new TIFFField(tagRowStrips, TIFFTag.TIFF_LONG, 1, new long[] {HEIGHT});
        
        TIFFField pi = new TIFFField(tagPI, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE);
        
        TIFFField fo = new TIFFField(tagPI, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE);

        //TIFFField compression = new TIFFField(tagCompression, TIFFTag.TIFF_SHORT, 1, COMPRESSION);

        tep.setCompression(COMPRESSION_TYPE);
        tep.setExtraFields(new TIFFField[] {bitSample,pi,fo,rowStrips,xDPI,yDPI,resUnit});

        return tep;
    }
//-------------------------------------------------------------------

    /**
     * 
     * @param imageWriter
     * @param image
     * @param param
     * @param dpi
     * @return
     */
//    private static IIOMetadata getMetadata(ImageWriter imageWriter,
//            BufferedImage image, ImageWriteParam param, int dpi)
//    {
//        TIFFImageMetadata tiffMetadata = (TIFFImageMetadata) getIIOMetadata(image, imageWriter, param);
//
//        com.sun.media.imageioimpl.plugins.tiff.TIFFIFD rootIFD = tiffMetadata.getRootIFD();
//
//        BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
//
//        //Resolution tags...
//        TIFFTag tagResUnit = base.getTag(BaselineTIFFTagSet.TAG_RESOLUTION_UNIT);
//        TIFFTag tagXRes = base.getTag(BaselineTIFFTagSet.TAG_X_RESOLUTION);
//        TIFFTag tagYRes = base.getTag(BaselineTIFFTagSet.TAG_Y_RESOLUTION);
//
//        TIFFTag tagPI = base.getTag(BaselineTIFFTagSet.TAG_PHOTOMETRIC_INTERPRETATION);
//        TIFFTag tagFO = base.getTag(BaselineTIFFTagSet.TAG_FILL_ORDER);
//        //BitsPerSample tag
//	TIFFTag tagBitSample = base.getTag(BaselineTIFFTagSet.TAG_BITS_PER_SAMPLE);
//        //Row and Strip tags...
//        TIFFTag tagRowStrips = base.getTag(BaselineTIFFTagSet.TAG_ROWS_PER_STRIP);
//        //Compression tag
//	TIFFTag tagCompression = base.getTag(BaselineTIFFTagSet.TAG_COMPRESSION);
//
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagResUnit, TIFFTag.TIFF_SHORT, 1, INCH_RESOLUTION_UNIT));
//
//        // set X DPI
//        //rootIFD.addTIFFField(new TIFFField(base.getTag(282), 5, 1, dpiArray));
//        long[][] X_DPI = new long[][] {{dpi, 1}};
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagXRes, TIFFTag.TIFF_RATIONAL, 1, X_DPI));
//        // set Y DPI
//        long[][] Y_DPI = new long[][] {{dpi, 1}};
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagYRes, TIFFTag.TIFF_RATIONAL, 1, Y_DPI));
//        
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagBitSample, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE));
//
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagRowStrips, TIFFTag.TIFF_LONG, 1, new long[] {HEIGHT}));
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagPI, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE));
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagFO, TIFFTag.TIFF_SHORT, 1, BITS_PER_SAMPLE));
//        rootIFD.addTIFFField(new com.sun.media.imageioimpl.plugins.tiff.TIFFField(tagCompression, TIFFTag.TIFF_SHORT, 1, COMPRESSION));
//
//        return tiffMetadata;
//    }
    
    /**
     * 
     * @param image
     * @param imageWriter
     * @param param
     * @return
     */
    private static IIOMetadata getIIOMetadata(BufferedImage image, ImageWriter imageWriter, ImageWriteParam param) {
        ImageTypeSpecifier spec = ImageTypeSpecifier.createFromRenderedImage(image);
        IIOMetadata metadata = imageWriter.getDefaultImageMetadata(spec, param);
        return metadata;
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

    /**
     * Load file into a byte array.
     * @param fileName
     * @return byte array
     */
    public static byte[] loadFileToByteArray(String fileName)
    {
        FileInputStream fis;
        
        try
        {
            fis = new FileInputStream(fileName);
            
            int fisSize = fis.available();
            
            byte[] in = new byte[fisSize];
            
            // read entire file
            fis.read(in, 0, fisSize);
            

            fis.close();
            
            return in;
        }
        catch(Exception fioe)
        {
            System.out.println(fioe.toString());
            return null;
        }
    }
}
