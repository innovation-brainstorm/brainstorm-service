package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.NumberListDataGenerateStrategy;
import org.springframework.stereotype.Component;

@Component
public class NumberListDataGenerateStrategyImpl implements NumberListDataGenerateStrategy<Number> {
    @Override
    public boolean canSupport(DataType dataType) {
        String typeName = dataType.getTypeName();
        if (typeName.equals("Integer") || typeName.equals("Long") || typeName.equals("Float")
            || typeName.equals("Double")) {
            return true;
        }
        return false;
    }

    @Override
    public <T extends Number> List<T> generateRandomList(int length, T min, T max) {

        List<T> randomList = new ArrayList<>();
        Random random = new Random();

        if (min instanceof Integer && max instanceof Integer) {
            for (int i = 0; i < length; i++) {
                int randomNumber = random.nextInt(max.intValue() - min.intValue() + 1) + min.intValue();
                randomList.add((T)Integer.valueOf(randomNumber));
            }
        } else if (min instanceof Double && max instanceof Double) {
            for (int i = 0; i < length; i++) {
                double randomNumber = random.nextDouble() * (max.doubleValue() - min.doubleValue()) + min.doubleValue();
                randomList.add((T)Double.valueOf(randomNumber));
            }
        } else if (min instanceof Long && max instanceof Long) {
            for (int i = 0; i < length; i++) {
                long randomNumber =
                    random.nextLong() / Long.MAX_VALUE * (max.longValue() - min.longValue()) + min.longValue();
                randomList.add((T)Long.valueOf(randomNumber));
            }
        } else if (min instanceof Float && max instanceof Float) {
            for (int i = 0; i < length; i++) {
                float randomNumber = random.nextFloat() * (max.floatValue() - min.floatValue()) + min.floatValue();
                randomList.add((T)Float.valueOf(randomNumber));
            }
        } else {
            throw new IllegalArgumentException("Unsupported Number type");
        }
        return randomList;
    }

}
