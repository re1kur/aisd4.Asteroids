package pcg.gameObjects;

import javafx.animation.AnimationTimer;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
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
    private int maxDepth = 8;
    private int capacity = 1;
    private int countOfAsteroids = 15;
    private double boundOfVelocity = 3.0;
    private double boundOfRadius = 5.0;
    private double originOfRadius = 2.0;
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
        GridPane inputPane = new GridPane();
        inputPane.setVgap(10);
        inputPane.setHgap(10);

        TextField maxDepthField = new TextField(String.valueOf(maxDepth));
        TextField capacityField = new TextField(String.valueOf(capacity));
        TextField countOfAsteroidsField = new TextField(String.valueOf(countOfAsteroids));
        TextField boundOfVelocityField = new TextField(String.valueOf(boundOfVelocity));
        TextField boundOfRadiusField = new TextField(String.valueOf(boundOfRadius));
        TextField originOfRadiusField = new TextField(String.valueOf(originOfRadius));
        TextField widthField = new TextField(String.valueOf(screenWidth));
        TextField heightField = new TextField(String.valueOf(screenHeight));

        Button startButton = new Button("Start Game");
        inputPane.add(new Label("Max Depth:"), 0, 0);
        inputPane.add(maxDepthField, 1, 0);
        inputPane.add(new Label("Capacity:"), 0, 1);
        inputPane.add(capacityField, 1, 1);
        inputPane.add(new Label("Count of Asteroids:"), 0, 2);
        inputPane.add(countOfAsteroidsField, 1, 2);
        inputPane.add(new Label("Bound of Velocity:"), 0, 3);
        inputPane.add(boundOfVelocityField, 1, 3);
        inputPane.add(new Label("Bound of Radius:"), 0, 4);
        inputPane.add(boundOfRadiusField, 1, 4);
        inputPane.add(new Label("Origin of Radius:"), 0, 5);
        inputPane.add(originOfRadiusField, 1, 5);
        inputPane.add(new Label("Screen Width:"), 0, 6);
        inputPane.add(widthField, 1, 6);
        inputPane.add(new Label("Screen Height:"), 0, 7);
        inputPane.add(heightField, 1, 7);
        inputPane.add(startButton, 0, 8, 2, 1);

        startButton.setOnAction(_ -> {
            try {
                maxDepth = Integer.parseInt(maxDepthField.getText());
                capacity = Integer.parseInt(capacityField.getText());
                countOfAsteroids = Integer.parseInt(countOfAsteroidsField.getText());
                boundOfVelocity = Double.parseDouble(boundOfVelocityField.getText());
                boundOfRadius = Double.parseDouble(boundOfRadiusField.getText());
                originOfRadius = Double.parseDouble(originOfRadiusField.getText());
                screenWidth = Integer.parseInt(widthField.getText());
                screenHeight = Integer.parseInt(heightField.getText());

                startGame(stage);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(stage);
                alert.setTitle("Invalid Input");
                alert.setContentText("Please enter valid numbers.");
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(inputPane, 400, 350);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }

    public void startGame(Stage stage) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: BLACK");
        asteroids = new ArrayList<>();
        quadTree = new QuadTree(new pcg.entities.Rectangle(0,
                0,
                screenWidth, screenHeight), capacity, maxDepth);
        for (int i = 0; i < countOfAsteroids; i++) {
            Point p = new Point(RandomGenerator.getDefault().nextInt(screenWidth),
                    RandomGenerator.getDefault().nextInt(screenHeight));
            Asteroid asteroid = new Asteroid(p.getX(), p.getY(), p, boundOfVelocity, boundOfRadius, originOfRadius);
            asteroids.add(asteroid);
        }
        stage.setScene(new Scene(root, screenWidth, screenHeight));
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
