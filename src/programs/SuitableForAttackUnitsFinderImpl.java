package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.*;

/**
 * Определение "видимых" юнитов для атаки.
 * Подходящий юнит: не закрыт соседним по Y в том же ряду (X)
 */
public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    // 🔥 Конструктор по умолчанию для рефлексии.
    public SuitableForAttackUnitsFinderImpl() {}

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        // Перебираем ряды противника (m=3)
        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;

            // 🔥 ОПТИМИЗАЦИЯ O(n): создаем карту юнитов по координате Y
            Map<Integer, Unit> unitsByY = new HashMap<>();
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    unitsByY.put(unit.getyCoordinate(), unit);
                }
            }

            // Проверяем каждый юнит в ряду (n=21)
            for (Unit unit : row) {
                if (unit == null || !unit.isAlive()) continue;

                // 🔥 O(1) проверка видимости через HashMap
                boolean isSuitable;
                if (isLeftArmyTarget) {
                    // Компьютер атакует: не закрыт СПРАВА (нет юнита y+1)
                    isSuitable = !unitsByY.containsKey(unit.getyCoordinate() + 1);
                } else {
                    // Игрок атакует: не закрыт СЛЕВА (нет юнита y-1)
                    isSuitable = !unitsByY.containsKey(unit.getyCoordinate() - 1);
                }

                if (isSuitable) {
                    suitableUnits.add(unit);
                }
            }
        }

        return suitableUnits;
    }
}
