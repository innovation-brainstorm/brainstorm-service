package org.brainstorm.interfaces.strategy;

import java.util.List;

public interface RandomSelectionDataGenerateStrategy extends Strategy {
    boolean canSupport(DataType dataType);
    public <T> List<T> selectRandomValues(List<T> values, int length);
}
