package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;
import org.brainstorm.interfaces.strategy.StrategyEnums;

import java.util.Collections;
import java.util.List;

public class AIDataGenerateStrategyImpl implements Strategy {

    @Override
    public boolean canSupport(DataType dataType) {
        return true;
    }

    @Override
    public List generate(DataType dataType) {
        //will populate by caller
        return Collections.emptyList();
    }

    @Override
    public int getIdentifier() {
        return StrategyEnums.AI.ordinal();
    }

    @Override
    public String getStrategyName() {
        return StrategyEnums.AI.name();
    }
}
