//import model.CascadeType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;
//import org.opencv.objdetect.Objdetect;
//
//public class FaceDetector {
//
//    private static final String HAAR_CASCADE_PATH = "haarcascades/haarcascade_frontalface_alt.xml";
//    private static final String LBP_CASCADE_PATH = "lbpcascades/lbpcascade_frontalface.xml";
//
//    private CascadeClassifier faceCascade;
//    private CascadeType cascadeType;
//    private int minFaceSize;
//
//    public FaceDetector() {
//        faceCascade = new CascadeClassifier();
//        minFaceSize = 0;
//        cascadeType = CascadeType.HAAR_CASCADE;
//    }
//
//    /**
//     * Method for face detection and tracking
//     *
//     * @param frame it looks for faces in this frame
//     */
//    public Mat faceDetectAndDisplay(Mat frame) {
//        Mat result = frame.clone();
//        Mat grayFrame = new Mat();
//
//        // convert the frame in gray scale
//        Imgproc.cvtColor(result, grayFrame, Imgproc.COLOR_BGR2GRAY);
//
//        // equalize the frame histogram to improve the result
//        Imgproc.equalizeHist(grayFrame, grayFrame);
//
//        // compute minimum face size (20% of the frame height, in our case)
//        if (minFaceSize == 0) {
//            int height = grayFrame.rows();
//            if (Math.round(height * 0.2f) > 0) {
//                minFaceSize = Math.round(height * 0.2f);
//            }
//        }
//
//        // detect faces
//        loadCascade();
//        MatOfRect faces = new MatOfRect();
//        faceCascade.detectMultiScale(
//                grayFrame, faces, 1.1, 2, Objdetect.CASCADE_SCALE_IMAGE,
//                new Size(minFaceSize, minFaceSize), new Size());
//
//        // each rectangle in faces is a face: draw them!
//        Rect[] facesCoordinates = faces.toArray();
//        for (Rect faceCoordinates : facesCoordinates) {
//            Scalar green = new Scalar(0, 255, 0);
//            Imgproc.rectangle(result, faceCoordinates.tl(), faceCoordinates.br(), green, 2);
//        }
//
//        return result;
//    }
//
//    private void loadCascade() {
//        String path = "";
//
//        if (cascadeType.equals(CascadeType.HAAR_CASCADE)) {
//            path = HAAR_CASCADE_PATH;
//        } else if (cascadeType.equals(CascadeType.LBP_CASCADE_TYPE)) {
//            path = LBP_CASCADE_PATH;
//        }
//
//        path = getClass().getResource(path).getPath();
//
//        if (!faceCascade.load(path)) {
//            System.err.println("Can't load cascade!");
//        }
//    }
//
//    public void setCascadeType(CascadeType cascadeType) {
//        this.cascadeType = cascadeType;
//    }
//
//}