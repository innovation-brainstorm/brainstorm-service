package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;

public class SpecificValueDataGenerateStrategyImpl<T> implements Strategy {
    @Override
    public boolean canSupport(DataType dataType) {
        return true;
    }

    @Override
    public List<T> generate(DataType dataType) {
        List<T> list = new ArrayList<>(dataType.getLength()); // 创建一个初始大小为Length的ArrayList对象
        for (int i = 0; i < dataType.getLength(); i++) {
            list.add((T)dataType.getValue()); // 将value添加到List的每个位置上
        }
        return list;
    }
}
