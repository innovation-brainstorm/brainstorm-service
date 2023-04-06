package org.brainstorm.service;

import org.brainstorm.datasource.modle.DataSourceInfo;

public interface DataSourceService {

	void genData(DataSourceInfo dataSourceInfo, String tableName);
}
