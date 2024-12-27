module aisd4.Asteroids {
    requires java.desktop;
    requires static lombok;
    requires javafx.controls;

    opens pcg.entities;
    opens pcg.gameObjects;
}