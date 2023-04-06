package org.brainstorm.datasource.modle;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class ColumnWithValue extends Column{

	private String columnValue;

	public void populateColumnWithValue(Column column,ColumnWithValue columnWithValue){
		BeanUtils.copyProperties(column,columnWithValue);
	}
}
