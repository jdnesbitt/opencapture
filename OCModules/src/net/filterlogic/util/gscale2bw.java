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

package net.filterlogic.util;

import java.io.File;
import net.filterlogic.util.imaging.ToTIFF;

/**
 *
 * @author Darron Nesbitt
 */

public class gscale2bw 
{

    public gscale2bw(String fileName,int page)
    {
       java.io.File file = new java.io.File(fileName);
        
       java.awt.image.BufferedImage bi = ToTIFF.loadTIFF(file, page);
       
       java.awt.image.RenderedImage ri = (java.awt.image.RenderedImage)bi;
       
       javax.media.jai.RenderedOp ro = ToTIFF.convertGrayscaleToBlackWhiteImage(ri);
       
       bi = ro.getAsBufferedImage();
       
       
    }
    
    public static void main(String[] args)
    {
        String filename = args[0];
        int page = Integer.parseInt(args[1]);
        
        gscale2bw gsbw = new gscale2bw(filename, page);
        
        
    }
}
