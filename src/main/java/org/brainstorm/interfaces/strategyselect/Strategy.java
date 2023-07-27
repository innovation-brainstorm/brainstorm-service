package org.brainstorm.interfaces.strategyselect;

import java.sql.SQLException;
import java.util.List;


public interface Strategy {
    int getIdentifier();

    String getStrategyName();


    List<String> dataselected(String column, String table, long quantity) throws SQLException;
}
