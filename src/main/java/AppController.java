import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.CascadeType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;
import utils.MatUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

public class AppController {

    private static final String HAAR_CASCADE_PATH = "haarcascades/haarcascade_frontalface_alt.xml";
    private static final String LBP_CASCADE_PATH = "lbpcascades/lbpcascade_frontalface.xml";

    /* UI Controls */
    public CheckBox haarCheckbox;
    public CheckBox lbpCheckbox;
    public ImageView currentFrame;
    public Button button;

    private boolean cameraActive;
    private VideoCapture capture;
    private ScheduledExecutorService timer;
    private CascadeClassifier faceCascade;
    private CascadeType cascadeType;
    private int minFaceSize;

    public AppController() {
        cameraActive = false;
        faceCascade = new CascadeClassifier();
        capture = new VideoCapture();

    }

    public void startCamera() throws InterruptedException {
        if (cameraActive) {
            timer.shutdown();
            timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            capture.release();

            currentFrame.setImage(null);

            button.setText("Start Camera");
        } else {
            if (!capture.open(0)) {
                System.err.println("Video stream not available!");
            }

            // Let camera time to initialize
            Thread.sleep(1000);

            capture.set(CV_CAP_PROP_FRAME_WIDTH, 640);
            capture.set(CV_CAP_PROP_FRAME_HEIGHT, 480);

            // grab a frame every 33 ms (30 frames/sec)
            Runnable frameGrabber = () -> {
                Image value = grabFrame();
                Platform.runLater(() -> currentFrame.setImage(value));
            };
            timer = Executors.newSingleThreadScheduledExecutor();
            timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            button.setText("Stop Camera");
        }
        cameraActive = !cameraActive;
    }

    private Image grabFrame() {
        Image imageToShow = null;
        Mat frame = new Mat();

        if (!capture.read(frame)) {
            System.err.println("Cannot read frame!");
        }

        // if the frame is not empty, process it
        if (!frame.empty()) {
            faceDetectAndDisplay(frame);
            imageToShow = MatUtils.mat2Image(frame);
        }

        return imageToShow;
    }

    public void haarSelected() {
        lbpCheckbox.setSelected(false);
        cascadeType = CascadeType.HAAR_CASCADE;
        loadCascade();
    }

    public void lbpSelected() {
        haarCheckbox.setSelected(false);
        cascadeType = CascadeType.LBP_CASCADE_TYPE;
        loadCascade();
    }

    /**
     * Method for face detection and tracking
     *
     * @param frame it looks for faces in this frame
     */
    public void faceDetectAndDisplay(Mat frame) {
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (20% of the frame height, in our case)
        if (minFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.1f) > 0) {
                minFaceSize = Math.round(height * 0.1f);
            }
        }

        // detect faces
        MatOfRect faces = new MatOfRect();
        faceCascade.detectMultiScale(
                grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
                new Size(minFaceSize, minFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Scalar green = new Scalar(0, 255, 0);
        for (Rect faceCoordinates : faces.toArray()) {
            Imgproc.rectangle(frame, faceCoordinates.tl(), faceCoordinates.br(), green, 2);
        }
    }

    private void loadCascade() {
        String path = "";

        if (cascadeType.equals(CascadeType.HAAR_CASCADE)) {
            path = HAAR_CASCADE_PATH;
        } else if (cascadeType.equals(CascadeType.LBP_CASCADE_TYPE)) {
            path = LBP_CASCADE_PATH;
        }

        path = getClass().getResource(path).getPath();

        if (!faceCascade.load(path)) {
            System.err.println("Can't load cascade!");
        }
    }

}
