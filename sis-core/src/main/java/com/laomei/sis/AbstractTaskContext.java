package com.laomei.sis;

/**
 * @author laomei on 2018/12/2 15:09
 */
public abstract class AbstractTaskContext {

    protected Reducer reducer;

    protected Transform transform;

    protected Executor executor;

    public abstract void initSolrCloudReducer();

    public abstract void initTransform();

    public abstract void initExecutor();

    public Reducer reducer() {
        return this.reducer;
    }

    public Transform transform() {
        return this.transform;
    }

    public Executor executor() {
        return this.executor;
    }

    public void close() {
        if (reducer != null) {
            reducer.close();
        }
        if (transform != null) {
            transform.close();
        }
        if (executor != null) {
            executor.close();
        }
    }
}
