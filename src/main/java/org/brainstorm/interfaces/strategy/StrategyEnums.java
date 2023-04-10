package org.brainstorm.interfaces.strategy;

import org.brainstorm.interfaces.strategy.impl.*;

public enum StrategyEnums {
    AI {
        @Override
        public Strategy getStrategy() {
            return new AIDataGenerateStrategyImpl();
        }
    },
    IncrementalList {
        @Override
        public Strategy getStrategy() {
            return new IncrementalListDataGenerateStrategyImpl();
        }
    }, NumberList {
        @Override
        public Strategy getStrategy() {
            return new NumberListDataGenerateStrategyImpl();
        }
    }, RandomSelection {
        @Override
        public Strategy getStrategy() {
            return new RandomSelectionDataGenerateStrategyImpl();
        }
    }, SpecificValue {
        @Override
        public Strategy getStrategy() {
            return new SpecificValueDataGenerateStrategyImpl();
        }
    };

    public abstract Strategy getStrategy();
}
