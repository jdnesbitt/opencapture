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

package net.filterlogic.OpenCapture.utils;

import net.filterlogic.OpenCapture.interfaces.IZoneReader;
import net.filterlogic.OpenCapture.interfaces.OpenCaptureReaderException;
import net.filterlogic.OpenCapture.*;
import net.filterlogic.util.xml.XMLParser;
import net.filterlogic.util.imaging.ToTIFF;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 *
 * @author Darron Nesbitt
 */
public class OCBarcodeReader 
{

    public OCBarcodeReader()
    {
    }
    
    public String ReadBarCode(String fileName, String idMethod, int x, int y, int w, int h, int page) throws Exception
    {

        String zoneValue = "";
        String className = "";

        try
        {
            Batch batch = new Batch(false);

            File file = new File(fileName);
            BufferedImage image = ToTIFF.loadTIFF(file, page);

            className = batch.getOcConfig().getReaderClass(idMethod);

            // load reader
            Class c = Class.forName(className);

            // create reader object.
            IZoneReader zr = (IZoneReader)c.newInstance();

            // set image object in reader
            zr.setImage(image);
            byte min=0;

            Zone zone = new Zone("FORMID", idMethod, x, y, w, h, min, "IndxField");

            try
            {
                zoneValue = zr.ReadZone(zone);
            }
            catch(OpenCaptureReaderException ocre)
            {
                zoneValue = "";
            }
            
            zr = null;
            zone = null;
            image = null;
            
            return zoneValue;
            
        }
        catch(OpenCaptureException e)
        {
            throw new Exception(net.filterlogic.util.StackTraceUtil.getStackTrace(e));
        }
    }
    
    private static void usage()
    {
        System.out.append("usage:  ocbr.sh <tif name> <ReaderID> <X> <Y> <W> <H> [page]\n\n");
    }
    
    public static void main(String[] args)
    {
        String msg = "OCBarcodeReader - Read barcode at specified zone.\n\n";
        
        String fileName = "";
        String idMethod = "";
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int page = 1;

        System.out.println(msg);
        
        if(args.length<6)
        {
            usage();
            
            System.exit(0);
        }
        
        try
        {

            fileName = args[0];
            idMethod = args[1];
            x = Integer.parseInt(args[2]);
            y = Integer.parseInt(args[3]);
            w = Integer.parseInt(args[4]);
            h = Integer.parseInt(args[5]);

            if(args.length>6)
                page = Integer.parseInt(args[6]);
            
            if(page>0)
                page -= 1;

            OCBarcodeReader ocbr = new OCBarcodeReader();

            String value = ocbr.ReadBarCode(fileName,idMethod, x, y, w, h, page);

            ocbr = null;
            
            System.out.println("ZoneValue = " + value + "\n\n");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
