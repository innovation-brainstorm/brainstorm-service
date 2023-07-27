package org.brainstorm.service;

import org.brainstorm.interfaces.strategy.Strategy;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;

import java.sql.SQLException;
import java.util.List;

public interface DataGenerateStrategyService<T> {

    List<Strategy> getAllSupportStrategy();

    StrategyData generateData(Session session, Task task) throws SQLException, ClassNotFoundException;///

    Strategy getSelectedStrategy(int id);
}
