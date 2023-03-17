package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.SpecificValueDataGenerateStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpecificValueDataGenerateStrategyImplTest {
    SpecificValueDataGenerateStrategy strategy= new SpecificValueDataGenerateStrategyImpl();
    @Test
    void testGenerateListWithSameValue() {
        List<Integer> list = strategy.generateListWithSameValue(42, 5);
        assertEquals(5, list.size());
        for (Integer value : list) {
            assertEquals(42, value);
        }

        List<String> list2 = strategy.generateListWithSameValue("hello", 3);
        assertEquals(3, list2.size());
        for (String value : list2) {
            assertEquals("hello", value);
        }

        List<Object> list3 = strategy.generateListWithSameValue(null, 10);
        assertEquals(10, list3.size());
        for (Object value : list3) {
            assertNull(value);
        }
    }
}