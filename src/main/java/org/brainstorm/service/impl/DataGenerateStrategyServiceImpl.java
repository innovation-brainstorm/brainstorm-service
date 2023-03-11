package org.brainstorm.service.impl;

import lombok.AllArgsConstructor;
import org.brainstorm.interfaces.strategy.DataGenerateStrategy;
import org.brainstorm.interfaces.strategy.Type;
import org.brainstorm.service.DataGenerateStrategyService;
import org.brainstorm.service.StrategyData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DataGenerateStrategyServiceImpl implements DataGenerateStrategyService {

    List<DataGenerateStrategy> dataGenerateStrategyList;

    /**
     *
     */
    @Override
    public StrategyData generateData(Type type, int num) {
        List <DataGenerateStrategy> allSupportDataGenerateStrategyList = new ArrayList<>();
        dataGenerateStrategyList.forEach( e -> {
                if (e.canSupport(type)) {
                    allSupportDataGenerateStrategyList.add(e);
                }
            }
        );

        if (allSupportDataGenerateStrategyList.isEmpty()) {
            //TODO 处理无法使用本地Strategy处理的数据
            return new StrategyData();
        }
        DataGenerateStrategy bestStrategy = getBestStrategy(allSupportDataGenerateStrategyList);

        List generate = bestStrategy.generate(num);

        StrategyData strategyData = new StrategyData();
        strategyData.setData(generate);//add data to strategyData
        return strategyData;
    }

    //TODO 对allSupportDataGenerateStrategyList进行排序，简单做法也可以匹配第一个，anyway
    private DataGenerateStrategy getBestStrategy( List<DataGenerateStrategy> list) {
        return list.get(0);
    }
}
