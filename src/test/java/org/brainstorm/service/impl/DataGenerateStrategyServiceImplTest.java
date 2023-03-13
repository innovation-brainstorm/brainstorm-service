package org.brainstorm.service.impl;

import org.brainstorm.interfaces.strategy.Type;
import org.brainstorm.service.DataGenerateStrategyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataGenerateStrategyServiceImplTest {

    @Autowired
    DataGenerateStrategyService strategyService;

    @Test
    public void test() {
        Type type = new Type();
        strategyService.generateData(type, 1);
    }
}