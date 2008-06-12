/*
 * tiffViewer.java
 *
 * Created on March 27, 2008, 11:30 PM
 */

package net.filterlogic.util;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.media.jai.*;
import java.awt.image.renderable.ParameterBlock;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.event.*;
import javax.media.jai.widget.*;
import javax.media.jai.PlanarImage;

import net.filterlogic.util.imaging.ToTIFF;

/**
 *
 * @author  dnesbitt
 */
public class tiffViewer extends javax.swing.JApplet implements java.awt.event.ActionListener 
{
    
    private PlanarImage source = null;
    private ImageCanvas canvas = null;

    /** Initializes the applet tiffViewer */
    public void init() {
        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    initComponents();
                    
                    process();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** This method is called from within the init() method to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public void process() {
        
        //PDF pdf = new PDF();
        
        java.awt.image.BufferedImage image = ToTIFF.loadTIFF(new java.io.File("C:\\Test\\OpenCapture\\FFE\\test_images\\615\\FFELDAL02_20080429033615_Page_01.tif"),0);

       //java.awt.image.RenderedImage ri = (java.awt.image.RenderedImage)image;
       
       //javax.media.jai.RenderedOp ro = ToTIFF.convertGrayscaleToBlackWhiteImage(ri);
       
       //image = ro.getAsBufferedImage();
        
        this.setSize(image.getWidth(),image.getHeight());
        
        source = PlanarImage.wrapRenderedImage(image);
        //source = create(256, 256);
        

        canvas = new ImageCanvas(source);
        JPanel panel = (JPanel) this.getContentPane();
        panel.setLayout(new BorderLayout());
        java.awt.Graphics g = image.getGraphics();
        ScrollPane sp = new ScrollPane();
        JButton button = new JButton("Add Constant");

        button.addActionListener(this);

        panel.add(button, BorderLayout.NORTH);
        panel.add(sp,BorderLayout.CENTER);
        sp.add(canvas);
        
        //panel.add(canvas, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        double[] val = new double[] { 64.0 };
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(source);
        pb.add(val);
        source = JAI.create("addconst", pb, null);
        canvas.set(source);
    }

    // create a test image
    public PlanarImage create(int width, int height) {
        byte[] data = new byte[width*height];

        for ( int i = 0; i < height; i++ ) {
            for ( int j = 0; j < width; j++ ) {
                data[i*width+j] = (byte)j;
            }
        }

        int bandOffsets[] = new int[] {0};

        DataBufferByte db = new DataBufferByte(data, width*height);

        WritableRaster wr = RasterFactory.createInterleavedRaster(db,
                                                                  width,
                                                                  height,
                                                                  width*1,
                                                                  1,
                                                                  bandOffsets,
                                                                  null);

        TiledImage image = new TiledImage(0, 0, width, height, 0, 0, wr.getSampleModel(), null);

        image.setData(wr);

        return image;
    }
   
}
