package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnits) {
        int[][] distance = new int[WIDTH][HEIGHT];
        for (int[] row : distance) {
            Arrays.fill(row, -1);
        }

        Set<String> obstacles = new HashSet<>();
        for (Unit unit : existingUnits) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                // Исправлено на getCoordinateX() / getCoordinateY()
                obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        Queue<Edge> queue = new LinkedList<>();
        // Исправлено получение координат у attackUnit
        Edge startNode = new Edge(attackUnit.getxCoordinate(), attackUnit.getyCoordinate());
        queue.add(startNode);
        distance[startNode.getX()][startNode.getY()] = 0;

        Map<Edge, Edge> parentMap = new HashMap<>();

        while (!queue.isEmpty()) {
            Edge current = queue.poll();

            // Исправлено сравнение с координатами targetUnit
            if (current.getX() == targetUnit.getxCoordinate() && current.getY() == targetUnit.getyCoordinate()) {
                return buildPath(parentMap, current);
            }

            for (int[] offset : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) {
                int nextX = current.getX() + offset[0];
                int nextY = current.getY() + offset[1];

                if (isValid(nextX, nextY) && !obstacles.contains(nextX + "," + nextY)) {
                    if (distance[nextX][nextY] == -1) {
                        distance[nextX][nextY] = distance[current.getX()][current.getY()] + 1;
                        Edge nextEdge = new Edge(nextX, nextY);
                        parentMap.put(nextEdge, current);
                        queue.add(nextEdge);
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private List<Edge> buildPath(Map<Edge, Edge> parentMap, Edge end) {
        List<Edge> path = new LinkedList<>();
        Edge curr = end;
        while (curr != null) {
            path.add(0, curr);
            curr = parentMap.get(curr);
        }
        if (!path.isEmpty()) {
            path.remove(0);
        }
        return path;
    }
}