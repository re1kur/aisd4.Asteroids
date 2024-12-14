package entities;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 800;
    private final int BALL_COUNT = 50;

    private List<Ball> balls;
    private Quadtree quadtree;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        balls = new ArrayList<>();
        quadtree = new Quadtree(0, 0, 0, WIDTH, HEIGHT);

        Random random = new Random();
        for (int i = 0; i < BALL_COUNT; i++) {
            double radius = random.nextDouble() * 15 + 5;
            double x = random.nextDouble() * (WIDTH - 2 * radius) + radius;
            double y = random.nextDouble() * (HEIGHT - 2 * radius) + radius;
            double dx = random.nextDouble() * 4 - 2;
            double dy = random.nextDouble() * 4 - 2;
            balls.add(new Ball(x, y, radius, dx, dy));
        }

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
                draw(gc);
            }
        };

        timer.start();

        primaryStage.setTitle("Asteroids with Quadtrees");
        primaryStage.setScene(new Scene(new javafx.scene.layout.StackPane(canvas)));
        primaryStage.show();
    }

    private void update() {
        quadtree.clear();

        // Вставка шаров в квадродерево
        for (Ball ball : balls) {
            quadtree.insert(new Circle(new Point(ball.x, ball.y), ball.radius));
        }

        // Обновление движения шаров и проверка столкновений
        for (Ball ball : balls) {
            ball.update(WIDTH, HEIGHT);

            // Получение возможных столкновений
            List<Circle> potentialCollisions = new ArrayList<>();
            quadtree.retrieve(potentialCollisions, new Circle(new Point(ball.x, ball.y), ball.radius));

            // Проверка каждого столкновения
            for (Circle other : potentialCollisions) {
                Ball tempBall = new Ball(other.center.x, other.center.y, other.radius, 0, 0);
                if (ball.intersects(tempBall)) {
                    ball.resolveCollision(tempBall);
                }
            }
        }
    }


    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // Draw balls
        gc.setFill(Color.BLUE);
        for (Ball ball : balls) {
            gc.fillOval(ball.x - ball.radius, ball.y - ball.radius, ball.radius * 2, ball.radius * 2);
        }

        // Draw quadtree boundaries
        gc.setStroke(Color.RED);
        drawQuadtree(gc, quadtree);
    }

    private void drawQuadtree(GraphicsContext gc, Quadtree node) {
        if (node == null) return;

        gc.strokeRect(node.x, node.y, node.width, node.height);

        if (node.nodes[0] != null) {
            for (Quadtree child : node.nodes) {
                drawQuadtree(gc, child);
            }
        }
    }
}
