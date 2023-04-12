package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.brainstorm.enums.DataTypeEnum;
import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;
import org.brainstorm.interfaces.strategy.StrategyEnums;

public class IncrementalListDataGenerateStrategyImpl implements Strategy {

    @Override
    public boolean canSupport(DataType dataType) {
        String typeName = dataType.getTypeName();
        List<String> dataStrategyList = Arrays.asList(DataTypeEnum.BIGINT.get(), DataTypeEnum.INT.get());
        return dataStrategyList.contains(typeName);
    }

    @Override
    public List<Integer> generate(DataType dataType) {
        List<Integer> incrementalList = new ArrayList<>();
        if (dataType.getLength() > 0) {
            incrementalList.add(dataType.getStartValue());
            for (int i = 1; i < dataType.getLength(); i++) {
                incrementalList.add(incrementalList.get(i - 1) + 1);
            }
        }
        return incrementalList;
    }

    @Override
    public int getIdentifier() {
        return StrategyEnums.IncrementalList.ordinal();
    }

    @Override
    public String getStrategyName() {
        return StrategyEnums.IncrementalList.name();
    }
}
