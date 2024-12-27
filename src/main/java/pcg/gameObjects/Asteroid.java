package pcg.gameObjects;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;
import pcg.entities.Point;
import pcg.entities.Rectangle;

import java.util.List;
import java.util.random.RandomGenerator;

@Getter
@Setter
public class Asteroid extends Point {
    private boolean highlight;
    private double r;
    private double velocityX;
    private double velocityY;
    private double mass;

    public Asteroid(double x, double y, Point userData) {
        super(x, y, userData);
        r = RandomGenerator.getDefault().nextDouble(3.0, 5.0);
        mass = Math.PI * r * r;
        velocityY = RandomGenerator.getDefault().nextDouble(-3.0,3.0);
        velocityX = RandomGenerator.getDefault().nextDouble(-3.0,3.0);
    }

    public void move() {
        checkBoundaries();
        this.x += velocityX;
        this.y += velocityY;
        Game.getQuadTree().update(this);
    }

    public void checkBoundaries() {
        int screenWidth = Game.getScreenWidth();
        int screenHeight = Game.getScreenHeight();

        if (x - r < 0) {
            x = r;
            velocityX *= -1;
        }

        else if (x + r > screenWidth) {
            x = screenWidth - r;
            velocityX *= -1;
        }

        if (y - r < 0) {
            y = r;
            velocityY *= -1;
        }

        else if (y + r > screenHeight) {
            y = screenHeight - r;
            velocityY *= -1;
        }
    }


    public void render(Pane pane) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(r);
        circle.setFill(Color.WHITE);
        pane.getChildren().add(circle);
    }

    public void checkCollisions(List<Asteroid> others) {
        if (others == null) {
            return;
        }
        for (Asteroid other : others) {
            if (this != other && this.intersects(other)) {
                this.collide(other);
            }
        }
    }

    private void collide(Asteroid other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;

        double distanceSquared = dx * dx + dy * dy;

        if (distanceSquared < 1e-10) {
            return;
        }

        double dvx = other.velocityX - this.velocityX;
        double dvy = other.velocityY - this.velocityY;

        double dotProduct = (dvx * dx + dvy * dy) / distanceSquared;

        double m1 = this.mass;
        double m2 = other.mass;

        double coefficient1 = (2 * m2 / (m1 + m2)) * dotProduct;
        this.velocityX += coefficient1 * dx;
        this.velocityY += coefficient1 * dy;

        double coefficient2 = (2 * m1 / (m1 + m2)) * dotProduct;
        other.velocityX -= coefficient2 * dx;
        other.velocityY -= coefficient2 * dy;
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, r * 2, r * 2);
    }
    public double sqDistanceFrom(Asteroid other) {
        double dx = other.x - this.x;
        double dy = other.y - this.y;
        return dx * dx + dy * dy;
    }

    public double distanceFrom(Asteroid other) {
        return Math.sqrt(this.sqDistanceFrom(other));
    }

    public boolean intersects(Asteroid other) {
        double d = this.distanceFrom(other);
        return d < (this.r + other.r);
    }
}
