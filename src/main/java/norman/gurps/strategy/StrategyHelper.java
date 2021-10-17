package norman.gurps.strategy;

import norman.gurps.combat.Action;
import norman.gurps.combat.Defense;

public interface StrategyHelper {
    Action decideAction();

    int decideTarget();

    Defense[] decideDefense();
}
