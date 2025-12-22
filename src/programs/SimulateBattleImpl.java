package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        // Получаем списки юнитов
        List<Unit> playerUnits = playerArmy.getUnits();
        List<Unit> computerUnits = computerArmy.getUnits();

        // Цикл битвы: продолжается, пока в обеих армиях есть живые юниты
        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {

            // Собираем всех юнитов на поле и сортируем их по убыванию атаки
            List<Unit> allUnits = new ArrayList<>();
            allUnits.addAll(playerUnits);
            allUnits.addAll(computerUnits);
            allUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            // Выполняем раунд
            for (Unit currentUnit : allUnits) {
                // Если юнит уже погиб в этом раунде, он не ходит
                if (!currentUnit.isAlive()) {
                    continue;
                }

                // Юнит совершает атаку
                Unit target = currentUnit.getProgram().attack();

                // Логируем атаку, если она состоялась
                if (target != null && printBattleLog != null) {
                    printBattleLog.printBattleLog(currentUnit, target);
                }
            }

            // Удаляем погибших юнитов из списков армий
            playerUnits.removeIf(u -> !u.isAlive());
            computerUnits.removeIf(u -> !u.isAlive());
        }
    }
}