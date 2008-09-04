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

package net.filterlogic.OpenCapture.interfaces;

/**
 * The IZoneReader interface defines a set if methods that all 
 * OpenCapture zone readers should implement.
 * 
 * @author Darron Nesbitt
 */
public interface IZoneReader 
{
    /**
     * Read the zone identified in the Zone object.
     * @param zone Zone object containg zone to read.
     * @return String containing the value read from the zone.
     */
    public String ReadZone(net.filterlogic.OpenCapture.Zone zone) throws OpenCaptureReaderException;

    /**
     * Get the name of the zone reader.
     * @return Name of the zone reader.
     */
    public String getName();

    /**
     * Set the image to be read.  Use this method to pass the entire image.  Especially usefull when multiple zones
     * exist on the same image.
     * 
     * @param image BufferedImage
     */
    public void setImage(java.awt.image.BufferedImage image);

    /** 
     * Set the snippet of an image.  Use this method to pass zone snapshot.
     * @param image Zone snapshot.
     */
    public void setZoneSnapshot(java.awt.image.BufferedImage image);

    /**
     * Get a snapshot of the zone being read.
     * 
     * @return BufferedImage containing a snapshot of the zone.
     */
    public java.awt.image.BufferedImage getZoneSnapshot();

}
