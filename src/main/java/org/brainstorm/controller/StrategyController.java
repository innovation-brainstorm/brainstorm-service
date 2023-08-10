package org.brainstorm.controller;

import org.brainstorm.service.DataGenerateStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/strategy")
public class StrategyController<T> {
    @Autowired
    private DataGenerateStrategyService dataGenerateStrategyService;

    @GetMapping("/getStrategy")
    public List getSupportableStrategy() {
        List SupportableStrategy = dataGenerateStrategyService.getAllSupportStrategy();
        return SupportableStrategy;
    }

}
