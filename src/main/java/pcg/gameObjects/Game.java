package pcg.gameObjects;

import javafx.animation.AnimationTimer;
import javafx.geometry.NodeOrientation;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import pcg.entities.Point;
import pcg.entities.QuadTree;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public class Game extends Application {
    private final int maxDepth = 8;
    private final int capacity = 1;
    private final int countOfAsteroids = 50;
    @Getter
    private static QuadTree quadTree;
    private List<Asteroid> asteroids;
    @Getter
    @Setter
    private static int screenWidth = 960;
    @Getter
    @Setter
    private static int screenHeight = 540;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: BLACK");
        asteroids = new ArrayList<>();
        quadTree = new QuadTree(new pcg.entities.Rectangle(0,
                0,
                screenWidth, screenHeight), capacity, maxDepth);
        for (int i = 0; i < countOfAsteroids; i++) {
            Point p = new Point(RandomGenerator.getDefault().nextInt(screenWidth),
                    RandomGenerator.getDefault().nextInt(screenHeight));
            Asteroid asteroid = new Asteroid(p.getX(), p.getY(), p);
            asteroids.add(asteroid);
        }
        stage.setScene(new Scene(root, screenWidth, screenHeight));
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                quadTree.clear();
                root.getChildren().clear();

                for (Asteroid a : asteroids) {
                    a.move();
                    quadTree.insert(a);
                    a.render(root);
                    List<Asteroid> found = quadTree.query(a.getBoundary(), new ArrayList<>());
                    a.checkCollisions(found);
                }
                drawQuadTree(root, quadTree);
                }
        };
        timer.start();
    }

    private void drawQuadTree(Pane pane, QuadTree tree) {
        pcg.entities.Rectangle boundary = tree.getBoundary();

        Rectangle rectangle = new Rectangle();

        rectangle.setX(boundary.getX());
        rectangle.setY(boundary.getY());
        rectangle.setWidth(boundary.getWidth());
        rectangle.setHeight(boundary.getHeight());

        rectangle.setStrokeWidth(0.3);
        rectangle.setStroke(Color.LIMEGREEN);
        rectangle.setFill(Color.TRANSPARENT);

        pane.getChildren().add(rectangle);

        if (tree.getNorthWest() != null) drawQuadTree(pane, tree.getNorthWest());
        if (tree.getNorthEast() != null) drawQuadTree(pane, tree.getNorthEast());
        if (tree.getSouthWest() != null) drawQuadTree(pane, tree.getSouthWest());
        if (tree.getSouthEast() != null) drawQuadTree(pane, tree.getSouthEast());
    }

}
