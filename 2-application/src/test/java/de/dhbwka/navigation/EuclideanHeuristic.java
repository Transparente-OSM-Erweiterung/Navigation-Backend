package de.dhbwka.navigation;

public class EuclideanHeuristic<T extends VectorNode> implements Heuristic<T> {

    @Override
    public double estimate(T from, T to) {
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
