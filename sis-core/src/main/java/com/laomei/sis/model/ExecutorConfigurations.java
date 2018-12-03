package com.laomei.sis.model;

import java.util.List;

/**
 * @author laomei on 2018/12/3 19:29
 */
public class ExecutorConfigurations {

    private List<ExecutorConfiguration> executorConfigurations;

    public List<ExecutorConfiguration> getExecutorConfigurations() {
        return executorConfigurations;
    }

    public void setExecutorConfigurations(
            final List<ExecutorConfiguration> executorConfigurations) {
        this.executorConfigurations = executorConfigurations;
    }
}
