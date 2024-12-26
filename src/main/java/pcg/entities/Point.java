package pcg.entities;

import lombok.Getter;

@Getter
public class Point {
    protected double x;
    protected double y;
    private Point userData;

    public Point(double x, double y, Point userData) {
        this.x = x;
        this.y = y;
        this.userData = userData;
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    private double sqDistanceFrom(Point other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return dx * dx + dy * dy;
    }

    public double distanceFrom(Point other) {
        return Math.sqrt(this.sqDistanceFrom(other));
    }
}
