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
}
