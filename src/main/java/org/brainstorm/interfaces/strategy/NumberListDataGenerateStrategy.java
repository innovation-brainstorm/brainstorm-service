package org.brainstorm.interfaces.strategy;

import java.util.List;

/**
 * used to generate different strategy
 * 使用时先判断canSupport 然后通过 generate() 产生数据，使用for循环遍历所有策略
 * TODO generate() 可以自定义一些参数
 * @param <T>
 */
public interface NumberListDataGenerateStrategy<T extends Number> extends Strategy{
    boolean canSupport(DataType dataType);
    public <T extends Number> List<T> generateRandomList(int length, T min, T max);
}
