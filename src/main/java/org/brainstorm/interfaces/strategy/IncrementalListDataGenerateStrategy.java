package org.brainstorm.interfaces.strategy;

import java.util.List;

public interface IncrementalListDataGenerateStrategy {
    boolean canSupport(DataType dataType);
    public List<Integer> generateIncrementalList(int size, int startValue);

}
