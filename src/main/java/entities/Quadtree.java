package entities;

import java.util.ArrayList;
import java.util.List;

public class Quadtree {
    private final int MAX_OBJECTS = 4;
    private final int MAX_LEVELS = 5;

    private int level;
    private List<Circle> objects;
    public double x;
    public double y;
    public double width;
    public double height;
    public Quadtree[] nodes;

    public Quadtree(int level, double x, double y, double width, double height) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nodes = new Quadtree[4];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        double subWidth = width / 2;
        double subHeight = height / 2;
        double xMid = x + subWidth;
        double yMid = y + subHeight;

        nodes[0] = new Quadtree(level + 1, x, y, subWidth, subHeight);
        nodes[1] = new Quadtree(level + 1, xMid, y, subWidth, subHeight);
        nodes[2] = new Quadtree(level + 1, x, yMid, subWidth, subHeight);
        nodes[3] = new Quadtree(level + 1, xMid, yMid, subWidth, subHeight);
    }

    private int getIndex(Circle circle) {
        int index = -1;
        double verticalMidpoint = x + width / 2;
        double horizontalMidpoint = y + height / 2;

        boolean topQuadrant = (circle.center.y - circle.radius < horizontalMidpoint) &&
                (circle.center.y + circle.radius < horizontalMidpoint);
        boolean bottomQuadrant = (circle.center.y - circle.radius > horizontalMidpoint);

        if ((circle.center.x - circle.radius < verticalMidpoint) &&
                (circle.center.x + circle.radius < verticalMidpoint)) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if ((circle.center.x - circle.radius > verticalMidpoint)) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(Circle circle) {
        if (nodes[0] != null) {
            int index = getIndex(circle);
            if (index != -1) {
                nodes[index].insert(circle);
                return;
            }
        }

        objects.add(circle);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }
    public List<Circle> retrieve(List<Circle> returnObjects, Circle circle) {
        int index = getIndex(circle);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, circle);
        }

        returnObjects.addAll(objects);
        return returnObjects;
    }
}



