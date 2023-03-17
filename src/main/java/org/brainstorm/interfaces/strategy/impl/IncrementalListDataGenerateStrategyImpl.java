package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.IncrementalListDataGenerateStrategy;

import java.util.ArrayList;
import java.util.List;

public class IncrementalListDataGenerateStrategyImpl implements IncrementalListDataGenerateStrategy {
    @Override
    public boolean canSupport(DataType dataType) {
        String typeName = dataType.getTypeName();
        if ( typeName.equals("Integer") ) {
            return true;
        }
        return false;
    }

    public List<Integer> generateIncrementalList(int size, int startValue) {
        List<Integer> incrementalList = new ArrayList<>();
        if (size > 0) {
            incrementalList.add(startValue);
            for (int i = 1; i < size; i++) {
                incrementalList.add(incrementalList.get(i - 1) + 1);
            }
        }
        return incrementalList;
    }
}
