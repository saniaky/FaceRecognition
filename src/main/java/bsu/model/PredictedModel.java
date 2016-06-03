package bsu.model;

/**
 * @author saniaky
 * @since 6/3/16
 */
public class PredictedModel {
    // Total match = 0, for our case it's 100
    private static final double SIM_MAX = 100.0;

    // Total mismatch = infinite, for our case it's 20.000
    private static final double DIS_MAX = 12000.0;

    private int[] labels;
    private double[] distance;

    public PredictedModel() {
        labels = new int[10];
        distance = new double[10];
    }

    public PredictedModel(int[] labels, double[] distance) {
        this.labels = labels;
        this.distance = distance;
    }

    /**
     * http://stackoverflow.com/questions/25683514/how-to-calculate-percentage-format-prediction-confidence-of-face-recognition-usi
     * http://w3.impa.br/~lenka/IP/IP_project.html
     */
    public double getConfidence() {
        if (getLabel() == -1) {
            return 0;
        }
        return SIM_MAX - SIM_MAX / DIS_MAX * getDistance();
    }

    public int getLabel() {
        return labels[0];
    }

    public double getDistance() {
        return distance[0];
    }
}
