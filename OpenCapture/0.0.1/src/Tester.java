/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dnesbitt
 */
import net.filterlogic.OpenCapture.*;

public class Tester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Batch batch = new Batch();
        
        try
        {
            //batch.CreateBatch("FFE Carrier Files","Test");
            batch.getNextBatch("OCDelivery", 1);
        }
        catch(OpenCaptureException e)
        {
            System.out.println(e);
        }
    }

}
