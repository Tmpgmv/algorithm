package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;


/**
 * Определить кратчайший маршрут между атакующим и
 * атакуемым юнитом и возвращает его в виде списка объектов,
 * содержащих координаты каждой точки данного кратчайшего пути.
 * */
public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;

    // 🔥 Конструктор без аргументов.
    public UnitTargetPathFinderImpl() {}

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnits) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        // Препятствия: живые юниты кроме start/target
        Set<String> obstacles = new HashSet<>();
        for (Unit unit : existingUnits) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Map<String, Double> gScore = new HashMap<>();
        Map<String, Edge> cameFrom = new HashMap<>();
        Set<String> openSetKeys = new HashSet<>(); // 🔥 Отслеживаем открытые

        String startKey = startX + "," + startY;
        gScore.put(startKey, 0.0);
        openSet.add(new Node(startX, startY, 0.0, heuristic(startX, startY, targetX, targetY)));
        openSetKeys.add(startKey);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            String currentKey = current.x + "," + current.y;
            openSetKeys.remove(currentKey);

            if (current.x == targetX && current.y == targetY) {
                return reconstructPath(cameFrom, current.x, current.y);
            }

            // 8 направлений (ортогональные + диагонали)
            int[][] directions = {
                    {-1, 0}, {1, 0}, {0, -1}, {0, 1},     // Ортогональные (вес 1.0)
                    {-1, -1}, {-1, 1}, {1, -1}, {1, 1}   // Диагональные (вес √2 ≈ 1.414)
            };

            for (int[] dir : directions) {
                int nextX = current.x + dir[0];
                int nextY = current.y + dir[1];

                if (!isValid(nextX, nextY) || obstacles.contains(nextX + "," + nextY)) {
                    continue;
                }

                String nextKey = nextX + "," + nextY;
                double moveCost = (Math.abs(dir[0]) + Math.abs(dir[1]) == 2) ? 1.414 : 1.0;
                double tentativeG = gScore.getOrDefault(currentKey, Double.MAX_VALUE) + moveCost;

                if (tentativeG < gScore.getOrDefault(nextKey, Double.MAX_VALUE)) {
                    cameFrom.put(nextKey, new Edge(current.x, current.y));
                    gScore.put(nextKey, tentativeG);
                    double fScore = tentativeG + heuristic(nextX, nextY, targetX, targetY);

                    // 🔥 Обновление PriorityQueue
                    if (openSetKeys.contains(nextKey)) {
                        // Уже в очереди — просто обновим при следующей poll()
                    } else {
                        openSet.add(new Node(nextX, nextY, tentativeG, fScore));
                        openSetKeys.add(nextKey);
                    }
                }
            }
        }

        return new ArrayList<>(); // Путь не найден
    }

    /** Октагональная эвристика (оптимальна для 8 направлений) */
    private double heuristic(int x, int y, int targetX, int targetY) {
        double dx = Math.abs(x - targetX);
        double dy = Math.abs(y - targetY);
        double diagonal = Math.min(dx, dy);
        return 1.414 * diagonal + (dx + dy - 2 * diagonal); // √2 для диагоналей + 1 для прямых
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    /** Восстанавливает путь от target к start (включительно) */
    private List<Edge> reconstructPath(Map<String, Edge> cameFrom, int currentX, int currentY) {
        List<Edge> path = new ArrayList<>();
        String currentKey = currentX + "," + currentY;

        while (currentKey != null) {
            String[] coords = currentKey.split(",");
            path.add(0, new Edge(Integer.parseInt(coords[0]), Integer.parseInt(coords[1])));
            currentKey = cameFrom.containsKey(currentKey) ?
                    cameFrom.get(currentKey).getX() + "," + cameFrom.get(currentKey).getY() : null;
        }
        return path;
    }

    private static class Node {
        final int x, y;
        final double gScore, fScore;

        Node(int x, int y, double gScore, double fScore) {
            this.x = x;
            this.y = y;
            this.gScore = gScore;
            this.fScore = fScore;
        }
    }
}
