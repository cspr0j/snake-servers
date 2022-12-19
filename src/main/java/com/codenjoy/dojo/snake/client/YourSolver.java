package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.Collections;
import java.util.List;

import static com.codenjoy.dojo.services.PointImpl.pt;

public class YourSolver implements Solver<Board> {

    private final Dice dice;
    private final Dijkstra way;
    private Dijkstra.Possible possible;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
        this.way = new Dijkstra();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                "http://159.89.27.106/codenjoy-contest/board/player/w7a72kzrh3ltnv8933xu?code=3565445488605485968",
                new YourSolver(new RandomDice()),
                new Board());
    }

    public Dijkstra.Possible possible(Board board) {
        return new Dijkstra.Possible() {
            @Override
            public boolean possible(Point from, Direction where) {
                Point to = where.change(from);
                return !board.isAt(to.getX(), to.getY(),
                        Elements.HEAD_DOWN, Elements.HEAD_LEFT,
                        Elements.HEAD_UP, Elements.HEAD_RIGHT);
            }

            @Override
            public boolean possible(Point point) {
                return !isBarrierAt(point.getX(), point.getY());
            }
        };
    }

    private boolean isBarrierAt(int x, int y) {
        return board.isAt(x, y, Elements.BREAK, Elements.BAD_APPLE,
                Elements.TAIL_END_DOWN, Elements.TAIL_END_LEFT,
                Elements.TAIL_END_UP, Elements.TAIL_END_RIGHT,
                Elements.TAIL_HORIZONTAL, Elements.TAIL_VERTICAL,
                Elements.TAIL_LEFT_DOWN, Elements.TAIL_LEFT_UP,
                Elements.TAIL_RIGHT_DOWN, Elements.TAIL_RIGHT_UP);
    }

    @Override
    public String get(final Board board) {
        this.board = board;
        if (board.isGameOver()) return "";
        List<Direction> result = getWay();
        if (result.isEmpty()) return "";
        return result.get(0).toString();
    }

    public List<Direction> getWay() {
        possible = possible(board);

        Point from = board.getHead();
        List<Point> to = board.get(Elements.GOOD_APPLE);
        List<Direction> way = getWay(from, to);

        if (way.isEmpty()) {
            int distance = 0;
            Point longest = null;
            for (int x = 0; x < board.size(); x++) {
                for (int y = 0; y < board.size(); y++) {
                    if (isBarrierAt(x, y)) continue;
                    Point pt = pt(x, y);
                    way = this.way.getShortestWay(board.size(), from, Collections.singletonList(pt), possible);
                    if (distance <= way.size()) {
                        distance = way.size();
                        longest = pt;
                    }
                }
            }
            if (longest != null) {
                way = getWay(from, Collections.singletonList(longest));
            }
        }
        return way;
    }

    private List<Direction> getWay(Point from, List<Point> to) {
        return this.way.getShortestWay(board.size(), from, to, possible);
    }
}
