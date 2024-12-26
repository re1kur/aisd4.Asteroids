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
                screenWidth, screenHeight), 1, 1);
        for (int i = 0; i < 67; i++) {
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
                    try {
                        quadTree.insert(a);
                    } catch (Exception e) {
                        throw  new RuntimeException(e);
                    }
                    a.move();
                    a.render(root);
                }
                drawQuadTree(root, quadTree);
                }
        };
        timer.start();
    }

    private void drawQuadTree(Pane pane, QuadTree tree) {
        // Получаем границы квадродерева
        pcg.entities.Rectangle boundary = tree.getBoundary();

        // Создаем новый прямоугольник для отображения
        Rectangle rectangle = new Rectangle();

        // Устанавливаем координаты верхнего левого угла
        rectangle.setX(boundary.getX()); // Используем left для X
        rectangle.setY(boundary.getY());   // Используем top для Y
        rectangle.setWidth(boundary.getWidth()); // Ширина прямоугольника
        rectangle.setHeight(boundary.getHeight()); // Высота прямоугольника

        // Настройки стиля
        rectangle.setStrokeWidth(0.3);
        rectangle.setStroke(Color.SKYBLUE);
        rectangle.setFill(Color.TRANSPARENT);

        // Добавляем прямоугольник на панель
        pane.getChildren().add(rectangle);

        // Рекурсивно рисуем дочерние узлы, если они существуют
        if (tree.getNorthWest() != null) drawQuadTree(pane, tree.getNorthWest());
        if (tree.getNorthEast() != null) drawQuadTree(pane, tree.getNorthEast());
        if (tree.getSouthWest() != null) drawQuadTree(pane, tree.getSouthWest());
        if (tree.getSouthEast() != null) drawQuadTree(pane, tree.getSouthEast());
    }

}
