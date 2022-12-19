package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;

import java.util.*;

public class Dijkstra {
    private static final List<Direction> DIRECTIONS;

    static {
        DIRECTIONS = Arrays.asList(Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT);
    }

    private Map<Point, List<Direction>> possibleWays;
    private int size;
    private Possible possible;

    public Dijkstra() {
    }

    public List<Direction> getShortestWay(int size, Point from, List<Point> goals, Possible possible) {
        this.size = size;
        this.possible = possible;
        this.setupPossibleWays();
        List<List<Direction>> paths = new LinkedList<>();
        Iterator<Point> pointIterator = goals.iterator();

        List<Direction> shortest;
        while (pointIterator.hasNext()) {
            Point to = pointIterator.next();
            shortest = this.getPath(from).get(to);
            if (shortest != null && !shortest.isEmpty()) {
                paths.add(shortest);
            }
        }

        int minDistance = Integer.MAX_VALUE;
        int indexMin = 0;

        for (int index = 0; index < paths.size(); ++index) {
            List<Direction> path = paths.get(index);
            if (minDistance > path.size()) {
                minDistance = path.size();
                indexMin = index;
            }
        }

        if (paths.isEmpty()) {
            return Collections.emptyList();
        } else {
            shortest = paths.get(indexMin);
            if (shortest.size() == 0) {
                return Collections.emptyList();
            } else {
                return shortest;
            }
        }
    }

    private Map<Point, List<Direction>> getPath(Point from) {
        Map<Point, List<Direction>> path = new HashMap<>();

        for (Point point : this.possibleWays.keySet()) {
            path.put(point, new LinkedList<>());
        }

        boolean[][] processed = new boolean[this.size][this.size];
        LinkedList<Point> toProcess = new LinkedList<>();
        Point current = from;

        label53:
        while (true) {
            if (current == null) {
                if (toProcess.isEmpty()) {
                    return path;
                }

                current = toProcess.remove();
            }

            List<Direction> before = path.get(current);
            Iterator<Direction> directionIterator = (this.possibleWays.get(current)).iterator();

            while (true) {
                Direction direction;
                Point to;
                List<Direction> directions;
                do {
                    do {
                        do {
                            if (!directionIterator.hasNext()) {
                                processed[current.getX()][current.getY()] = true;
                                current = null;
                                if (toProcess.isEmpty()) {
                                    return path;
                                }
                                continue label53;
                            }

                            direction = directionIterator.next();
                            to = direction.change(current);
                        } while (this.possible != null && !this.possible.possible(to));
                    } while (processed[to.getX()][to.getY()]);

                    directions = path.get(to);
                } while (!directions.isEmpty() && directions.size() <= before.size() + 1);

                directions.addAll(before);
                directions.add(direction);
                if (!processed[to.getX()][to.getY()]) {
                    toProcess.add(to);
                }
            }
        }
    }

    private void setupPossibleWays() {
        this.possibleWays = new TreeMap<>();

        for (int x = 0; x < this.size; ++x) {
            for (int y = 0; y < this.size; ++y) {
                Point from = PointImpl.pt(x, y);
                List<Direction> directions = new LinkedList<>();

                for (Direction direction : DIRECTIONS) {
                    if (this.possible.possible(from, direction)) {
                        directions.add(direction);
                    }
                }

                this.possibleWays.put(from, directions);
            }
        }

    }

    public Map<Point, List<Direction>> getPossibleWays() {
        return this.possibleWays;
    }

    public interface Possible {
        boolean possible(Point point, Direction direction);

        boolean possible(Point point);
    }
}
