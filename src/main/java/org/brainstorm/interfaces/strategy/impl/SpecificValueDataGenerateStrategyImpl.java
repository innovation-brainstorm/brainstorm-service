package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.SpecificValueDataGenerateStrategy;

import java.util.ArrayList;
import java.util.List;

public class SpecificValueDataGenerateStrategyImpl implements SpecificValueDataGenerateStrategy {
    @Override
    public boolean canSupport(DataType dataType) {
        return true;
    }
    public <T> List<T> generateListWithSameValue(T value, int size) {
        List<T> list = new ArrayList<>(size); // 创建一个初始大小为size的ArrayList对象
        for (int i = 0; i < size; i++) {
            list.add(value); // 将value添加到List的每个位置上
        }
        return list;
    }
}
