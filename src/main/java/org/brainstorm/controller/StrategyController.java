package org.brainstorm.controller;

import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;
import org.brainstorm.service.DataGenerateStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/strategy")
public class StrategyController<T> {
    @Autowired
    private DataGenerateStrategyService dataGenerateStrategyService;

    @Autowired
    private Strategy strategy;

    @GetMapping("/getStrategy")
    public List<Strategy> getSupportableStrategy(@RequestBody DataType dataType) {
        List<Strategy> SupportableStrategy = dataGenerateStrategyService.getAllSupportStrategy(dataType);
        return SupportableStrategy;
    }

    @GetMapping("getData")
    public List<T> getMockedData(@RequestBody DataType dataType) {
        List<T> MockedData = strategy.generate(dataType);
        return MockedData;
    }

}
