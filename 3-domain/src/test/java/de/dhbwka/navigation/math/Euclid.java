package de.dhbwka.navigation.math;

public class Euclid {
    public static double distance(double[] a, double[] b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Dimension mismatch");
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double d = a[i] - b[i];
            sum += d * d;
        }
        return Math.sqrt(sum);
    }
}