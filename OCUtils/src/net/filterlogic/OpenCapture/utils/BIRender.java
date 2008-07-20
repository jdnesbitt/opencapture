/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.filterlogic.OpenCapture.utils;

/**
 *
 * @author dnesbitt
 */
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;
 
public class BIRender 
{
    
    public static void main(String[] args) throws IOException 
    {
        File file = new File("temp.jpeg");
        createSampleFile(file);
        BufferedImage image = ImageIO.read(file);

        Icon icon = new ImageIcon(image);

        JLabel label = new JLabel(icon);
 
        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run() 
            {
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
 
    static void createSampleFile(File file) throws IOException {
        BufferedImage bi = new BufferedImage(300, 60, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.GREEN);
        g.setFont(new Font("Lucida Sans Typewriter", Font.PLAIN, 36));
        g.drawString("Hello, World!", 10, 50);
        g.dispose();
        file.delete();
        boolean result = ImageIO.write(bi, "jpeg", file);
        if (!result)
            throw new IOException("write fails to find jpeg writer");
    }
}

