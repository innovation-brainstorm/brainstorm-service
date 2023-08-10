package org.brainstorm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.brainstorm.datasource.modle.Record;
import org.brainstorm.datasource.modle.*;
import org.brainstorm.service.DataSourceService;
import org.brainstorm.utils.JdbcUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DataSourceServiceImpl implements DataSourceService {

	@Autowired
	ExMetaDataImpl exMetaDataImpl;

	@Override
	public void genData(DataSourceInfo dataSourceInfo,String tableName) {
		if(Objects.isNull(dataSourceInfo) || StringUtils.isBlank(tableName)){
			return;
		}
		List<Record> data = getData(dataSourceInfo, tableName);

		// todo
		String sql = genData(data);
	}

	public List<Record> getData(DataSourceInfo dataSourceInfo, String tableName){
		Connection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		List<Record> recordList = new ArrayList<>();
		try {
			connection = JdbcUtils.getConnection(dataSourceInfo.getUrl(), dataSourceInfo.getUsername(), dataSourceInfo.getPassword());
			String sql = "SELECT * from "+ tableName + " LIMIT"+ dataSourceInfo.getMaxRows();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sql);

			TableInfo tableInfo = exMetaDataImpl.getTableInfo(connection, tableName);
			List<Column> columnList = tableInfo.getColumnList();

			while (resultSet.next()) {
				Record record = new Record();
				List<ColumnWithValue> columnWithValues = new ArrayList<>();
				// 下面是分析一行数据中，每一列的值
				for (Column column : columnList) {
					// 拿到每一列的值
					String columnName = column.getColumnName();
					String columnValue = resultSet.getString(columnName);

					ColumnWithValue columnWithValue = new ColumnWithValue();
					columnWithValue.setColumnValue(columnValue);
					columnWithValue.populateColumnWithValue(column,columnWithValue);

					columnWithValues.add(columnWithValue);
				}
				record.setColumn(columnWithValues);
				recordList.add(record);
			}
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		} finally {
			JdbcUtils.close(statement,connection,resultSet);
		}
		return recordList;
	}


	// todo
	public String genData(List<Record> data){

		return "sql";
	}
}
