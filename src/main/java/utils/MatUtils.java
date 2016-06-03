package utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.opencv.imgcodecs.Imgcodecs.imencode;

/**
 * @author saniaky
 * @since 3/20/16
 */
public abstract class MatUtils {

    private MatUtils() {
    }

    public static void saveImage(Image image, final String name) {
        BufferedImage img = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(img, "bmp", new File("/Users/saniaky/" + name + ".bmp"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImage(RenderedImage bufferedImage, final String name) {
        File outputfile = new File("/Users/saniaky/" + name + ".bmp");
        try {
            ImageIO.write(bufferedImage, "bmp", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveImage(InputStream imgStream, final String name) {
        try {
            FileOutputStream fileInputStream = new FileOutputStream("/Users/saniaky/" + name + ".bmp");
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = imgStream.read(bytes)) != -1) {
                fileInputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveImage(Mat frame, final String name) {
        Imgcodecs.imwrite("/Users/saniaky/" + name + ".bmp", frame);
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     */
    public static Image mat2Image(Mat frame) {
        MatOfByte buffer = new MatOfByte();

        imencode(".bmp", frame, buffer);

//        long startTime = System.currentTimeMillis();

//        toBufferedImage(frame);

//        long endTime = System.currentTimeMillis();
//        String executionTime = String.valueOf(endTime - startTime);
//        Scalar green = new Scalar(0, 255, 0);
//        Imgproc.putText(frame, executionTime, new Point(100, 100), 2, 2, green, 2);

        BufferedImage bufferedImage = toBufferedImage(frame);

        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    /**
     * Converts/writes a Mat into a BufferedImage.
     * Execution time: ~3 msec
     *
     * @param matrix Mat of type CV_8UC3 or CV_8UC1
     * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
     */
    public static BufferedImage matToBufferedImage(Mat matrix) {
        int cols = matrix.cols();
        int rows = matrix.rows();
        int elemSize = (int) matrix.elemSize();
        byte[] data = new byte[cols * rows * elemSize];
        int type;

        matrix.get(0, 0, data);

        switch (matrix.channels()) {
            case 1:
                type = BufferedImage.TYPE_BYTE_GRAY;
                break;

            case 3:
                type = BufferedImage.TYPE_3BYTE_BGR;

                // bgr to rgb
                byte b;
                for (int i = 0; i < data.length; i = i + 3) {
                    b = data[i];
                    data[i] = data[i + 2];
                    data[i + 2] = b;
                }
                break;

            default:
                return null;
        }

        BufferedImage image = new BufferedImage(cols, rows, type);
        image.getRaster().setDataElements(0, 0, cols, rows, data);

        return image;
    }

    /**
     * Execution time: ~1 msec
     */
    public static BufferedImage toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;

        if (m.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = m.channels() * m.cols() * m.rows();
        byte[] b = new byte[bufferSize];
        m.get(0, 0, b); // get all the pixels

        BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);

        return image;
    }


    /**
     * Processing time: ~14 msec
     */
    public static BufferedImage toBufferedImage2(Mat image) {
        Mat imageTmp = image.clone();

        MatOfByte matOfByte = new MatOfByte();
        imencode(".bmp", imageTmp, matOfByte);

        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage = null;

        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bufImage;
    }

}
