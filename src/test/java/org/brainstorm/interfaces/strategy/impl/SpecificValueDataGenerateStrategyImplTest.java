package org.brainstorm.interfaces.strategy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.junit.jupiter.api.Test;

class SpecificValueDataGenerateStrategyImplTest {
    SpecificValueDataGenerateStrategyImpl strategy = new SpecificValueDataGenerateStrategyImpl();

    @Test
    void testGenerateListWithSameValue() {
        List<Integer> list = strategy.generate(new DataType(42, 5));
        assertEquals(5, list.size());
        for (Integer value : list) {
            assertEquals(42, value);
        }

        List<String> list2 = strategy.generate(new DataType("hello", 3));
        assertEquals(3, list2.size());
        for (String value : list2) {
            assertEquals("hello", value);
        }

        List<Object> list3 = strategy.generate(new DataType(null, 10));
        assertEquals(10, list3.size());
        for (Object value : list3) {
            assertNull(value);
        }
    }
}