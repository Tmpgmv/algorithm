import com.battle.heroes.army.Unit;
import programs.GeneratePresetImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Unit> unitList = new ArrayList<Unit>();

        Map<String, Double> attackBonus = new HashMap<>();
        attackBonus.put("magic", 10.0);

        Map<String, Double> defenseBonus = new HashMap<>();
        attackBonus.put("defense", 10.0);


        Unit knight = new Unit("knight",
                "cavalry",
                100,
                2,
                100,
                "spear",
                attackBonus,
                defenseBonus,
                10,
                10);


        unitList.add(knight);

        var tmp = new GeneratePresetImpl();
        tmp.generate(unitList, 0);
    }
}
