/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author dnesbitt
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
            throw new Exception(e.toString());
        }
    }
    
    private static void usage()
    {
        String msg = "OCBarcodeReader - Read barcode at specified zone.\n\n" +
                "usage:  ocbr.sh <tif name> <ReaderID> <X> <Y> <W> <H> [page]\n\n";
        
        System.out.println(msg);
    }
    
    public static void main(String[] args)
    {
        String fileName = "";
        String idMethod = "";
        int x = 0;
        int y = 0;
        int w = 0;
        int h = 0;
        int page = 1;

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
            System.out.println(e.toString());
        }
    }
}
