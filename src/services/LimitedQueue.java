package services;

import com.battle.heroes.army.Unit;
import java.util.LinkedList;


/**
 * Cоблюдать ограничение в 11 юнитов каждого типа.
 *
 * Archer
 * Knight
 * Pikeman
 * Swordsman
 *
 * @param <Unit>
 */
public class LimitedQueue<Unit> extends LinkedList<Unit> {
    private final int limit = 11;

    @Override
    public boolean add(Unit unit) {
        super.add(unit);
        while (size() > limit) {
            super.removeFirst();
        }
        return true;
    }
}