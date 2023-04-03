package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;

public class IncrementalListDataGenerateStrategyImpl implements Strategy {
    @Override
    public boolean canSupport(DataType dataType) {
        String typeName = dataType.getTypeName();
        if (typeName.equals("Integer")) {
            return true;
        }
        return false;
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
}
