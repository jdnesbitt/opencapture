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

package net.filterlogic.imaging;

import javax.media.jai.PlanarImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.RenderedImage;
import javax.media.jai.operator.SubsampleAverageDescriptor;
import java.awt.RenderingHints;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;

public class ScaleImage
{
  public static PlanarImage scaleImage(PlanarImage orig, int maxW, int maxH) 
  {
    double hScale = 1.0F, wScale = 1.0F;
    Double dScale;
    if (orig != null)
    {
      int w = orig.getWidth();
      int h = orig.getHeight();

//      if (w > maxW || h > maxH)
//      {
//        if (w > maxW)
          wScale = (double) maxW / (double) w;

        //if (h > maxH)
          hScale = (double) maxH / (double) h;

        dScale = new Double( wScale > hScale ? hScale : wScale);
        float sc = dScale.floatValue();

        ParameterBlock pb = new ParameterBlock();
        pb.addSource(orig);
        //pb.add(null).add(null).add(null).add(null).add(null);
        pb.add(sc).add(sc).add(0.0F).add(0.0F).add(new InterpolationNearest());

        //RenderedImage ren = (RenderedImage) orig;
        //RenderingHints xx = new RenderingHints(null);
        //SubsampleAverageDescriptor sad = new SubsampleAverageDescriptor();

        //RenderedOp ro = sad.create(ren, dScale, dScale, null);

        //PlanarImage planrImg = ro.getRendering();
        PlanarImage planrImg = JAI.create("scale", pb);

        return planrImg;
//      }
    }
    return (orig);
  }
}