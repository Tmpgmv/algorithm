package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;
import java.util.*;

/**
 * Реализация генератора оптимальной армии компьютера с использованием динамического программирования
 * Максимизирует соотношение (атака + здоровье) / стоимость при ограничениях:
 * - ≤11 юнитов каждого типа
 * - ≤maxPoints общих очков (1500)
 * Сложность: O(n × m), где n=4 (типы), m≈150 (макс. юнитов)
 */
public class GeneratePresetImpl implements GeneratePreset {

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        // Инициализируем армию компьютера
        Army computerArmy = new Army();
        int n = unitList.size(); // n=4: лучник, всадник, мечник, копейщик

        // Шаг 1: Вычисляем эффективность каждого типа юнита
        // Эффективность = (атака + здоровье) / стоимость
        double[] efficiencies = new double[n];
        for (int i = 0; i < n; i++) {
            Unit u = unitList.get(i);
            efficiencies[i] = ((double) u.getBaseAttack() + u.getHealth()) / u.getCost();
        }

        // Шаг 2: Сортируем типы юнитов по убыванию эффективности
        // Для оптимального порядка рассмотрения в DP
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) indices[i] = i;
        Arrays.sort(indices, (a, b) -> Double.compare(efficiencies[b], efficiencies[a]));

        // Шаг 3: Инициализация таблиц динамического программирования
        // dp[i][j] = максимальная эффективность для первых i типов с j очками
        double[][] dp = new double[n + 1][maxPoints + 1];

        // counts[i][j][type] = количество юнитов типа type в оптимальном решении для i типов, j очков
        int[][][] counts = new int[n + 1][maxPoints + 1][n];

        // Шаг 4: Заполнение DP таблицы
        // Перебираем типы юнитов в порядке убывания эффективности
        for (int i = 1; i <= n; i++) {
            int typeIdx = indices[i - 1]; // Индекс текущего типа
            Unit unit = unitList.get(typeIdx);
            int cost = unit.getCost();    // Стоимость одного юнита этого типа

            // Для каждого возможного количества очков
            for (int points = 0; points <= maxPoints; points++) {
                // Базовый случай: не используем текущий тип
                dp[i][points] = dp[i - 1][points];

                // Пробуем добавить 1..11 юнитов текущего типа
                for (int cnt = 1; cnt <= 11 && points >= cnt * cost; cnt++) {
                    int prevPoints = points - cnt * cost; // Очки для предыдущих типов
                    double newEff = dp[i - 1][prevPoints] + cnt * efficiencies[typeIdx];

                    // Если новая комбинация лучше
                    if (newEff > dp[i][points]) {
                        dp[i][points] = newEff; // Обновляем эффективность

                        // Сохраняем оптимальные количества юнитов
                        // Копируем решение для предыдущих типов
                        System.arraycopy(counts[i - 1][prevPoints], 0, counts[i][points], 0, n);
                        // Устанавливаем количество текущего типа
                        counts[i][points][typeIdx] = cnt;
                    }
                }
            }
        }

        // Шаг 5: Восстановление оптимального решения
        // Берем лучшее решение для всех типов и всех доступных очков
        List<Unit> selectedUnits = new ArrayList<>();
        int totalCost = 0; // Фактическая стоимость собранной армии
        int[] finalCounts = counts[n][maxPoints]; // Количества юнитов каждого типа

        // Создаем экземпляры юнитов согласно оптимальным количествам
        for (int i = 0; i < n; i++) {
            int count = finalCounts[i]; // Сколько юнитов типа i нужно
            if (count > 0) {
                Unit template = unitList.get(i); // Шаблон юнита этого типа
                for (int j = 0; j < count; j++) {
                    // Клонируем юнит с уникальным именем
                    Unit newUnit = new Unit(
                            template.getName() + " " + j,                    // Уникальное имя
                            template.getUnitType(),                           // Тип юнита
                            template.getHealth(),                             // Здоровье
                            template.getBaseAttack(),                         // Базовая атака
                            template.getCost(),                               // Стоимость
                            template.getAttackType(),                         // Тип атаки
                            template.getAttackBonuses(),                      // Бонусы атаки
                            template.getDefenceBonuses(),                     // Бонусы защиты
                            template.getxCoordinate(),                        // Начальные координаты X
                            template.getyCoordinate()                         // Начальные координаты Y
                    );
                    selectedUnits.add(newUnit);
                    totalCost += template.getCost();
                }
            }
        }

        // Шаг 6: Распределяем юниты по координатам (0-2 по X, 0-20 по Y)
        assignCoordinates(selectedUnits);

        // Шаг 7: Финализируем армию
        computerArmy.setUnits(selectedUnits);
        computerArmy.setPoints(totalCost);
        return computerArmy;
    }

    /**
     * Распределяет юниты армии по случайным уникальным координатам
     * Левая сторона поля: X ∈ [0,2], Y ∈ [0,20]
     */
    private void assignCoordinates(List<Unit> units) {
        Set<String> occupiedCoords = new HashSet<>(); // Множество занятых координат
        Random random = new Random();

        for (Unit unit : units) {
            int x, y;
            // Генерируем уникальные координаты до успеха
            do {
                x = random.nextInt(3);        // X: 0, 1 или 2 (левая армия)
                y = random.nextInt(21);       // Y: 0-20 (21 ряд)
            } while (occupiedCoords.contains(x + "," + y));

            // Занимаем координаты и устанавливаем их юниту
            occupiedCoords.add(x + "," + y);
            unit.setxCoordinate(x);
            unit.setyCoordinate(y);
        }
    }
}