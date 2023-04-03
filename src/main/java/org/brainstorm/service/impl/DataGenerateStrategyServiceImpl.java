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
public class DataGenerateStrategyServiceImpl implements DataGenerateStrategyService {

    List<Strategy> strategyList = new ArrayList<>(
        Arrays.asList(new IncrementalListDataGenerateStrategyImpl(), new NumberListDataGenerateStrategyImpl(),
            new RandomSelectionDataGenerateStrategyImpl(), new SpecificValueDataGenerateStrategyImpl()));

    public List<Strategy> getAllSupportStrategy(DataType dataType) {
        List<Strategy> allSupportDataGenerateStrategyList = new ArrayList<>();
        strategyList.forEach(e -> {
            if (e.canSupport(dataType)) {
                allSupportDataGenerateStrategyList.add(e);
            }
        });
        return allSupportDataGenerateStrategyList;
    }

    @Override
    public StrategyData generateData(DataType dataType) {
        return null;
    }

    // @Override
    // public StrategyData generateData(DataType dataType) {
    //
    //
    // List<Strategy> strategyList = getAllSupportStrategy(dataType);
    // if (strategyList.isEmpty()) {
    // //TODO 处理无法使用本地Strategy处理的数据
    // return new StrategyData();
    // }
    // Strategy bestStrategy = getBestStrategy(strategyList);
    //
    // List generate = bestStrategy.generate(dataType);
    //
    // StrategyData strategyData = new StrategyData();
    // strategyData.setData(generate);//add data to strategyData
    // return strategyData;
    // }
    //
    // //TODO 对allSupportDataGenerateStrategyList进行排序，简单做法也可以匹配第一个，anyway
    // private Strategy getBestStrategy(List<Strategy> list) {
    // return list.get(0);
    // }
}
