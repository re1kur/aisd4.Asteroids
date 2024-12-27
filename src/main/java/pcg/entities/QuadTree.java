package pcg.entities;

import lombok.Getter;
import pcg.gameObjects.Asteroid;

import java.util.ArrayList;
import java.util.List;

@Getter
public class QuadTree {
    private final int MAX_DEPTH;
    private final int capacity;
    private final int depth;
    private final Rectangle boundary;
    private QuadTree northWest, northEast, southWest, southEast;
    private final List<Asteroid> points;
    private boolean divided;

    public QuadTree(Rectangle boundary, int capacity, int maxDepth) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.depth = 1;
        this.MAX_DEPTH = maxDepth;
        this.points = new ArrayList<>();
        this.divided = false;
    }

    public QuadTree(Rectangle boundary, int capacity, int depth, int maxDepth) {
        this.boundary = boundary;
        this.capacity = capacity;
        this.depth = depth;
        this.MAX_DEPTH = maxDepth;
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
        this.points.clear();

        if (this.divided) {
            if (northWest != null) {
                northWest.clear();
                northWest = null;
            }
            if (northEast != null) {
                northEast.clear();
                northEast = null;
            }
            if (southWest != null) {
                southWest.clear();
                southWest = null;
            }
            if (southEast != null) {
                southEast.clear();
                southEast = null;
            }
            this.divided = false;
        }
    }

    public void subdivide() {
        this.northEast = new QuadTree(this.boundary.subdivide("ne"), this.getCapacity(), this.depth + 1, getMAX_DEPTH());
        this.northWest = new QuadTree(this.boundary.subdivide("nw"), this.getCapacity(), this.depth + 1, getMAX_DEPTH());
        this.southEast = new QuadTree(this.boundary.subdivide("se"), this.getCapacity(), this.depth + 1, getMAX_DEPTH());
        this.southWest = new QuadTree(this.boundary.subdivide("sw"), this.getCapacity(), this.depth + 1, getMAX_DEPTH());

        this.divided = true;

        for (Asteroid p : new ArrayList<>(this.points)) {
            boolean inserted =
                    this.northEast.insert(p) ||
                            this.northWest.insert(p) ||
                            this.southEast.insert(p) ||
                            this.southWest.insert(p);

            if (!inserted) {
                System.err.printf("\nthis.quad.boundary: x(range)= %f to %f, y(range)=%f to %f\n" +
                                "point.coords: x=%f, y=%f | point.velocities: Vx=%f, Vy=%f", this.boundary.getX(), this.boundary.getWidth() + this.boundary.getX(),
                        this.boundary.getY(), this.boundary.getHeight() + this.boundary.getY(), p.getX(), p.getY(), p.getVelocityX(), p.getVelocityY());
            }
        }
        this.points.clear();
    }

    public boolean remove(Asteroid point) {
        if (!this.boundary.contains(point)) {
            return false;
        }

        if (!this.divided) {
            return this.points.remove(point);
        }

        if (this.northWest != null && this.northWest.remove(point)) return true;
        if (this.northEast != null && this.northEast.remove(point)) return true;
        if (this.southWest != null && this.southWest.remove(point)) return true;
        if (this.southEast != null && this.southEast.remove(point)) return true;

        return false;
    }

    public boolean update(Asteroid point) {
        if (!remove(point)) {
            return false;
        }
        return insert(point);
    }


    public boolean insert(Point point) {
        if (!this.boundary.contains(point)) {
            return false;
        }

        if (!this.divided) {
            if (this.points.size() < this.capacity && this.depth < MAX_DEPTH) {
                this.points.add((Asteroid) point);
                return true;
            }

            if (this.points.size() >= this.capacity && this.depth < MAX_DEPTH) {
                subdivide();
            }
        }

        if (this.northWest != null && this.northWest.insert(point)) return true;
        if (this.northEast != null && this.northEast.insert(point)) return true;
        if (this.southWest != null && this.southWest.insert(point)) return true;
        if (this.southEast != null && this.southEast.insert(point)) return true;

        this.points.add((Asteroid) point);
        return true;
    }

    public List<Asteroid> query(Rectangle range, List<Asteroid> found) {
        if (!range.intersects(this.boundary)) {
            return found;
        }

        if (this.divided) {
            if (northWest != null) northWest.query(range, found);
            if (northEast != null) northEast.query(range, found);
            if (southWest != null) southWest.query(range, found);
            if (southEast != null) southEast.query(range, found);
            return found;
        }

        for (Asteroid p : points) {
            if (range.contains(p)) {
                found.add(p);
            }
        }
        return found;
    }
}
