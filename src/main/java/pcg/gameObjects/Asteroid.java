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
    private double accelerationX;
    private double accelerationY;
    private double mass;

    public Asteroid(double x, double y, Point userData) {
        super(x, y, userData);
        highlight = false;
        r = RandomGenerator.getDefault().nextDouble(2.0, 6.0);
        mass = Math.PI * r * r;
        velocityY = RandomGenerator.getDefault().nextDouble(-3.0,3.0);
        velocityX = RandomGenerator.getDefault().nextDouble(-3.0,3.0);
        accelerationX = RandomGenerator.getDefault().nextDouble(-3.0,3.0);
        accelerationY = RandomGenerator.getDefault().nextDouble(-3.0,3.0);
    }

    public void move() {
        checkBoundaries();
        this.x += velocityX;
        this.y += velocityY;

    }

    public void checkBoundaries() {
        int screenWidth = Game.getScreenWidth();
        int screenHeight = Game.getScreenHeight();

        if (x - r < 0) {
            x = r;
            velocityX = -velocityX;
        }

        else if (x + r > screenWidth) {
            x = screenWidth - r;
            velocityX = -velocityX;
        }

        if (y - r < 0) {
            y = r;
            velocityY = -velocityY;
        }

        else if (y + r > screenHeight) {
            y = screenHeight - r;
            velocityY = -velocityY;
        }
    }


    public void render(Pane pane) {
        Circle circle = new Circle();
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(r);
        circle.setFill(highlight ? Color.TOMATO : Color.BLUEVIOLET);
        pane.getChildren().add(circle);
        highlight = false;
    }

    public void checkCollisions(List<Asteroid> others) {
        if (others == null) {
            return;
        }
        this.highlight = false;
        for (Asteroid other : others) {
            if (other.getUserData() != null) {
                other = (Asteroid) other.getUserData();
            }
            if (this != other) {
                double d = distanceFrom(other);
                if (d < other.r / 2 + this.r / 2) {
                    this.highlight = true;
                    other.highlight = true;
                }
            }
        }
    }

//    public double dist(double x1, double y1, double x2, double y2) {
//        double dx = x1 - x2;
//        double dy = y1 - y2;
//        return Math.sqrt(dx * dx + dy * dy);
//    }

    public boolean intersects(Asteroid other) {
        double d =distanceFrom(other);
        return d < other.r + this.r;
    }

    public Rectangle getBoundary() {
        return new Rectangle(x, y, (int) (r * 2), (int) (r * 2));
    }


}
