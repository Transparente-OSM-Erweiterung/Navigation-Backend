package de.dhbwka.navigation.math;


public class Vec2 {
    public static final Vec2 ZERO = new Vec2(0.0F, 0.0F);
    public static final Vec2 ONE = new Vec2(1.0F, 1.0F);
    public static final Vec2 UNIT_X = new Vec2(1.0F, 0.0F);
    public static final Vec2 NEG_UNIT_X = new Vec2(-1.0F, 0.0F);
    public static final Vec2 UNIT_Y = new Vec2(0.0F, 1.0F);
    public static final Vec2 NEG_UNIT_Y = new Vec2(0.0F, -1.0F);
    public static final Vec2 MAX = new Vec2(Double.MAX_VALUE, Double.MAX_VALUE);
    public static final Vec2 MIN = new Vec2(Double.MIN_VALUE, Double.MIN_VALUE);
    public final double x;
    public final double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 scale(double factor) {
        return new Vec2(this.x * factor, this.y * factor);
    }

    public double dot(Vec2 other) {
        return this.x * other.x + this.y * other.y;
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(this.x + other.x, this.y + other.y);
    }

    public Vec2 add(double value) {
        return new Vec2(this.x + value, this.y + value);
    }

    public boolean equals(Vec2 other) {
        return this.x == other.x && this.y == other.y;
    }

    public Vec2 normalized() {
        double f = Math.sqrt(this.x * this.x + this.y * this.y);
        return new Vec2(this.x / f, this.y / f);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public double distanceToSqr(Vec2 other) {
        double f = other.x - this.x;
        double f1 = other.y - this.y;
        return f * f + f1 * f1;
    }

    public Vec2 negated() {
        return new Vec2(-this.x, -this.y);
    }

    public Vec2 rot90right() {
        return new Vec2(this.y, -this.x);
    }

    public Vec2 rot90left() {
        return new Vec2(-this.y, this.x);
    }
}