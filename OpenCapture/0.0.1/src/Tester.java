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
        try
        {
            Batch batch = new Batch();

            //batch.CreateBatch("FFE Carrier Files","Test");
            batch.getNextBatch("OCDelivery");
            
            System.out.println(batch.getBatchName());
            
            
        }
        catch(OpenCaptureException e)
        {
            System.out.println(e);
        }
    }

}
