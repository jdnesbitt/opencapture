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

import java.util.List;
import net.filterlogic.util.imaging.OpenCaptureImagingException;
import net.filterlogic.util.imaging.ToTIFF;

/**
 *
 * @author Darron Nesbitt
 */
public class OCTIFFormatter 
{

    public OCTIFFormatter()
    {
    }

    public List ConvertTIFF(String tifFile, String outDir) throws OpenCaptureImagingException
    {
        String[] tiffs = new String[1];
        
        tiffs[0] = tifFile;
        
        List list = ToTIFF.toTIFF(tiffs, outDir, outDir, "00000000", false, 200);
        
        return list;

    }

    private static void usage()
    {
        System.out.println("usage:  octf.sh <tif name> <output directory>\n\n");
    }

    public static void main(String[] args)
    {
        String msg = "OCTIFFormatter - OpenCapture TIFF Formatter converts specified TIF to OC TIF format (200x200 DPI).\n\n";

        System.out.println(msg);

        String fileName = "";
        String outDir = "";

        if(args.length<2)
        {
            usage();

            System.exit(0);
        }
        
        try
        {

            fileName = args[0];
            outDir = args[1];

            OCTIFFormatter octf = new OCTIFFormatter();
            
            try
            {            
                List list = octf.ConvertTIFF(fileName, outDir);

                octf = null;
            
                System.out.println("Finished! Extracted " + list.size() + " images.\n\n");
            }
            catch(OpenCaptureImagingException ocie)
            {
                ocie.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
