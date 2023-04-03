package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.DataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class NumberListNumberListDataGenerateStrategyTest {
    @Test
    public void testCanSupport() {
        NumberListDataGenerateStrategyImpl strategy = new NumberListDataGenerateStrategyImpl();

        DataType integerType = new DataType("Integer");
        Assertions.assertTrue(strategy.canSupport(integerType));

        DataType longType = new DataType("Long");
        Assertions.assertTrue(strategy.canSupport(longType));

        DataType floatType = new DataType("Float");
        Assertions.assertTrue(strategy.canSupport(floatType));

        DataType doubleType = new DataType("Double");
        Assertions.assertTrue(strategy.canSupport(doubleType));

        DataType stringType = new DataType("String");
        Assertions.assertFalse(strategy.canSupport(stringType));
    }

    @Test
    public void testGenerateRandomList() {
        NumberListDataGenerateStrategyImpl strategy = new NumberListDataGenerateStrategyImpl();

        DataType dataTypeInt = new DataType<>("integer", 10, 0, 100);
        List<Integer> integerList = strategy.generate(dataTypeInt);
        Assertions.assertEquals(10, integerList.size());
        for (Integer integer : integerList) {
            Assertions.assertTrue(integer >= 0 && integer <= 100);
        }

        DataType dataTypeLong = new DataType<>("Long", 10, 0L, 100L);
        List<Long> longList = strategy.generate(dataTypeLong);
        Assertions.assertEquals(10, longList.size());
        for (Long aLong : longList) {
            Assertions.assertTrue(aLong >= 0L && aLong <= 100L);
        }

        DataType dataTypeFloat = new DataType<>("float", 10, 0.0f, 100.0f);
        List<Float> floatList = strategy.generate(dataTypeFloat);
        Assertions.assertEquals(10, floatList.size());
        for (Float aFloat : floatList) {
            Assertions.assertTrue(aFloat >= 0.0f && aFloat <= 100.0f);
        }

        DataType dataTypeDouble = new DataType<>("Double", 10, 0.0, 100.0);
        List<Double> doubleList = strategy.generate(dataTypeDouble);
        Assertions.assertEquals(10, doubleList.size());
        for (Double aDouble : doubleList) {
            Assertions.assertTrue(aDouble >= 0.0 && aDouble <= 100.0);
        }
    }
}