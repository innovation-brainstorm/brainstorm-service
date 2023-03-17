package org.brainstorm.interfaces.strategy.impl;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class IncrementalListDataGenerateStrategyImplTest {
    IncrementalListDataGenerateStrategyImpl strategy = new IncrementalListDataGenerateStrategyImpl();
    @Test
    public void testGenerateIncrementalList() {
        // Test case 1: size = 0, startValue = 1
        assertEquals(new ArrayList<>(), strategy.generateIncrementalList(0, 1));

        // Test case 2: size = 1, startValue = 0
        assertEquals(Arrays.asList(0), strategy.generateIncrementalList(1, 0));

        // Test case 3: size = 5, startValue = -2
        assertEquals(Arrays.asList(-2, -1, 0, 1, 2), strategy.generateIncrementalList(5, -2));
    }
}