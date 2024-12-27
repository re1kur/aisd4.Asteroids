package pcg.entities;

import lombok.Getter;

@Getter
public class Rectangle {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Rectangle(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean contains(Point point) {
        return (point.getX() >= this.x && point.getX() <= this.x + this.getWidth() &&
                point.getY() >= this.y && point.getY() <= this.y + this.getHeight());
    }

    public boolean intersects(Rectangle range) {
        return !(this.x + this.width < range.x ||
                this.x > range.x + range.width ||
                this.y + this.height < range.y ||
                this.y > range.y + range.height);
    }


    public Rectangle subdivide(String quadrant) {
        switch (quadrant) {
            case "ne":
                return new Rectangle(this.x + this.width / 2, this.y, this.width / 2, this.height / 2);
            case "nw":
                return new Rectangle(this.x, this.y, this.width / 2, this.height / 2);
            case "se":
                return new Rectangle(this.x + this.width / 2, this.y + this.height / 2, this.width / 2, this.height / 2);
            case "sw":
                return new Rectangle(this.x, this.y + this.height / 2, this.width / 2, this.height / 2);
        }
        return null;
    }

    private double xDistanceFrom(Point point) {
        if (this.x <= point.getX() && point.getX() <= this.width) {
            return 0;
        }
        return Math.min(
                Math.abs(point.getX() - this.x),
                Math.abs(point.getX() - this.width)
        );
    }

    private double yDistanceFrom(Point point) {
        if (this.y <= point.getY() && point.getY() <= this.height) {
            return 0;
        }
        return Math.min(
                Math.abs(point.getY() - this.y),
                Math.abs(point.getY() - this.height)
        );
    }

    private double sqDistanceFrom(Point point) {
        double dx = this.xDistanceFrom(point);
        double dy = this.yDistanceFrom(point);
        return dx * dx + dy * dy;
    }

    public double distanceFrom(Point point) {
        return Math.sqrt(this.sqDistanceFrom(point));
    }
}
