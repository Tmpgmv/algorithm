package programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;

import java.util.*;

/**
 * Симуляция боя:
 * - Сортировка всех юнитов по атаке в начале раунда
 * - Поочередные ходы до конца раунда
 * - Если армия уничтожена → остальные юниты противника завершают раунд
 * - Динамическое исключение мертвых из списков армий
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


            // Собираем живых юнитов для сортировки
            List<Unit> allAliveUnits = new ArrayList<>();
            allAliveUnits.addAll(getAliveUnits(playerUnits));
            allAliveUnits.addAll(getAliveUnits(computerUnits));

            if (allAliveUnits.isEmpty()) break;

            // Сортируем по убыванию атаки.
            allAliveUnits.sort(Comparator.comparingInt(Unit::getBaseAttack).reversed());

            // Выполнение ходов по очереди.
            for (Unit currentUnit : allAliveUnits) {
                // Проверяем жив ли юнит (мог погибнуть ранее в раунде)
                if (!currentUnit.isAlive()) continue;

                // Атакуем
                Unit target = currentUnit.getProgram().attack();

                // Логируем
                if (target != null && printBattleLog != null) {
                    printBattleLog.printBattleLog(currentUnit, target);
                }

                // Обновляем списки
                updateArmies(playerUnits, computerUnits);
            }

            // Окончательная очистка после раунда
            playerUnits.removeIf(unit -> !unit.isAlive());
            computerUnits.removeIf(unit -> !unit.isAlive());
        }
    }

    /**
     * Обновляем списки армий без прерывания раунда
     * Мертвые юниты исключаются из будущих проверок, но раунд продолжается
     */
    private void updateArmies(List<Unit> playerUnits, List<Unit> computerUnits) {
        // Удаляем мертвых только из списков армий (не прерываем цикл)
        playerUnits.removeIf(unit -> !unit.isAlive());
        computerUnits.removeIf(unit -> !unit.isAlive());
    }

    /**
     * Фильтрует только живые юниты для формирования очереди
     */
    private List<Unit> getAliveUnits(List<Unit> units) {
        return units.stream()
                .filter(Unit::isAlive)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
}