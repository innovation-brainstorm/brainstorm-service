package org.brainstorm.interfaces.strategy;

import java.util.List;

public interface SpecificValueDataGenerateStrategy {
    boolean canSupport(DataType dataType);

    public <T> List<T> generateListWithSameValue(T value, int size);
}
