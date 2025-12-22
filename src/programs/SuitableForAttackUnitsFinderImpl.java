package programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        for (List<Unit> row : unitsByRow) {
            if (row == null || row.isEmpty()) {
                continue;
            }

            List<Unit> aliveUnits = new ArrayList<>();
            for (Unit unit : row) {
                if (unit != null && unit.isAlive()) {
                    aliveUnits.add(unit);
                }
            }

            if (aliveUnits.isEmpty()) {
                continue;
            }

            if (isLeftArmyTarget) {
                // Ищем юнита с максимальным X (правый край)
                Unit rightMostUnit = aliveUnits.get(0);
                for (Unit unit : aliveUnits) {
                    if (unit.getxCoordinate() > rightMostUnit.getxCoordinate()) {
                        rightMostUnit = unit;
                    }
                }
                suitableUnits.add(rightMostUnit);
            } else {
                // Ищем юнита с минимальным X (левый край)
                Unit leftMostUnit = aliveUnits.get(0);
                for (Unit unit : aliveUnits) {
                    if (unit.getxCoordinate() < leftMostUnit.getxCoordinate()) {
                        leftMostUnit = unit;
                    }
                }
                suitableUnits.add(leftMostUnit);
            }
        }

        return suitableUnits;
    }
}