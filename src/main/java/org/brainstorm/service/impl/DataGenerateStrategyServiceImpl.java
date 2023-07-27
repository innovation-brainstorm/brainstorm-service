package org.brainstorm.service.impl;

import lombok.AllArgsConstructor;
import org.brainstorm.interfaces.strategyselect.Strategy;
import org.brainstorm.interfaces.strategyselect.impl.Incremental;
import org.brainstorm.interfaces.strategyselect.impl.randomselect;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;
import org.brainstorm.service.DataGenerateStrategyService;
import org.brainstorm.service.StrategyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class DataGenerateStrategyServiceImpl<T> implements DataGenerateStrategyService {
    @Autowired
    Incremental incremental;
    @Autowired
    randomselect randomselect;

    @Override
    public List<Strategy> getAllSupportStrategy() {
        List<Strategy> strategyList = new ArrayList<>();
        strategyList.add(incremental);
        strategyList.add(randomselect);
        return strategyList;
    }

    @Override
    public StrategyData generateData(Session session, Task task) throws SQLException, ClassNotFoundException {
        int id = task.getStrategy();
        StrategyData strategyData = new StrategyData();
        if (id > 2 || id < 0)
            return strategyData;
        List<String> resultSet = null;
        switch (id) {
            case (1):
                resultSet = incremental.dataselected(task.getColumnName(), session.getTableName(), session.getExpectedCount());
                break;
            case (2):
                resultSet = randomselect.dataselected(task.getColumnName(), session.getTableName(), session.getExpectedCount());
                break;
        }
        strategyData.setData(resultSet);
        return strategyData;
        //        Strategy strategy = StrategyEnums.values()[id].getStrategy();
//        System.out.println(strategy);
//        List<T> generate = strategy.generate();
//
//        StrategyData strategyData = new StrategyData();
//        strategyData.setData(generate);// add data to strategyData
//        return strategyData;
    }

    @Override
    public Strategy getSelectedStrategy(int id) {
        Strategy strategy = null;
        switch (id) {
            case 1:
                strategy = incremental;
                break;
            case 2:
                strategy = randomselect;
                break;

        }
        return strategy;
    }
}
