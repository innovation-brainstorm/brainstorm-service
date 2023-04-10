package org.brainstorm.interfaces.strategy;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;

public class DefaultDataType {
    private static int length = 1000;

    public static void setLength(int length) {
        DefaultDataType.length = length;
    }

    public static DataType getDefaultDataType(StrategyEnums strategyEnums) {
        String typeName = "Integer";
        switch (strategyEnums) {
            case IncrementalList:
                return new DataType(typeName, length, 0);
            case NumberList:
                return new DataType(typeName, length, 999, 9999);
            case RandomSelection:
                ArrayList<String> randomString = new ArrayList<>();
                for (int i = 0; i < 10; i++) randomString.add(RandomStringUtils.randomAlphanumeric(10));
                return new DataType(randomString, length);
            case SpecificValue:
                return new DataType("111", length);//todo
            default:
                return new DataType("AI");
        }
    }
}
