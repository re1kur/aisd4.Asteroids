package pcg.entities;

import lombok.Getter;

@Getter
public class Circle {
    double x, y, r;
    double rSquared;

    public Circle(double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.r = radius;
        this.rSquared = this.r * this.r; // Предварительное вычисление квадрат радиуса для оптимизации
    }

    public boolean contains(Point point) {
        double d = Math.pow((point.getX() - this.x), 2) + Math.pow((point.getY() - this.y), 2);
        return d <= this.rSquared;
    }


    public boolean intersects(Rectangle range) {
        // Находим ближайшую точку на прямоугольнике к центру круга
        double nearestX = Math.max(range.getX(), Math.min(this.x, range.getX() + range.getWidth()));
        double nearestY = Math.max(range.getY(), Math.min(this.y, range.getY() + range.getHeight()));

        // Вычисляем квадрат расстояния от центра круга до ближайшей точки на прямоугольнике
        double dx = this.x - nearestX;
        double dy = this.y - nearestY;

        // Проверяем, пересекается ли круг с прямоугольником
        return (dx * dx + dy * dy) <= this.rSquared;
    }


}
