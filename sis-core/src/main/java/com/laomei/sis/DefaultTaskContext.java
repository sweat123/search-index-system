package com.laomei.sis;

import com.alibaba.fastjson.JSON;
import com.laomei.sis.executor.NoopExecutor;
import com.laomei.sis.executor.SqlExecutor;
import com.laomei.sis.model.ExecutorConfigurations;
import com.laomei.sis.model.Fields;
import com.laomei.sis.model.SourceConfiguration;
import com.laomei.sis.model.SourceConfigurations;
import com.laomei.sis.transform.ChainTransform;
import com.laomei.sis.transform.FieldTransform;
import com.laomei.sis.transform.FilterTransform;
import com.laomei.sis.transform.PlaceholderTransform;
import com.laomei.sis.transform.RecordTransform;
import com.laomei.sis.transform.SqlTransform;

import java.util.ArrayList;
import java.util.List;

import static com.laomei.sis.JdbcContext.DEFAULT_JDBC_TEMPLATE;

/**
 * @author laomei on 2018/12/3 16:07
 */
public abstract class DefaultTaskContext extends AbstractTaskContext {

    private final BaseConnectorConfig config;

    public DefaultTaskContext(final String name, final BaseConnectorConfig config) {
        super(name);
        this.config = config;
    }

    @Override
    public void initReducer() {
        if (!jdbcInited) {
            initJdbcContext();
        }
        initSinkReducer();
    }

    public abstract void initSinkReducer();

    @Override
    public void initTransform() {
        if (!jdbcInited) {
            initJdbcContext();
        }
        String sourceConfigurations = config.sourceConfigurations;
        SourceConfigurations configurations = JSON.parseObject(sourceConfigurations, SourceConfigurations.class);
        configurations.getSourceConfigurations().forEach(configuration -> {
            String topic = configuration.getTopic();
            ChainTransform chainTransform = getChainTransform(configuration);
            transformContext.addTransforms(topic, chainTransform);
        });
    }

    @Override
    public void initExecutor() {
        if (!jdbcInited) {
            initJdbcContext();
        }
        String executorConfigurations = config.executorConfigurations;
        ExecutorConfigurations configurations = JSON.parseObject(executorConfigurations, ExecutorConfigurations.class);
        if (configurations.getExecutorConfigurations().isEmpty()) {
            executor = new NoopExecutor();
        } else {
            executor = new SqlExecutor(configurations, jdbcContext);
        }
    }

    @Override
    public void initJdbcContext() {
        if (jdbcInited) {
            return;
        }
        jdbcInited = true;
        jdbcContext = new JdbcContext();
        //create default mysql jdbc configuration;
        //default jdbc template will be used in SqlTransform
        String url = config.defaultMysqlUrl;
        String username = config.defaultMysqlUsername;
        String password = config.defaultMysqlPassword;
        jdbcContext.addDataSource(url, username, password, DEFAULT_JDBC_TEMPLATE);
    }

    private ChainTransform getChainTransform(SourceConfiguration configuration) {
        List<Transform> transforms = new ArrayList<>(4);
        if (configuration.getFields() != null) {
            Fields fields = configuration.getFields();
            transforms.add(new FieldTransform(fields.getFields()));
        }
        //FIXME: now record transform is add into chain transform by default;
        transforms.add(new RecordTransform());
        if (configuration.getFilter() != null) {
            transforms.add(new FilterTransform(configuration.getFilter()));
        }
        if (configuration.getPlaceholder() != null) {
            transforms.add(new PlaceholderTransform(configuration.getPlaceholder().getConfig()));
        } else if (configuration.getSqlTrans() != null) {
            transforms.add(new SqlTransform(configuration.getSqlTrans().getSql(), jdbcContext.getDefaultJdbcTemplate()));
        }
        return new ChainTransform(transforms);
    }
}
