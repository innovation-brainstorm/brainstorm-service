package org.brainstorm.interfaces.strategyselect.impl;

import org.brainstorm.interfaces.strategyselect.Strategy;
import org.brainstorm.service.impl.ValueFromTPDBImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
@Component
public class randomselect implements Strategy {
    @Autowired
    private ValueFromTPDBImpl valueFromTPDB;
    @Override
    public List<String> dataselected(String column, String table, long quantity) throws SQLException {
        String statement = String.format("select %s from %s ORDER BY RAND() limit %d", column, table,quantity);
        return valueFromTPDB.selectDataByStatement(statement);
    }
}
