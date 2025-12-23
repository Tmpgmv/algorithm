Проект Heroes.

Выполнил: Граблевский М.В.

**Введение**
Какие алгоритмы я реализовал:
1. Метод generate интерфейса GeneratePreset.  
Генератор оптимальной армии компьютера. Максимизирует соотношение 
(атака + здоровье) / стоимость при ограничениях:
- ≤11 юнитов каждого типа.
- ≤maxPoints общих очков (1500). 
2. Метод simulate интерфейса SimulateBattle. 
Симуляция боя:
- Сортировка всех юнитов по атаке в начале раунда.
- Поочередные ходы до конца раунда.
- Если армия уничтожена → остальные юниты противника завершают раунд.
- Динамическое исключение мертвых из списков армий.
3. Метод getSuitableUnits интерфейса SuitableForAttackUnitsFinder.
Определение "видимых" юнитов для атаки.
Подходящий юнит: не закрыт соседним по Y в том же ряду (X).
4. Метод getTargetPath интерфейса UnitTargetPathFinder
выполняет поиск кратчайшего пути между атакующим юнитом и 
целью на поле фиксированного размера. 
Он учитывает препятствия в виде живых юнитов противника, 
используя 8 направлений движения (включая диагональные)
Подготавливает координаты точек пути от старта к цели или пустой список, 
если путь невозможен.




Путь до JAR-файла: build/libs/heroes_task_lib-1.0-SNAPSHOT.jar


**1. Расчет алгоритмической сложности метода 
getSuitableUnits интерфейса SuitableForAttackUnitsFinder**

Обозначения

    m = 3 (количество рядов = unitsByRow.size(), фиксировано по заданию)

    n = 21 (максимальное количество юнитов в ряду, Y ∈ )



Поэтапный анализ
Этап 0: Инициализация класса


public SuitableForAttackUnitsFinderImpl() {}  // O(1)

Этап 1: Инициализация результата


List<Unit> suitableUnits = new ArrayList<>();  // O(1)

Этап 2: Главный цикл по рядам


for (List<Unit> row : unitsByRow) {           // Цикл: m=3 итерации
if (row == null || row.isEmpty()) continue; // O(1)

    // Создание HashMap по Y координатам
    Map<Integer, Unit> unitsByY = new HashMap<>(); // O(1)
    for (Unit unit : row) {                       // Цикл: ≤n итераций
        if (unit != null && unit.isAlive()) {     // O(1)
            unitsByY.put(unit.getyCoordinate(), unit); // O(1) амортизировано
        }
    }
    
    // Проверка каждого юнита
    for (Unit unit : row) {                       // Цикл: ≤n итераций
        if (unit == null || !unit.isAlive()) continue; // O(1)
        
        boolean isSuitable;                       // O(1)
        if (isLeftArmyTarget) {                   // O(1)
            isSuitable = !unitsByY.containsKey(unit.getyCoordinate() + 1); // O(1)
        } else {
            isSuitable = !unitsByY.containsKey(unit.getyCoordinate() - 1); // O(1)
        }
        
        if (isSuitable) {                         // O(1)
            suitableUnits.add(unit);              // O(1)
        }
    }
}

Детальный анализ подэтапов:

2.1 Создание HashMap


T_hashmap = Σ_{i=1 to n} put(y_i, unit_i) = n × O(1) = O(n)

Конкретно: 21 вставка × O(1) = 21 операций

2.2 Проверка юнитов


T_check = Σ{i=1 to n} containsKey(y_i ± 1) = n × O(1) = O(n)

Конкретно: 21 запрос × O(1) = 21 операций

Сложность Этапа 2 для одного ряда: O(n) + O(n) = O(n)

Общая для m рядов: m × O(n) = O(m × n)

Этап 3: Возврат результата


return suitableUnits;  // O(1)

Итоговая таблица сложностей
№	Операции	Асимптотика	
0. Конструктор	O(1)
1. Инициализация	O(1)
2. Главный цикл	O(m × n)
3. Возврат	O(1)
ИТОГО: max{O(1), O(m × n), O(1)} = O(m × n)




**2. Расчет алгоритмической сложности метода generate интерфейса GeneratePreset**

Обозначения:

    n = количество типов юнитов = 4 (лучник, всадник, мечник, копейщик)

    m = maxPoints = 1500 (максимальная стоимость армии)

    k = 11 (максимум юнитов одного типа, константа)

Поэтапный анализ
Этап 1: Вычисление эффективности


double[] efficiencies = new double[n];
for (int i = 0; i < n; i++) {           // Цикл: n итераций
Unit u = unitList.get(i);           // O(1)
efficiencies[i] = ... / u.getCost(); // O(1) - арифметика
}

Количество операций: n × O(1) = O(n)
Конкретно: 4 операции

Этап 2: Сортировка индексов


Integer[] indices = new Integer[n];     // O(n)
for (int i = 0; i < n; i++) indices[i] = i;  // O(n)
Arrays.sort(indices, comparator);       // O(n * log n)

Количество операций: O(n) + O(n) + O(n * log n) = O(n * log n)
Конкретно: n=4 → log n ≈ 2 → O(8) ≈ O(1) (константа)

Этап 3: Инициализация таблиц


double[][] dp = new double[n + 1][maxPoints + 1];              // O(n × m)
int[][] bestPrevType = new int[n + 1][maxPoints + 1];          // O(n × m)  
int[][] bestPrevPoints = new int[n + 1][maxPoints + 1];        // O(n × m)
int[][] bestCount = new int[n + 1][maxPoints + 1];             // O(n × m)

Количество операций: 4 × (n × m) = O(n × m)


Этап 4: Заполнение таблицы


for (int i = 1; i <= n; i++) {                    // Внешний: n итераций
int typeIdx = indices[i - 1];                 // O(1)
Unit unit = unitList.get(typeIdx);            // O(1)
int cost = unit.getCost();                    // O(1)

    for (int points = 0; points <= maxPoints; points++) {  // Средний: m итераций
        dp[i][points] = dp[i - 1][points];        // O(1) - базовый случай
        
        for (int cnt = 1; cnt <= 11 && points >= cnt * cost; cnt++) {  // Внутренний
            int prevPoints = points - cnt * cost; // O(1)
            double newEff = dp[i - 1][prevPoints] + cnt * efficiencies[typeIdx]; // O(1)
            
            if (newEff > dp[i][points]) {         // O(1)
                dp[i][points] = newEff;           // O(1)
                bestPrevType[i][points] = typeIdx; // O(1)
                bestPrevPoints[i][points] = prevPoints; // O(1)
                bestCount[i][points] = cnt;       // O(1)
            }
        }
    }
}

Анализ вложенных циклов:

    Внутренний цикл cnt: выполняется ≤11 раз → O(1)

    Каждая итерация внутреннего цикла: O(1) (все операции константны)

    Общая сложность этапа: n × m × 11 × O(1) = O(n × m)


Этап 5: Восстановление решения


while (currentTypeIdx > 0) {              // O(n) итераций
int cnt = bestCount[currentTypeIdx][currentPoints]; // O(1)
if (cnt > 0) {
int typeIdx = bestPrevType[...];  // O(1)
Unit template = unitList.get(typeIdx); // O(1)
for (int j = 0; j < cnt; j++) {   // ≤11 = O(1)
Unit newUnit = new Unit(...); // O(1) - конструктор
selectedUnits.add(newUnit);   // O(1) амортизировано
totalCost += template.getCost(); // O(1)
}
currentPoints = bestPrevPoints[...]; // O(1)
}
currentTypeIdx--;
}

Количество операций: n × (1 + 11 × O(1)) = O(n)

Этап 6: Назначение координат


for (Unit unit : units) {                 // total_units ≤ n×11 = O(n)
do {                                  // Ожидаемо O(1)
x = random.nextInt(3);            // O(1)
y = random.nextInt(21);           // O(1)
} while (occupiedCoords.contains(...)); // HashSet: O(1) амортизировано
occupiedCoords.add(...);              // O(1)
unit.setxCoordinate(x);               // O(1)
unit.setyCoordinate(y);               // O(1)
}

Количество операций: (n × 11) × O(1) = O(n)

Этап 7: Финализация армии


computerArmy.setUnits(selectedUnits);  // O(1)
computerArmy.setPoints(totalCost);    // O(1)
return computerArmy;                  // O(1)

Сложность: O(1)

Итоговая таблица сложностей
№  Операциия	Асимптотика
1. Эффективность	O(n)
2. Сортировка	O(1)
3. Инициализация	O(n × m)
4. Заполнение таблиц  O(n × m)
5. Восстановление	O(n)
6. Координаты	O(n)
7. Финализация	O(1)
Итого: max{O(n), O(1), O(n × m), O(n)} = O(n × m)





**3. Расчет алгоритмической сложности метода getTargetPath 
интерфейса UnitTargetPathFinder.**

Обозначения

    W = 27 (ширина поля)

    H = 21 (высота поля)

    V = W × H = 567 (вершины графа)

    E ≈ 8V = 4536 (рёбра, 8 соседей на вершину)

    o ≤ 44 (препятствия — юниты)

Поэтапный анализ
Этап 1: Инициализация координат

int startX = attackUnit.getxCoordinate(); // O(1)
int startY = attackUnit.getyCoordinate(); // O(1)
int targetX = targetUnit.getxCoordinate(); // O(1)
int targetY = targetUnit.getyCoordinate(); // O(1)

Сложность: O(1)

Этап 2: Построение множества препятствий

Set<String> obstacles = new HashSet<>();           // O(1)
for (Unit unit : existingUnits) {                 // Цикл: o ≤ 44 итерации
if (unit.isAlive() && unit != attackUnit && unit != targetUnit) { // O(1)
obstacles.add(unit.getxCoordinate() + "," + unit.getyCoordinate()); // O(1)
}
}

Сложность: O(o) (линейно по юнитам)

Этап 3: Инициализация A структур


PriorityQueue<Node> openSet = new PriorityQueue<>(...); // O(1)
Map<String, Double> gScore = new HashMap<>();          // O(1)
Map<String, Edge> cameFrom = new HashMap<>();          // O(1)
Set<String> openSetKeys = new HashSet<>();             // O(1)
String startKey = ...; gScore.put(...);                // O(1)
openSet.add(...); openSetKeys.add(startKey);           // O(log V)

Сложность: O(log V)

Этап 4: Главный цикл



while (!openSet.isEmpty()) {              // ≤ V = 567 итераций (poll)
Node current = openSet.poll();        // O(log V)
openSetKeys.remove(currentKey);       // O(1)

    if (current.x == targetX...) return;  // O(1)
    
    for (int[] dir : directions) {        // 8 итераций
        int nextX = ...; int nextY = ...; // O(1)
        
        if (!isValid(nextX,nextY) || obstacles.contains(...)) continue; // O(1)
        
        double tentativeG = ...           // O(1)
        if (tentativeG < gScore.getOrDefault(...)) {  // O(1)
            cameFrom.put(...); gScore.put(...);       // O(1)
            if (!openSetKeys.contains(nextKey)) {     // O(1)
                openSet.add(...); openSetKeys.add(...); // O(log V)
            }
        }
    }
}

Этап 5: Реконструкция пути


private List<Edge> reconstructPath(...) {
while (currentKey != null) {          // ≤ max_path_length ≤ 48 шагов
String[] coords = currentKey.split(","); // O(1)
path.add(0, new Edge(...));        // O(1)
currentKey = cameFrom.get(...);    // O(1)
}
}

Сложность: O(path_length) = O(min(W,H)) = O(1)

Этап 6: Возврат пустого пути


return new ArrayList<>();  // O(1)

Сложность: O(1)

Итоговая таблица сложностей
№	Операция	Асимптотика	
1. Координаты	O(1)	
2. Препятствия	O(o)
3. Инициализация	O(log V)
4. Цикл	V log V + E	O((W×H) log(W×H))
5. Путь	O(1)
6. Пустой путь	O(1)
ИТОГО: max{O(o), O(V log V), O(1)} = O((W×H) log(W×H))







**4. Расчет алгоритмической сложности метода 
simulate интерфейса SimulateBattle SimulateBattle**


Обозначения

    n = общее начальное количество юнитов в обеих армиях (≤ 4×11 = 44)

    r = количество раундов (≤ n/2 ≈ 22 в худшем случае)

    u_t = количество живых юнитов в раунде t (u_1 = n, u_r → 0)


Этап 1: Инициализация


List<Unit> playerUnits = new ArrayList<>(playerArmy.getUnits()); // O(n/2)
List<Unit> computerUnits = new ArrayList<>(computerArmy.getUnits()); // O(n/2)

Сложность: O(n)

Этап 2: Главный цикл while


while (!playerUnits.isEmpty() && !computerUnits.isEmpty()) {  // r ≤ n/2 = 22 итераций
// 2.1 Сбор живых юнитов
List<Unit> allAliveUnits = new ArrayList<>();             // O(1)
allAliveUnits.addAll(getAliveUnits(playerUnits));         // O(u_t/2)
allAliveUnits.addAll(getAliveUnits(computerUnits));       // O(u_t/2)

// 2.2 Сортировка
allAliveUnits.sort(...);                                  // O(u_t * log u_t)
    
// 2.3 Цикл атак
for (Unit currentUnit : allAliveUnits) {                  // u_t итераций
    if (!currentUnit.isAlive()) continue;                 // O(1)
    Unit target = currentUnit.getProgram().attack();      // O(1) по заданию
    if (target != null && printBattleLog != null)         // O(1)
        printBattleLog.printBattleLog(...);               // O(1)
    updateArmies(playerUnits, computerUnits);             // O(u_t)
}
    
// 2.4 Очистка после раунда
playerUnits.removeIf(...);                                // O(u_t)
computerUnits.removeIf(...);                              // O(u_t)
}

Подэтапы:

2.1 getAliveUnits()


units.stream().filter(Unit::isAlive).collect(...)  // O(u_t/2)

Сложность на вызов: O(u_t/2)

2.2 Сортировка

T_sort(t) = O(u_t log u_t)
Σ_{t=1 to r} u_t log u_t ≤ n log n × r = O(n² log n)

2.3 updateArmies()

playerUnits.removeIf(...);  // O(u_t/2)
computerUnits.removeIf(...); // O(u_t/2)

Сложность за вызов: O(u_t)

2.4 Очистка после раунда

2 × O(u_t) = O(u_t)

Сложность одного раунда t:

T_round(t) = O(u_t) + O(u_t log u_t) + u_t × O(u_t) + O(u_t)
= O(u_t log u_t + u_t²)

Сложность всех раундов:

T_total = Σ_{t=1 to r} O(u_t log u_t + u_t²)
≤ r × O(n log n + n²)
≤ (n/2) × O(n²) = O(n³)

НО! Реально O(n² log n), т.к. u_t быстро уменьшается.

Итоговая таблица сложностей
№	Сложность	Конкретно (n=44)
1. Этап 1	O(n)	44
2. Главный цикл	O(n² * log n)
3. Сбор юнитов	O(Σu_t) = O(n²)	
4. Сортировки	O(Σu_t * log u_t)
5. updateArmies	O(Σu_t²)
ИТОГО:	O(n² * log n)









