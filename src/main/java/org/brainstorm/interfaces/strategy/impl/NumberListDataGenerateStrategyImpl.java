package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;
import org.springframework.stereotype.Component;

@Component
public class NumberListDataGenerateStrategyImpl<T> implements Strategy {

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
    public List<T> generate(DataType dataType) {

        List<T> randomList = new ArrayList<>();
        Random random = new Random();

        if (dataType.getMin() instanceof Integer && dataType.getMax() instanceof Integer) {
            for (int i = 0; i < dataType.getLength(); i++) {
                int randomNumber = random
                    .nextInt(((Integer)dataType.getMax()).intValue() - ((Integer)dataType.getMin()).intValue() + 1)
                    + ((Integer)dataType.getMin()).intValue();
                randomList.add((T)Integer.valueOf(randomNumber));
            }
        } else if (dataType.getMin() instanceof Double && dataType.getMax() instanceof Double) {
            for (int i = 0; i < dataType.getLength(); i++) {
                double randomNumber = random.nextDouble()
                    * (((Double)dataType.getMax()).doubleValue() - ((Double)dataType.getMin()).doubleValue())
                    + ((Double)dataType.getMin()).doubleValue();
                randomList.add((T)Double.valueOf(randomNumber));
            }
        } else if (dataType.getMin() instanceof Long && dataType.getMax() instanceof Long) {
            for (int i = 0; i < dataType.getLength(); i++) {
                long randomNumber = random.nextLong() / Long.MAX_VALUE
                    * (((Long)dataType.getMax()).longValue() - ((Long)dataType.getMin()).longValue())
                    + ((Long)dataType.getMin()).longValue();
                randomList.add((T)Long.valueOf(randomNumber));
            }
        } else if (dataType.getMin() instanceof Float && dataType.getMax() instanceof Float) {
            for (int i = 0; i < dataType.getLength(); i++) {
                float randomNumber = random.nextFloat()
                    * (((Float)dataType.getMax()).floatValue() - ((Float)dataType.getMin()).floatValue())
                    + ((Float)dataType.getMin()).floatValue();
                randomList.add((T)Float.valueOf(randomNumber));
            }
        } else {
            throw new IllegalArgumentException("Unsupported Number type");
        }
        return randomList;
    }

}
