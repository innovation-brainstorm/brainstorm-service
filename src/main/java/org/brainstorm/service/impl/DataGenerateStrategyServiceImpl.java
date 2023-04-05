package org.brainstorm.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;
import org.brainstorm.interfaces.strategy.impl.IncrementalListDataGenerateStrategyImpl;
import org.brainstorm.interfaces.strategy.impl.NumberListDataGenerateStrategyImpl;
import org.brainstorm.interfaces.strategy.impl.RandomSelectionDataGenerateStrategyImpl;
import org.brainstorm.interfaces.strategy.impl.SpecificValueDataGenerateStrategyImpl;
import org.brainstorm.service.DataGenerateStrategyService;
import org.brainstorm.service.StrategyData;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class DataGenerateStrategyServiceImpl<T> implements DataGenerateStrategyService {
    @Override
    public List<Strategy> getAllSupportStrategy(DataType dataType) {
        List<Strategy> strategyList = new ArrayList<>(
            Arrays.asList(new IncrementalListDataGenerateStrategyImpl(), new NumberListDataGenerateStrategyImpl(),
                new RandomSelectionDataGenerateStrategyImpl(), new SpecificValueDataGenerateStrategyImpl()));
        List<Strategy> allSupportDataGenerateStrategyList = new ArrayList<>();
        strategyList.forEach(e -> {
            if (e.canSupport(dataType)) {
                allSupportDataGenerateStrategyList.add(e);
            }
        });
        return allSupportDataGenerateStrategyList;
    }

    @Override
    public StrategyData generateData(DataType dataType, int id) {
        if (id > 4 || id < 1)
            return new StrategyData();
        Strategy strategy = getSelectedStrategy(id);
        System.out.println(strategy);
        List<T> generate = strategy.generate(dataType);

        StrategyData strategyData = new StrategyData();
        strategyData.setData(generate);// add data to strategyData
        return strategyData;
    }

    @Override
    public Strategy getSelectedStrategy(int id) {
        Strategy strategy = null;
        switch (id) {
            case 1:
                strategy = new IncrementalListDataGenerateStrategyImpl();
                break;
            case 2:
                strategy = new NumberListDataGenerateStrategyImpl();
                break;
            case 3:
                strategy = new RandomSelectionDataGenerateStrategyImpl();
                break;
            case 4:
                strategy = new SpecificValueDataGenerateStrategyImpl();
                break;
        }
        return strategy;
    }
}
