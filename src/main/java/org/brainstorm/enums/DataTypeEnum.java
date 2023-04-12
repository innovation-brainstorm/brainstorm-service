package org.brainstorm.enums;

public enum DataTypeEnum {

    BIGINT("BIGINT"),
    INT("INT"),
    DOUBLE("DOUBLE"),
    VARCHAR("VARCHAR"),
    TEXT("TEXT"),
    DATE("DATE"),
    DATETIME("DATE");

    private String dateType;

    private DataTypeEnum(String dateType){
        this.dateType = dateType;
    }

    public String get() {
        return dateType;
    }

}
