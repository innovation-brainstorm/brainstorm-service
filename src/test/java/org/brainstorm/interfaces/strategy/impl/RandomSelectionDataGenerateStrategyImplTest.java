package org.brainstorm.interfaces.strategy.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RandomSelectionDataGenerateStrategyImplTest {
    RandomSelectionDataGenerateStrategyImpl strategy = new RandomSelectionDataGenerateStrategyImpl();

    @Test
    public <T> void testSelectRandomValuesWithIntegers() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> selectedValues = strategy.generate(new DataType<>(values, 3));

        Assertions.assertEquals(3, selectedValues.size());
        for (Integer value : selectedValues) {
            Assertions.assertTrue(values.contains(value));
        }
    }

    @Test
    public void testSelectRandomValuesWithStrings() {
        List<String> values = Arrays.asList("apple", "banana", "orange", "grape", "kiwi");
        List<String> selectedValues = strategy.generate(new DataType<>(values, 2));

        Assertions.assertEquals(2, selectedValues.size());
        for (String value : selectedValues) {
            Assertions.assertTrue(values.contains(value));
        }
    }

    @Test
    public void testSelectRandomValuesWithEmptyList() {
        List<Integer> values = new ArrayList<>();
        List<Integer> selectedValues = strategy.generate(new DataType<>(values, 0));

        Assertions.assertEquals(0, selectedValues.size());
    }

    @Test
    public void testSelectRandomValuesWithNegativeLength() {
        List<Integer> values = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> selectedValues = strategy.generate(new DataType<>(values, -1));

        Assertions.assertEquals(0, selectedValues.size());
    }
}