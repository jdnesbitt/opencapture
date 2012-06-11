
import javax.media.jai.JAI;
import javax.media.jai.TiledImage;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.ComponentColorModel;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;
import javax.media.jai.RasterFactory;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.TIFFEncodeParam;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateTIFF {

    private ByteArrayOutputStream stream = null;

    public CreateTIFF() {
    }

//Takes in the text to be displayed in the image.
    public CreateTIFF(String[] text) {
        TiledImage image = null;
        Graphics2D graphics = null;
        SampleModel model = null;
        ImageCodec codec = null;
        TIFFEncodeParam param = null;
        int yIndex = 30;
        HashMap map = null;
        int fontSize = 30;

        model = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, 2400, 3000, 1);
        image = new TiledImage(0, 0, 2000, 2500, 0, 0,
                model,
                ImageCodec.createGrayIndexColorModel(model, false));

        graphics = image.createGraphics();

        map = new HashMap();
        map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHints(map);
        graphics.setFont(new Font(null, Font.PLAIN, fontSize));

        for (int i = 0; i < text.length; i++) {
            graphics.drawString(text[i], 50, yIndex);
            yIndex += fontSize + 5;
        }

        stream = new ByteArrayOutputStream();

//Setting the compression to be used.
        param = new TIFFEncodeParam();
        param.setCompression(TIFFEncodeParam.COMPRESSION_GROUP4);

        JAI.create("encode", image, stream, "TIFF", param);
    }

    public static void main(String args[]) {
        FileOutputStream file = null;
        CreateTIFF image = null;

        if (args.length < 1) {
            System.out.println("Enter the String to be created");
            System.exit(-1);
        }

//Create the byte Stream of the image to be display.
        image = new CreateTIFF(args);

//Store the image in a file.
        try {
            file = new FileOutputStream("image.tiff");
            file.write(image.getStream().toByteArray());
            file.close();
        } catch (IOException ioExp) {
            ioExp.printStackTrace();
            System.out.println("The error " + ioExp.toString());
        }
    }

    public ByteArrayOutputStream getStream() {
        return stream;
    }

    public void setStream(ByteArrayOutputStream stream) {
        this.stream = stream;
    }
}
