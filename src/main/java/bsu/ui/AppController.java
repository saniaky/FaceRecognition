package bsu.ui;

import bsu.Camera;
import bsu.FaceDetector;
import bsu.model.CascadeType;
import bsu.utils.MatUtils;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppController {

    /* UI Controls */
    public CheckBox haarCheckbox;
    public CheckBox lbpCheckbox;
    public ImageView currentFrame;
    public Button button;

    private boolean cameraActive;
    private ScheduledExecutorService timer;

    private Camera camera;
    private FaceDetector faceDetector;

    public AppController() {
        cameraActive = false;
        camera = new Camera();
        faceDetector = new FaceDetector();
    }

    public void startCamera() throws InterruptedException {
        if (cameraActive) {
            timer.shutdown();
            timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            camera.stop();
            currentFrame.setImage(null);

            button.setText("Start video.Camera");
        } else {
            camera.start();

            // grab a frame every 33 ms (30 frames/sec)
            Runnable frameGrabber = () -> {
                Mat frame = camera.getFrame();

                faceDetector.faceDetectAndDisplay(frame);
                Image image = MatUtils.mat2Image(frame);

                Platform.runLater(() -> currentFrame.setImage(image));
            };

            timer = Executors.newSingleThreadScheduledExecutor();
            timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

            button.setText("Stop video.");
        }
        cameraActive = !cameraActive;
    }


    public void haarSelected() {
        lbpCheckbox.setSelected(false);
        faceDetector.useCascade(CascadeType.HAAR_CASCADE);
    }

    public void lbpSelected() {
        haarCheckbox.setSelected(false);
        faceDetector.useCascade(CascadeType.LBP_CASCADE_TYPE);
    }

}
