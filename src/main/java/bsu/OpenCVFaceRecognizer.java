package bsu;

import bsu.model.PredictedModel;
import org.bytedeco.javacpp.opencv_core;

import java.io.File;
import java.nio.IntBuffer;

import static org.bytedeco.javacpp.opencv_core.CV_32SC1;
import static org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_core.MatVector;
import static org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * @author saniaky
 * @since 5/25/16
 */
public class OpenCVFaceRecognizer {

    private static final String TRAINING_DIR = "src/main/resources/training-set";
    private FaceRecognizer faceRecognizer;

    public OpenCVFaceRecognizer() {
        initializeFaceRecognizer();
    }

    public int recognize(Mat face) {
        return faceRecognizer.predict(face);
    }

    public PredictedModel recognize(org.opencv.core.Mat face) {
        Mat javaCVFace = new opencv_core.Mat() { { address = face.getNativeObjAddr(); } };
        int[] labels = new int[10];
        double[] confidence = new double[10];
        faceRecognizer.predict(javaCVFace, labels, confidence);
        return new PredictedModel(labels, confidence);
    }

    private void initializeFaceRecognizer() {
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

        // keep 10 Eigenfaces
        int numComponents = 10;

        // any distance greater than that is ignored
        double threshold = 12000.0;

//        faceRecognizer = createEigenFaceRecognizer(numComponents, threshold);
        faceRecognizer = createFisherFaceRecognizer(numComponents, threshold);
        // faceRecognizer = createLBPHFaceRecognizer()

        faceRecognizer.train(images, labels);
    }
}