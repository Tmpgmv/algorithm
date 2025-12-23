package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Определить список юнитов, подходящих для атаки, для атакующего юнита одной из армий.
 */
public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {


    /**
     * Цель метода — исключить ненужные попытки найти кратчайший путь между юнитами,
     * которые не могут атаковать друг друга.
     * */
    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) continue;

            for (Unit unit : row) {
                if (unit == null || !unit.isAlive()) continue;

                // Проверяем, не закрыт ли юнит соседним по Y
                boolean isSuitable = true;

                if (isLeftArmyTarget) {
                    // Для левой армии (компьютер атакует): не закрыт справа (больше Y)
                    isSuitable = !hasNeighborToRight(row, unit);
                } else {
                    // Для правой армии (игрок атакует): не закрыт слева (меньше Y)
                    isSuitable = !hasNeighborToLeft(row, unit);
                }

                if (isSuitable) {
                    suitableUnits.add(unit);
                }
            }
        }

        return suitableUnits;
    }

    private boolean hasNeighborToRight(List<Unit> row, Unit unit) {
        int y = unit.getyCoordinate();
        // Ищем юнита в соседней клетке справа (y+1)
        for (Unit neighbor : row) {
            if (neighbor != null && neighbor.isAlive() &&
                    neighbor.getyCoordinate() == y + 1 &&
                    neighbor.getxCoordinate() == unit.getxCoordinate()) {
                return true; // Закрыт справа
            }
        }
        return false;
    }

    private boolean hasNeighborToLeft(List<Unit> row, Unit unit) {
        int y = unit.getyCoordinate();
        // Ищем юнита в соседней клетке слева (y-1)
        for (Unit neighbor : row) {
            if (neighbor != null && neighbor.isAlive() &&
                    neighbor.getyCoordinate() == y - 1 &&
                    neighbor.getxCoordinate() == unit.getxCoordinate()) {
                return true; // Закрыт слева
            }
        }
        return false;
    }
}