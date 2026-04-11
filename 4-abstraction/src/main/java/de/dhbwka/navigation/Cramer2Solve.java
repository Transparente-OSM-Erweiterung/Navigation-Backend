package de.dhbwka.navigation;

public abstract class Cramer2Solve {
    public static double solveX(Mat2 mat, Vec2 rhs) {
        double det = mat.det();
        if (det == 0) throw new IllegalArgumentException("Matrix is singular");
        return (rhs.x * mat.d - mat.b * rhs.y) / det;
    }
}
