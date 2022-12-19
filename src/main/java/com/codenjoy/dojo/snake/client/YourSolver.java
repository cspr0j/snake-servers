package com.codenjoy.dojo.snake.client;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.*;

import java.util.*;

/**
 * User: Jalal Aliyev
 */
public class YourSolver implements Solver<Board> {

    private Dice dice;
    private Board board;

    public YourSolver(Dice dice) {
        this.dice = dice;
    }

    @Override
    public String get(Board board) {
        this.board = board;
        if (board.getHead() == null) return Direction.LEFT.toString();

        Direction dir = bfs(board.getApples().get(0));
        return dir.toString();
    }

    public Direction bfs(Point dist){
        Set<Point> visited = new HashSet<>(board.getBarriers());

        Queue<Point> queue = new LinkedList<>();
        queue.add(board.getHead());

        while (!queue.isEmpty()){
            Point curr = queue.remove();
            if(curr.equals(dist)){
                return board.getDirection(curr, board.getHead());
            }

            for (Point neighbour: board.getNeighbours(curr)) {
                if(!visited.contains(neighbour)){
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }
        return Direction.UP;
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://159.89.27.106/codenjoy-contest/board/player/w7a72kzrh3ltnv8933xu?code=3565445488605485968",
                new YourSolver(new RandomDice()),
                new Board());
    }
}
