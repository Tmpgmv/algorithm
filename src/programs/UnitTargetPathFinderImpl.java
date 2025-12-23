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
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        Set<String> obstacles = new HashSet<>();
        for (Unit unit : existingUnits) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Double> fScore = new HashMap<>();
        Map<String, Edge> cameFrom = new HashMap<>();

        String startKey = startX + "," + startY;
        gScore.put(startKey, 0.0);
        fScore.put(startKey, heuristic(startX, startY, targetX, targetY));
        openSet.add(new Node(startX, startY, 0, 0));

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            String currentKey = current.x + "," + current.y;

            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(cameFrom, current.x, current.y, startX, startY);
            }

            // 8 направлений (включая диагонали)
            int[][] directions = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Ортогональные
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}  // Диагональные
            };

            for (int[] dir : directions) {
                int nextX = current.x + dir[0];
                int nextY = current.y + dir[1];

                if (!isValid(nextX, nextY) || obstacles.contains(nextX + "," + nextY)) {
                    continue;
                }

                String nextKey = nextX + "," + nextY;
                double tentativeG = gScore.getOrDefault(currentKey, Double.MAX_VALUE) +
                        (Math.abs(dir[0]) + Math.abs(dir[1]) == 2 ? 1.414 : 1.0);

                if (tentativeG < gScore.getOrDefault(nextKey, Double.MAX_VALUE)) {
                    cameFrom.put(nextKey, new Edge(current.x, current.y));
                    gScore.put(nextKey, tentativeG);
                    fScore.put(nextKey, tentativeG + heuristic(nextX, nextY, targetX, targetY));

                    if (!openSet.stream().anyMatch(n -> n.x == nextX && n.y == nextY)) {
                        openSet.add(new Node(nextX, nextY, gScore.get(nextKey), fScore.get(nextKey)));
                    }
                }
            }
        }

        return new ArrayList<>(); // Путь не найден
    }

    private double heuristic(int x, int y, int targetX, int targetY) {
        // Манхэттенское расстояние (для 8 направлений)
        return Math.abs(x - targetX) + Math.abs(y - targetY);
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    private List<Edge> reconstructPath(Map<String, Edge> cameFrom, int currentX, int currentY, int startX, int startY) {
        List<Edge> path = new ArrayList<>();
        String currentKey = currentX + "," + currentY;

        while (currentKey != null) {
            String[] coords = currentKey.split(",");
            path.add(0, new Edge(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
            currentKey = cameFrom.containsKey(currentKey) ?
                    cameFrom.get(currentKey).getX() + "," + cameFrom.get(currentKey).getY() : null;
        }

        return path; // Включает и start, и target
    }

    private static class Node {
        int x, y;
        double gScore, fScore;

        Node(int x, int y, double gScore, double fScore) {
            this.x = x;
            this.y = y;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
}
