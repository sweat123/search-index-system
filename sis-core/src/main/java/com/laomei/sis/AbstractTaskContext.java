package com.laomei.sis;

/**
 * @author laomei on 2018/12/2 15:09
 */
public abstract class AbstractTaskContext {

    protected String    name;

    protected Reducer   reducer;

    protected TransformContext transformContext;

    protected Executor  executor;

    public AbstractTaskContext(String name) {
        this.name = name;
    }

    public abstract void initSolrCloudReducer();

    public abstract void initTransform();

    public abstract void initExecutor();

    public String  getTaskName() {
        return name;
    }

    public Reducer reducer() {
        return this.reducer;
    }

    public TransformContext transformContext() {
        return this.transformContext;
    }

    public Executor executor() {
        return this.executor;
    }

    public void close() {
        if (reducer != null) {
            reducer.close();
        }
        if (transformContext != null) {
            transformContext.shutdown();
        }
        if (executor != null) {
            executor.close();
        }
    }
}
