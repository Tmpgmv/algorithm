package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {
    public SimulateBattleImpl() {
        // Пустой конструктор для рефлексии
    }
    private PrintBattleLog printBattleLog;

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {

        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        // Основной цикл битвы
        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            // Раунд игрока
            if (!playerUnits.isEmpty()) {
                simulateSideTurn(playerUnits, computerUnits);
            }

            // Раунд компьютера (если игрок еще жив)
            if (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
                simulateSideTurn(computerUnits, playerUnits);
            }
        }
    }

    private void simulateSideTurn(List<Unit> attackers, List<Unit> defenders) throws InterruptedException {
        // 1. Сортируем атакующих по убыванию атаки
        attackers.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

        // 2. Каждый юнит ходит по очереди
        Iterator<Unit> iterator = attackers.iterator();
        while (iterator.hasNext()) {
            Unit attacker = iterator.next();

            // Проверяем жив ли юнит (может погибнуть от предыдущих атак)
            if (!attacker.isAlive()) {
                iterator.remove();
                continue;
            }

            // 3. Атакуем
            Unit target = attacker.getProgram().attack();

            // 4. Логируем атаку
            if (target != null) {
                printBattleLog.printBattleLog(attacker, target);
            }

            // 5. Удаляем мертвых защитников (пересчет очередей)
            removeDeadUnits(defenders);
        }
    }

    private void removeDeadUnits(List<Unit> units) {
        units.removeIf(unit -> !unit.isAlive());
    }
}
