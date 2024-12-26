package pcg.entities;

import lombok.Getter;
import pcg.gameObjects.Asteroid;

import java.util.ArrayList;
import java.util.List;

@Getter
public class QuadTree {
    private final int DEFAULT_CAPACITY = 1;
    private final int MAX_DEPTH = 8;
    private int capacity;
    private int depth;
    private final Rectangle boundary;
    private QuadTree northWest, northEast, southWest, southEast;
    private List<Asteroid> points;
    private boolean divided;

    public QuadTree(Rectangle boundary, int capacity, int depth) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.depth = depth;
        this.points = new ArrayList<>();
        this.divided = false;
    }

    public QuadTree[] getChildren() {
        if (this.divided) {
            return new QuadTree[]{northWest, northEast, southWest, southEast};
        }
        return new QuadTree[0];
    }

    public void clear() {
        this.points.clear(); // Очищаем точки в текущем узле

        if (this.divided) {
            // Рекурсивно очищаем дочерние узлы
            if (northWest != null) {
                northWest.clear(); // Очищаем северо-западный узел
                northWest = null; // Освобождаем память
            }
            if (northEast != null) {
                northEast.clear(); // Очищаем северо-восточный узел
                northEast = null; // Освобождаем память
            }
            if (southWest != null) {
                southWest.clear(); // Очищаем юго-западный узел
                southWest = null; // Освобождаем память
            }
            if (southEast != null) {
                southEast.clear(); // Очищаем юго-восточный узел
                southEast = null; // Освобождаем память
            }

            this.divided = false; // Устанавливаем флаг, что узел больше не разделен
        }
    }

    public void subdivide() throws Exception {
        // Создаем четыре дочерних узла
        this.northEast = new QuadTree(this.boundary.subdivide("ne"), this.getCapacity(), this.depth + 1);
        this.northWest = new QuadTree(this.boundary.subdivide("nw"), this.getCapacity(), this.depth + 1);
        this.southEast = new QuadTree(this.boundary.subdivide("se"), this.getCapacity(), this.depth + 1);
        this.southWest = new QuadTree(this.boundary.subdivide("sw"), this.getCapacity(), this.depth + 1);

        // Устанавливаем флаг, что узел разделен
        this.divided = true;

        // Перемещаем точки в дочерние узлы
        for (Point p : new ArrayList<>(this.points)) { // Используем новый список для избежания ConcurrentModificationException
            boolean inserted =
                    this.northEast.insert(p) ||
                            this.northWest.insert(p) ||
                            this.southEast.insert(p) ||
                            this.southWest.insert(p);

            if (!inserted) {
                throw new IllegalArgumentException("Capacity must be greater than 0");
            }
        }

        this.points.clear();
    }

    public boolean insert(Point point) throws Exception {
        if (!this.boundary.contains(point)) {
            System.out.printf("\nthis.quad.boundary: x(range)= %f to %f, y(range)=%f to %f\n" +
                    "point.coords: x=%f, y=%f", this.boundary.getX(), this.boundary.getWidth() + this.boundary.getX(),
                    this.boundary.getY(), this.boundary.getHeight() + this.boundary.getY(), point.getX(), point.getY());
            return false; // Точка не попадает в границы
        }

        // Если узел еще не разделен
        if (!this.divided) {
            // Если узел не переполнен и глубина меньше максимальной
            if (this.points.size() < this.capacity && this.depth < MAX_DEPTH) {
                this.points.add((Asteroid) point); // Добавляем точку в текущий узел
                return true;
            }

            // Делим узел только если он переполнен и глубина меньше максимальной
            if (this.points.size() >= this.capacity && this.depth < MAX_DEPTH) {
                subdivide(); // Делим узел
            }
        }

        // Если узел уже разделен, пытаемся вставить точку в дочерние узлы
        if (this.northWest != null && this.northWest.insert(point)) return true;
        if (this.northEast != null && this.northEast.insert(point)) return true;
        if (this.southWest != null && this.southWest.insert(point)) return true;
        if (this.southEast != null && this.southEast.insert(point)) return true;

        // Если ни один из дочерних узлов не смог вставить точку, добавляем её в текущий узел
        this.points.add((Asteroid) point);
        return true; // Возвращаем true, так как точка успешно добавлена
    }



//    public boolean insert(Point point) throws Exception {
//        if (!this.boundary.contains(point)) {
//            return false; // Точка не попадает в границы
//        }
//
//        if (!this.divided) {
//            if (this.points.size() < this.capacity && this.depth < MAX_DEPTH) {
//                this.points.add((Asteroid) point);
//                return true;
//            }
//            subdivide(); // Делим узел
//        }
//
//        return (
//                this.northWest.insert(point) ||
//                        this.northEast.insert(point) ||
//                        this.southWest.insert(point) ||
//                        this.southEast.insert(point)
//        );
//    }


    public List<Asteroid> query(Rectangle range, List<Asteroid> found) {
        if (!range.intersects(this.boundary)) {
            return found; // Нет пересечения с границами
        }

        if (this.divided) {
            northWest.query(range, found);
            northEast.query(range, found);
            southWest.query(range, found);
            southEast.query(range, found);
            return found; // Возвращаем найденные астероиды
        }

        for (Point p : points) {
            if (range.contains(p)) {
                found.add((Asteroid) p); // Приведение типа
            }
        }

        return found; // Возвращаем найденные астероиды
    }
}
