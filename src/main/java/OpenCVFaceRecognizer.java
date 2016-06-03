import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.MatVector;
import static org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * @author saniaky
 * @since 5/25/16
 */
public class OpenCVFaceRecognizer {

    private static final String TRAINING_DIR = "src/main/resources/training-set";
    private static FaceRecognizer faceRecognizer;

    public OpenCVFaceRecognizer() {
        initializeFaceRecognizer();
    }

    public static void main(String[] args) {
        initializeFaceRecognizer();

        HashMap<Integer, String> names = new HashMap<>();
        names.put(1, "Sasha");
        names.put(2, "Oleg");

        String imageToRecognize = "src/main/resources/face4.bmp";

        Mat testImage = imread(imageToRecognize, CV_LOAD_IMAGE_GRAYSCALE);
        int predictedLabel = faceRecognizer.predict(testImage);
        System.out.println("Predicted: " + names.get(predictedLabel));
    }

    public int recognize(Mat face) {
        return faceRecognizer.predict(face);
    }

    public int recognize(org.opencv.core.Mat face) {
        Mat javaCVFace = new opencv_core.Mat() { { address = face.getNativeObjAddr(); } };
        return faceRecognizer.predict(javaCVFace);
    }

    private static void initializeFaceRecognizer() {
        File root = new File(TRAINING_DIR);

        File[] imageFiles = root.listFiles((dir, name) -> {
            name = name.toLowerCase();
            return name.endsWith(".jpg") || name.endsWith(".bmp") || name.endsWith(".png") || name.endsWith(".pgm");
        });

        MatVector images = new MatVector(imageFiles.length);

        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.getIntBuffer();

        int counter = 0;
        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            int label = Integer.parseInt(image.getName().split("\\-")[0]);

            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }

        faceRecognizer = createEigenFaceRecognizer();
//        faceRecognizer = createFisherFaceRecognizer();
        // faceRecognizer = createLBPHFaceRecognizer()

        faceRecognizer.train(images, labels);
    }

    public static FaceRecognizer getFaceRecognizer() {
        return faceRecognizer;
    }
}