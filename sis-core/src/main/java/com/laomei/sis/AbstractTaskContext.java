package com.laomei.sis;

/**
 * @author laomei on 2018/12/2 15:09
 */
public abstract class AbstractTaskContext {

    protected String    name;

    protected Reducer   reducer;

    protected TransformContext transformContext;

    protected Executor  executor;

    protected JdbcContext jdbcContext;

    protected volatile boolean jdbcInited;

    public AbstractTaskContext(String name) {
        this.name = name;
        this.jdbcInited = false;
    }

    public abstract void initReducer();

    public abstract void initTransform();

    public abstract void initExecutor();

    public abstract void initJdbcContext();

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
        if (jdbcContext != null) {
            jdbcContext.close();
        }
    }
}
