package programs;


import com.battle.heroes.army.Army;

import com.battle.heroes.army.Unit;

import com.battle.heroes.army.programs.GeneratePreset;


import java.util.*;


public class GeneratePresetImpl implements GeneratePreset {


    @Override

    public Army generate(List<Unit> unitList, int maxPoints) {

        Army computerArmy = new Army();

        List<Unit> selectedUnits = new ArrayList<>();

        int currentPoints = 0;


// Сортируем по эффективности

        unitList.sort((u1, u2) -> {

            double efficiency1 = ((double) u1.getBaseAttack() + u1.getHealth()) / u1.getCost();

            double efficiency2 = ((double) u2.getBaseAttack() + u2.getHealth()) / u2.getCost();

            return Double.compare(efficiency2, efficiency1);

        });


        for (Unit unitTemplate : unitList) {

            int unitsCount = 0;

            while (unitsCount < 11 && currentPoints + unitTemplate.getCost() <= maxPoints) {

// Используем getCoordinateX() и getCoordinateY()

                Unit newUnit = new Unit(

                        unitTemplate.getName() + " " + unitsCount,

                        unitTemplate.getUnitType(),

                        unitTemplate.getHealth(),

                        unitTemplate.getBaseAttack(),

                        unitTemplate.getCost(),

                        unitTemplate.getAttackType(),

                        unitTemplate.getAttackBonuses(),

                        unitTemplate.getDefenceBonuses(),

                        unitTemplate.getxCoordinate(),

                        unitTemplate.getyCoordinate()

                );


                selectedUnits.add(newUnit);

                currentPoints += unitTemplate.getCost();

                unitsCount++;

            }

        }


        assignCoordinates(selectedUnits);


        computerArmy.setUnits(selectedUnits);

        computerArmy.setPoints(currentPoints);

        return computerArmy;

    }


    private void assignCoordinates(List<Unit> units) {

        Set<String> occupiedCoords = new HashSet<>();

        Random random = new Random();


        for (Unit unit : units) {

            int x, y;

            do {

                x = 15 + random.nextInt(12);

                y = random.nextInt(21);

            } while (occupiedCoords.contains(x + "," + y));


            occupiedCoords.add(x + "," + y);


// Используем сеттеры setCoordinateX() и setCoordinateY()

            unit.setxCoordinate(x);

            unit.setyCoordinate(y);

        }

    }

} 