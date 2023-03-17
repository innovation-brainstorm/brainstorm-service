package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.RandomSelectionDataGenerateStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomSelectionDataGenerateStrategyImpl implements RandomSelectionDataGenerateStrategy {
    @Override
    public boolean canSupport(DataType dataType) {
        return true;
    }

    @Override
    public <T> List<T> selectRandomValues(List<T> values, int length) {
        Random random =new Random();
        if (length >= 0) {
            List<T> result = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                int randomIndex = random.nextInt(values.size());
                result.add(values.get(randomIndex));
            }

        return result;
        }
        else return new ArrayList<>();
    }
}
