package entities;

public class Ball {
    public double x;
    public double y;
    public double radius;
    double dx;
    double dy;

    public Ball(double x, double y, double radius, double dx, double dy) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
    }

    public void update(double width, double height) {
        x += dx;
        y += dy;

        if (x - radius < 0 || x + radius > width) {
            dx = -dx;
        }
        if (y - radius < 0 || y + radius > height) {
            dy = -dy;
        }
    }

    public boolean intersects(Ball other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= this.radius + other.radius;
    }

    public void resolveCollision(Ball other) {
        this.dx = -this.dx;
        this.dy = -this.dy;
    }
}
