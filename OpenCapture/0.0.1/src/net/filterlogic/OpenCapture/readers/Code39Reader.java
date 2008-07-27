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

package net.filterlogic.OpenCapture.readers;

import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.MonochromeBitmapSource;

import net.filterlogic.OpenCapture.interfaces.OpenCaptureReaderException;
import java.awt.image.BufferedImage;
import net.filterlogic.OpenCapture.readers.dependencies.BufferedImageMonochromeBitmapSource;
import net.filterlogic.OpenCapture.Zone;
import net.filterlogic.OpenCapture.interfaces.IZoneReader;

import java.util.Hashtable;

/**
 *
 * @author Darron Nesbitt
 */
public class Code39Reader implements IZoneReader 
{

    private BufferedImage m_Image = null;
    private BufferedImage m_Snapshot = null;
    private Zone m_Zone = null;
    private static final String m_READER_NAME = "Code39Reader";

    public String ReadZone(Zone zone)throws OpenCaptureReaderException
    {
        String value = "";
        
        if(m_Image != null)
        {
            value = ReadZone(zone.getX(),zone.getY(),zone.getH(),zone.getW(), m_Image);
        }
        else
        {
            if(m_Snapshot != null)
            {
                value = ReadZone(m_Snapshot);
            }
            else
                throw new OpenCaptureReaderException("ReadZone(Zone) exception.  SetImage or setZoneSnapshot " +
                                                        "must be called prior to calling ReadZone!");
        }

        return value;
    }

    /**
     * Read zone if entire image is zone.
     * @param image Image to scan.
     * @return Value read from zone.
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureReaderException
     */
    private String ReadZone(BufferedImage image) throws OpenCaptureReaderException
    {
        return ReadZone(0,0,image.getHeight(),image.getWidth(),image);
    }

    /**
     * Read zone specified by coordites.
     * @param x X coordinate of zone.
     * @param y Y coordinate of zone.
     * @param height Height of zone.
     * @param width Width of zone.
     * @param image Image to scan.
     * @return Value read from zone.
     * @throws net.filterlogic.OpenCapture.interfaces.OpenCaptureReaderException
     */
    private String ReadZone(int x, int y, int height, int width, BufferedImage image) throws OpenCaptureReaderException
    {
        Hashtable<DecodeHintType, Object> hints = null;
        MonochromeBitmapSource source = null;
        BufferedImage subImage = null;

        try
        {
            // get sub image.
            subImage = image.getSubimage(x, y, width, height);

            hints = new Hashtable<DecodeHintType, Object>(3);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            source = new BufferedImageMonochromeBitmapSource(subImage);

            String result = new MultiFormatReader().decode(source, hints).getText();

            // return what was read
            return result;
        }
        catch(ReaderException re)
        {
            throw new OpenCaptureReaderException(re.toString());
        }
        finally
        {
            subImage = null;
            source = null;
            if(hints != null)
                hints.clear();
            hints = null;
        }
    }

    public String getName()
    {
        return m_READER_NAME;
    }

    public BufferedImage getZoneSnapshot()
    {
        return m_Snapshot;
    }

    public void setImage(BufferedImage image)
    {
        m_Image = image;
    }

    public void setZoneSnapshot(BufferedImage image)
    {
        m_Snapshot = image;
    }

}
