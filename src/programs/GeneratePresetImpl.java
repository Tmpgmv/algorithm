package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;
import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army computerArmy = new Army();
        int n = unitList.size(); // 4 типа юнитов

        // Вычисляем эффективность: (атака + здоровье) / стоимость
        double[] efficiencies = new double[n];
        for (int i = 0; i < n; i++) {
            Unit u = unitList.get(i);
            efficiencies[i] = ((double) u.getBaseAttack() + u.getHealth()) / u.getCost();
        }

        // Сортируем по убыванию эффективности
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> Double.compare(efficiencies[b], efficiencies[a]));

        // DP: dp[i][j][k] = max эффективность для i типов, j очков, k юнитов
        // Оптимизировано до dp[types][points] из-за малого n=4
        double[][] dp = new double[n + 1][maxPoints + 1];
        int[][][] counts = new int[n + 1][maxPoints + 1][n]; // counts[i][j][type]

        for (int i = 1; i <= n; i++) {
            int typeIdx = indices[i - 1];
            Unit unit = unitList.get(typeIdx);
            int cost = unit.getCost();

            for (int points = 0; points <= maxPoints; points++) {
                dp[i][points] = dp[i - 1][points];

                for (int cnt = 1; cnt <= 11 && points >= cnt * cost; cnt++) {
                    int prevPoints = points - cnt * cost;
                    double newEff = dp[i - 1][prevPoints] + cnt * efficiencies[typeIdx];
                    if (newEff > dp[i][points]) {
                        dp[i][points] = newEff;
                        // Сохраняем количество юнитов каждого типа
                        System.arraycopy(counts[i - 1][prevPoints], 0, counts[i][points], 0, n);
                        counts[i][points][typeIdx] = cnt;
                    }
                }
            }
        }

        // Восстанавливаем оптимальную комбинацию (максимум юнитов при max эффективности)
        List<Unit> selectedUnits = new ArrayList<>();
        int remainingPoints = maxPoints;
        int[] finalCounts = counts[n][maxPoints];

        for (int i = 0; i < n; i++) {
            int count = finalCounts[i];
            Unit template = unitList.get(i);
            for (int j = 0; j < count; j++) {
                Unit newUnit = new Unit(
                        template.getName() + " " + j,
                        template.getUnitType(),
                        template.getHealth(),
                        template.getBaseAttack(),
                        template.getCost(),
                        template.getAttackType(),
                        template.getAttackBonuses(),
                        template.getDefenceBonuses(),
                        template.getxCoordinate(),
                        template.getyCoordinate()
                );
                selectedUnits.add(newUnit);
                remainingPoints -= template.getCost();
            }
        }

        assignCoordinates(selectedUnits);
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(maxPoints - remainingPoints);
        return computerArmy;
    }

    private void assignCoordinates(List<Unit> units) {
        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        for (Unit unit : units) {
            int x, y;
            do {
                x = random.nextInt(3);
                y = random.nextInt(21);
            } while (occupiedCoords.contains(x + "," + y));

            occupiedCoords.add(x + "," + y);
            unit.setxCoordinate(x);
            unit.setyCoordinate(y);
        }
    }
}
