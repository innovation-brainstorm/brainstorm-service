package org.brainstorm.service;

import org.brainstorm.interfaces.strategy.Type;

import java.util.List;

public interface DataGenerateStrategyService {
    StrategyData generateData(Type type, int num);
}
