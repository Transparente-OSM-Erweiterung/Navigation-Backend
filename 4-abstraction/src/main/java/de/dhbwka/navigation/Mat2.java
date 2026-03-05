package de.dhbwka.navigation;

public class Mat2 {
    public final double a;
    public final double b;
    public final double c;
    public final double d;

    public Mat2(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public double det() {
        return a * d - b * c;
    }
}
