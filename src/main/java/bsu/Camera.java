package bsu;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

/**
 * @author saniaky
 * @since 6/3/16
 */
public class Camera {

    private VideoCapture capture;

    public Camera() {
        capture = new VideoCapture();
    }

    public Mat getFrame() {
        Mat frame = new Mat();
        if (!capture.read(frame)) {
            System.err.println("Cannot read frame!");
        }
        return frame;
    }

    public void stop() {
        capture.release();
    }

    public void start() {
        if (!capture.open(0)) {
            System.err.println("Video stream not available!");
        }

        capture.set(CV_CAP_PROP_FRAME_WIDTH, 640);
        capture.set(CV_CAP_PROP_FRAME_HEIGHT, 480);

        // Let camera time to initialize
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
