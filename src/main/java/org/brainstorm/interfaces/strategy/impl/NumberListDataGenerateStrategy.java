package org.brainstorm.interfaces.strategy.impl;

import org.brainstorm.interfaces.strategy.DataGenerateStrategy;
import org.brainstorm.interfaces.strategy.Type;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NumberListDataGenerateStrategy  implements DataGenerateStrategy <Integer>{
    @Override
    public boolean canSupport(Type type) {
        return true;
    }

    @Override
    public List<Integer> generate(int num) {
        return null;
    }
}
