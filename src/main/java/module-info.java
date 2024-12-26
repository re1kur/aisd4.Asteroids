module aisd4.Asteroids {
    requires javafx.graphics;
    requires java.desktop;
    requires static lombok;

    opens pcg.entities;
    opens pcg.gameObjects;
}