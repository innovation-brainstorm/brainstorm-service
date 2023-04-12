package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;
import org.brainstorm.interfaces.strategy.StrategyEnums;

public class RandomSelectionDataGenerateStrategyImpl<T> implements Strategy {

    @Override
    public boolean canSupport(DataType dataType) {
        return true;
    }

    public List<T> generate(DataType dataType) {
        Random random = new Random();
        if (dataType.getLength() >= 0) {
            List<T> result = new ArrayList<>(dataType.getLength());

            for (int i = 0; i < dataType.getLength(); i++) {
                int randomIndex = random.nextInt(dataType.getValues().size());
                result.add((T)dataType.getValues().get(randomIndex));
            }

            return result;
        } else
            return new ArrayList<>();
    }

    @Override
    public int getIdentifier() {
        return StrategyEnums.RandomSelection.ordinal();
    }

    @Override
    public String getStrategyName() {
        return StrategyEnums.RandomSelection.name();
    }
}
