package bsu;

import bsu.model.CascadeType;
import bsu.model.Color;
import bsu.model.PredictedModel;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.util.HashMap;

import static org.bytedeco.javacpp.opencv_imgproc.CV_FONT_HERSHEY_PLAIN;

/**
 * @author saniaky
 * @since 6/3/16
 */
public class FaceDetector {

    private static final String HAAR_CASCADE_PATH = "/haarcascades/haarcascade_frontalface_alt.xml";
    private static final String LBP_CASCADE_PATH = "/lbpcascades/lbpcascade_frontalface.xml";

    private int minFaceSize;
    private CascadeClassifier faceCascade;
    private OpenCVFaceRecognizer faceRecognizer;

    private HashMap<Integer, String> names;


    public FaceDetector() {
        faceCascade = new CascadeClassifier();
        faceRecognizer = new OpenCVFaceRecognizer();

        names = new HashMap<>();
        names.put(-1, "Unknown");
        names.put(1, "Sasha");
        names.put(2, "Oleg");
    }


    public void useCascade(CascadeType cascade) {
        String path = HAAR_CASCADE_PATH;

        if (cascade.equals(CascadeType.LBP_CASCADE_TYPE)) {
            path = LBP_CASCADE_PATH;
        }

        path = getClass().getResource(path).getPath();

        if (!faceCascade.load(path)) {
            System.err.println("Can't load cascade!");
        }
    }

    /**
     * Method for face detection and tracking
     *
     * @param frame it looks for faces in this frame
     */
    public MatOfRect faceDetectAndDisplay(Mat frame) {
        Mat gray = new Mat();

        // Convert the frame in gray scale
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

        // Equalize the frame histogram to improve the result
        Imgproc.equalizeHist(gray, gray);

        // Compute minimum face size (20% of the frame height, in our case)
        if (minFaceSize == 0) {
            int height = gray.rows();
            if (Math.round(height * 0.1f) > 0) {
                minFaceSize = Math.round(height * 0.1f);
            }
        }

        // Detect faces
        MatOfRect faces = new MatOfRect();

        double scaleFactor = 1.1;
        int minNeighbors = 2;
        Size minSize = new Size(minFaceSize, minFaceSize);
        Size maxSize = new Size();

        faceCascade.detectMultiScale(
                gray, faces, scaleFactor, minNeighbors, Objdetect.CASCADE_SCALE_IMAGE, minSize, maxSize);

        // Each rectangle in faces is a face: draw them and perform recognize
        for (Rect faceCoordinates : faces.toArray()) {
            recognizeFace(frame, faceCoordinates);
            Imgproc.rectangle(frame, faceCoordinates.tl(), faceCoordinates.br(), Color.GREEN, 2);
        }

        return faces;
    }

    private void recognizeFace(Mat frame, Rect faceCoordinates) {
        // http://answers.opencv.org/question/24670/how-can-i-align-face-images/

        int rowStart = faceCoordinates.x;
        int rowEnd = (int) Math.min(frame.size().width, rowStart + faceCoordinates.width);

        int colStart = faceCoordinates.y;
        int colEnd = (int) Math.min(frame.size().height, colStart + faceCoordinates.height);

        // Get face from frame
        Mat face = frame.submat(colStart, colEnd, rowStart, rowEnd);
        Imgproc.cvtColor(face, face, Imgproc.COLOR_BGR2GRAY);
        Imgproc.resize(face, face, new Size(256, 256));

        // Perform recognize
        PredictedModel predictedModel = faceRecognizer.recognize(face);

        // Create label
        String name = names.get(predictedModel.getLabel());
        double similarity = predictedModel.getConfidence();
        String text = String.format("%s (%.0f%%)", name, similarity);
        Point labelCoordinates = new Point(rowStart, colStart - 5);

        // Add label to frame
        Imgproc.putText(frame, text, labelCoordinates, CV_FONT_HERSHEY_PLAIN, 1.5, Color.YELLOW, 2);
    }

}
