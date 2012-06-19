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
import java.util.Properties;

/**
 *
 * @author Darron Nesbitt
 */
public class Converter
{

    public static void main(String[] args)
    {

        //String fname = "C:\\export\\oc\\OCData\\Images\\0000003c\\00000000.tif";
        String fname = "C:\\tmp\\attsl.pdf";
        //String fname = "C:\\OCData\\Import\\Archive\\MXFax\\59.tif";
        //String fname = "C:\\OCData\\Import\\TRANSFLO\\Archive\\TransfloExpress\\2.tif";


        try
        {
            ToTIF tt = new ToTIF();
            ToPDF tp = new ToPDF();
            
            //toPdf.setDocument(fname);
        	//ToGIF tt = new ToGIF();

            //tt.setDocument("C:\\TestDocs\\TIFF\\TIF1.tif");
            tp.setDocument(fname);
            //tt.setDocument("C:\\Dell\\E-WTRMRK.GIF");
            //tt.setDocument("C:\\mqmapps\\gskit\\jre\\javaws\\sunlogo64x30.gif");
            //tt.setDocument("C:\\mqmapps\\gskit\\jre\\javaws\\javalogo52x88.gif");

            //tt.setVerticalDPI(300);
            //tt.setHorizontalDPI(300);

//            ToPNG tp = new ToPNG();
//
//            tp.setDocument(tt.getBufferedImages());
//            tp.setHorizontalDPI(tt.getHorizontalDPI());
//            tp.setVerticalDPI(tt.getVerticalDPI());
//            Properties p = new Properties();

            //tt.addDocumentProperty("Compression", "COMPRESSION_GROUP4");
            tt.addDocumentProperty("Compression", "COMPRESSION_PACKBITS");
            
            // single page tifs
            tt.addDocumentProperty(tt.MULTIPAGE_PROPERTY_NAME, "true");
            //tt.rotateImage(1, -90.0);
            tt.setDocument(tp.getBufferedImagesAsGrayscale());
            
            tt.saveDocument(fname + ".tif");

//            for(int i=0;i<tt.getFiles().size();i++)
//                System.out.println("File-" + i + " = " + tt.getFiles().get(i));

//            p.put("TITLE", "Test Document");
//            p.put("AUTHOR", "DARRON NESBITT");
//            p.put("Creator", "GDR2.1");
//            p.put("CREATIONDATE", "");
//            p.put("SUBJECT", "Test TIF to PDF Conversion!");
//
//            tp.setDocumentProperties(p);

//tp.saveDocument("C:\\Temp\\GIF1.gif.png");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
