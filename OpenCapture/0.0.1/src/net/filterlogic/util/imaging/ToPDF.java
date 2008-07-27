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
package net.filterlogic.util.imaging;

import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
//import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;


/**
 *
 * @author Darron Nesbitt
 */
public class ToPDF 
{
    
    public static void ToPDF(String[] files,String pdfOutFile) throws OpenCaptureImagingException
    {
        String tiff_file;
        String pdf_file = pdfOutFile;

        try 
        {

            Document document = new Document();

            PdfWriter writer = PdfWriter.getInstance(document,
                            new FileOutputStream(pdf_file));
            int pages = 0;
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            RandomAccessFileOrArray ra = null;

            for (int i = 0; i < files.length; i++) 
            {
                int comps = 0;
                tiff_file = files[i];

                try 
                {
                    ra = new RandomAccessFileOrArray(tiff_file);
                    comps = TiffImage.getNumberOfPages(ra);
                }
                catch (Throwable e) 
                {
                    throw new Exception("Exception in " + tiff_file + " " + e.toString());
                    //continue;
                }

                //System.out.println("Processing: " + tiff_file);

                for (int c = 0; c < comps; ++c) 
                {
                    try 
                    {
                        Image img = TiffImage.getTiffImage(ra, c + 1);
                        if (img != null) 
                        {
                            //System.out.println("page " + (c + 1));

                            img.scaleToFit(675,775);
                            img.setAbsolutePosition(0,50);
//                            document.add(new Paragraph(tiff_file + " - page " + (c + 1)));

                            cb.addImage(img);
                            document.newPage();
                            ++pages;
                        }
                    }
                    catch (Throwable e) 
                    {
                        throw new Exception("Exception " + tiff_file + " page " + (c + 1) + " " + e.getMessage());
                    }
                }

                ra.close();
            }
            
            // close pdf
            document.close();

        } 
        catch (Exception e) 
        {
            throw new OpenCaptureImagingException("ToPDF exception: " + e.toString());
        }

    }
    /**
     * Demonstrates some TIFF functionality.
     * 
     * @param args
     *            a list of tiff files to convert
     */
    public static void main(String[] args) 
    {


        try
        {
            String path = "/home/dnesbitt/Test/OpenCapture/ffe/Images/0000005d/";
            String filelist = path + "00000003.tif|" + path + "00000004.tif|" + path + "00000005.tif|" + path + "00000006.tif";
            String[] sfile = filelist.split("\\|");
            
            ToPDF(sfile,path + "document2.pdf");
            
        }
        catch (Throwable e) 
        {
            e.printStackTrace();
        }
    }
}