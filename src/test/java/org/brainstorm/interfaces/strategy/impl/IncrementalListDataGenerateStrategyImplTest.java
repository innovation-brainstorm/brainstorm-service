package org.brainstorm.interfaces.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.brainstorm.interfaces.strategy.DataType;
import org.junit.jupiter.api.Test;

class IncrementalListDataGenerateStrategyImplTest {
    IncrementalListDataGenerateStrategyImpl strategy = new IncrementalListDataGenerateStrategyImpl();

    @Test
    public void testGenerateIncrementalList() {
        // Test case 1: size = 0, startValue = 1
        assertEquals(new ArrayList<>(), strategy.generate(new DataType<>("int", 0, 1)));

        // Test case 2: size = 1, startValue = 0
        assertEquals(Arrays.asList(0), strategy.generate(new DataType("int", 1, 0)));

        // Test case 3: size = 5, startValue = -2
        assertEquals(Arrays.asList(-2, -1, 0, 1, 2), strategy.generate(new DataType("int", 5, -2)));
    }
}