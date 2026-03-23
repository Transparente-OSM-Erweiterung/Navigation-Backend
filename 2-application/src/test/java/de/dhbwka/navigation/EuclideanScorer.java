package de.dhbwka.navigation;

public class EuclideanScorer<T extends VectorNode> implements Scorer<T> {

    @Override
    public double computeCost(T from, T to) {
        double[] a = from.getComponents();
        double[] b = to.getComponents();

        if (a.length != b.length) throw new IllegalArgumentException("Dimension mismatch");

        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}
