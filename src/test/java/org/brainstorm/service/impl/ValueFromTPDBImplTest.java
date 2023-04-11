package org.brainstorm.service.impl;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

class ValueFromTPDBImplTest {

    @Test
    void getDataByColumn() throws SQLException, ClassNotFoundException {
        ValueFromTPDBImpl valueFromTPDB = new ValueFromTPDBImpl();
        List<String> dataByColumn = valueFromTPDB.getDataByColumnMySQL("student", "age", 1, 2);
        Assert.assertTrue(dataByColumn.size() == 2);
    }
}