package org.brainstorm.service.impl;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.service.DataGenerateStrategyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NumberListDataGenerateStrategyServiceImplTest {

    @Autowired
    DataGenerateStrategyService strategyService;

    @Test
    public void test() {
        DataType dataType = new DataType("Long");
        strategyService.generateData(dataType, 1);
    }
}