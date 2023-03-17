package org.brainstorm.interfaces.strategy;

/**
 * different type
 */
public class DataType {
    //TODO 将要产生的数据类型，有什么要求等 封装在这个类中，
    private String typeName;

    public DataType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
