package org.brainstorm.service;

import org.brainstorm.interfaces.strategy.DataType;

import java.util.List;

public interface DataGenerateStrategyService {
    StrategyData generateData(DataType dataType, int num);
}
