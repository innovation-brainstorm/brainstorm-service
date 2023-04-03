package org.brainstorm.interfaces.strategy;

import java.util.List;

import lombok.Data;

/**
 * different type
 */
@Data
public class DataType<T> {
    // TODO 将要产生的数据类型，有什么要求等 封装在这个类中，
    private String typeName;
    private int startValue;
    private int length;
    private T min;
    private T max;
    private T value;
    private List<T> values;

    public DataType(String typeName) {
        this.typeName = typeName;
    }

    // 给定范围内的随机数列表
    public DataType(String typeName, int length, T min, T max) {
        this.typeName = typeName;
        this.length = length;
        this.max = max;
        this.min = min;
    }

    // 给定List中选取随机值
    public DataType(List<T> value, int length) {
        this.values = value;
        this.length = length;
    }

    // 整数自增
    public DataType(String typeName, int length, int startValue) {
        this.startValue = startValue;
        this.length = length;
        this.typeName = typeName;
    }

    // 生成全部为相同的特定值列表
    public DataType(T value, int length) {
        this.value = value;
        this.length = length;
    }

}
