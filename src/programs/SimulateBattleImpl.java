package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

/**
 * Симуляция боя ТОЧНО по правилам задания:
 * - Сортировка всех юнитов по атаке в начале раунда
 * - Поочередные ходы до конца раунда
 * - Если армия уничтожена → остальные юниты противника завершают раунд
 * - Динамическое исключение мертвых из списков армий
 * Сложность: O(n² log n)
 */
public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;
    public SimulateBattleImpl() {}

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits());
        List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits());

        // Главный цикл раундов
        while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {
            // === НАЧАЛО РАУНДА ===

            // 1. Собираем живых юнитов для сортировки
            List<Unit> allAliveUnits = new ArrayList<>();
            allAliveUnits.addAll(getAliveUnits(playerUnits));
            allAliveUnits.addAll(getAliveUnits(computerUnits));

            if (allAliveUnits.isEmpty()) break;

            // 2. СОРТИРУЕМ ПО УБЫВАНИЮ АТАКИ
            allAliveUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            // 3. КАЖДЫЙ ЮНИТ ВЫПОЛНЯЕТ ХОД ПО ОЧЕРЕДИ
            for (Unit currentUnit : allAliveUnits) {
                // Проверяем жив ли юнит (мог погибнуть ранее в раунде)
                if (!currentUnit.isAlive()) continue;

                // Атакуем
                Unit target = currentUnit.getProgram().attack();

                // Логируем
                if (target != null && printBattleLog != null) {
                    printBattleLog.printBattleLog(currentUnit, target);
                }

                // ✅ КРИТИЧЕСКОЕ: НЕ прерываем раунд! Только обновляем списки
                updateArmies(playerUnits, computerUnits);
            }

            // 4. Окончательная очистка ПОСЛЕ раунда
            playerUnits.removeIf(unit -> !unit.isAlive());
            computerUnits.removeIf(unit -> !unit.isAlive());
        }
    }

    /**
     * Обновляет списки армий без прерывания раунда
     * Мертвые юниты исключаются из будущих проверок, но раунд продолжается
     */
    private void updateArmies(List<Unit> playerUnits, List<Unit> computerUnits) {
        // Удаляем мертвых только из списков армий (не прерываем цикл)
        playerUnits.removeIf(unit -> !unit.isAlive());
        computerUnits.removeIf(unit -> !unit.isAlive());
    }

    /**
     * Фильтрует только живых юнитов для формирования очереди
     */
    private List<Unit> getAliveUnits(List<Unit> units) {
        return units.stream()
                .filter(Unit::isAlive)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}