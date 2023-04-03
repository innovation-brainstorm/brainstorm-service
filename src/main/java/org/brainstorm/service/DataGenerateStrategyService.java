package org.brainstorm.service;

import java.util.List;

import org.brainstorm.interfaces.strategy.DataType;
import org.brainstorm.interfaces.strategy.Strategy;

public interface DataGenerateStrategyService {
    List<Strategy> getAllSupportStrategy(DataType dataType);

    StrategyData generateData(DataType dataType);
}
