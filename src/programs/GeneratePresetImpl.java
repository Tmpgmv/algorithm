package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;
import java.util.*;

/**
 * Генератор оптимальной армии компьютера с использованием динамического программирования
 * Максимизирует соотношение (атака + здоровье) / стоимость при ограничениях:
 * - ≤11 юнитов каждого типа
 * - ≤maxPoints общих очков (1500)
 */
public class GeneratePresetImpl implements GeneratePreset {

    // 🔥 Конструктор по умолчанию
    public GeneratePresetImpl() {}

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Инициализируем армию компьютера
        Army computerArmy = new Army();
        int n = unitList.size(); // n=4: лучник, всадник, мечник, копейщик

        // Вычисляем эффективность каждого типа юнита
        // Эффективность = (атака + здоровье) / стоимость
        double[] efficiencies = new double[n];
        for (int i = 0; i < n; i++) {
            Unit u = unitList.get(i);
            efficiencies[i] = ((double) u.getBaseAttack() + u.getHealth()) / u.getCost();
        }

        // Сортируем типы юнитов по убыванию эффективности
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> Double.compare(efficiencies[b], efficiencies[a]));

        // Инициализация таблиц
        double[][] dp = new double[n + 1][maxPoints + 1];

        int[][] bestPrevType = new int[n + 1][maxPoints + 1];  // Предыдущий тип
        int[][] bestPrevPoints = new int[n + 1][maxPoints + 1]; // Предыдущие очки
        int[][] bestCount = new int[n + 1][maxPoints + 1];      // Количество юнитов

        // Заполнение таблицы
        for (int i = 1; i <= n; i++) {
            int typeIdx = indices[i - 1];
            Unit unit = unitList.get(typeIdx);
            int cost = unit.getCost();

            for (int points = 0; points <= maxPoints; points++) {
                // Базовый случай: не используем текущий тип
                dp[i][points] = dp[i - 1][points];

                // Пробуем 1..11 юнитов текущего типа
                for (int cnt = 1; cnt <= 11 && points >= cnt * cost; cnt++) {
                    int prevPoints = points - cnt * cost;
                    double newEff = dp[i - 1][prevPoints] + cnt * efficiencies[typeIdx];

                    if (newEff > dp[i][points]) {
                        // 🔥 O(1) обновление вместо System.arraycopy O(n)
                        dp[i][points] = newEff;
                        bestPrevType[i][points] = typeIdx;
                        bestPrevPoints[i][points] = prevPoints;
                        bestCount[i][points] = cnt;
                    }
                }
            }
        }

        // Восстановление решения через backtracking)
        List<Unit> selectedUnits = new ArrayList<>();
        int totalCost = 0;
        int currentPoints = maxPoints;
        int currentTypeIdx = n;

        // Идем от конца к началу, восстанавливая оптимальное решение
        while (currentTypeIdx > 0) {
            int cnt = bestCount[currentTypeIdx][currentPoints];
            if (cnt > 0) {
                int typeIdx = bestPrevType[currentTypeIdx][currentPoints];
                Unit template = unitList.get(typeIdx);

                // Создаем cnt юнитов этого типа
                for (int j = 0; j < cnt; j++) {
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
                    totalCost += template.getCost();
                }
                currentPoints = bestPrevPoints[currentTypeIdx][currentPoints];
            }
            currentTypeIdx--;
        }

        // Распределяем юниты по координатам
        assignCoordinates(selectedUnits);

        // Финализируем армию
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(totalCost);
        return computerArmy;
    }

    /**
     * Распределяем юниты армии по случайным уникальным координатам
     * Левая сторона поля: X ∈ [0,2], Y ∈ [0,20]
     */
    private void assignCoordinates(List<Unit> units) {
        Set<String> occupiedCoords = new HashSet<>();
        Random random = new Random();

        for (Unit unit : units) {
            int x, y;
            do {
                x = random.nextInt(3);      // X: 0, 1, 2 (левая армия)
                y = random.nextInt(21);     // Y: 0-20 (21 ряд)
            } while (occupiedCoords.contains(x + "," + y));

            occupiedCoords.add(x + "," + y);
            unit.setxCoordinate(x);
            unit.setyCoordinate(y);
        }
    }
}
