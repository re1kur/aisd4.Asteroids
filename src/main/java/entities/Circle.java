package entities;

public class Circle {
    public Point center;
    public double radius;

    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    public boolean intersects(Circle other) {
        double dx = this.center.x - other.center.x;
        double dy = this.center.y - other.center.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= this.radius + other.radius;
    }
}
