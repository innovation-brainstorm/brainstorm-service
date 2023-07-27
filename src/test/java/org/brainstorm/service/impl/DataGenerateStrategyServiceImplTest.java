package org.brainstorm.service.impl;


import org.brainstorm.service.StrategyData;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DataGenerateStrategyServiceImplTest {

    @Autowired
    DataGenerateStrategyServiceImpl strategyService;

    @Test
    public void testGetAllSupportStrategy() {
        //DataType dataType = new DataType("Integer");
        Assert.assertEquals(5, strategyService.getAllSupportStrategy().size());
        System.out.println(strategyService.getAllSupportStrategy());
    }
/*
    @Test
    public void testGenerateData() {
        DataType incrementalDataType = new DataType("Integer", 100, 1);
        // 测试无法使用本地Strategy处理的数据情况下是否会返回空的 StrategyData 对象
        StrategyData emptyStrategyData = strategyService.generateData(incrementalDataType, 100);
        Assert.assertEquals(null, emptyStrategyData.getData());

        // 测试正常生成数据
        StrategyData strategyData = strategyService.generateData(incrementalDataType, 1);
        Assert.assertFalse(strategyData.getData().isEmpty());
    }*/
}