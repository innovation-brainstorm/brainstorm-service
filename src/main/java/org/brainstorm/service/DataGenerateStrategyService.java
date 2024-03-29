package org.brainstorm.service;

import org.brainstorm.interfaces.strategyselect.Strategy;
import org.brainstorm.model.Session;
import org.brainstorm.model.Task;

import java.sql.SQLException;
import java.util.List;

public interface DataGenerateStrategyService<T> {

    List<Strategy> getAllSupportStrategy();

    StrategyData generateData(Session session, Task task) throws SQLException, ClassNotFoundException;///

    org.brainstorm.interfaces.strategyselect.Strategy getSelectedStrategy(int id);
}
