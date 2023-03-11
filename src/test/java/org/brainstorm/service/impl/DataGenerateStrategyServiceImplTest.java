package org.brainstorm.service.impl;

import org.brainstorm.interfaces.strategy.Type;
import org.brainstorm.service.DataGenerateStrategyService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
class DataGenerateStrategyServiceImplTest {

    @Autowired
    DataGenerateStrategyService strategyService;

    @Test
    public void test() {
        Type type = new Type();
        strategyService.generateData(type, 1);
    }
}