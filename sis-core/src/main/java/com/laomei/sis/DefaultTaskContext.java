package com.laomei.sis;

import com.laomei.sis.exception.JdbcContextException;
import com.laomei.sis.executor.NoopExecutor;
import com.laomei.sis.executor.SqlExecutor;
import com.laomei.sis.model.DataSourceConfiguration;
import com.laomei.sis.model.DataSourceConfigurations;
import com.laomei.sis.model.ExecutorConfiguration;
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
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

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
    public void initReducer() throws JdbcContextException {
        if (!jdbcInited) {
            initJdbcContext();
        }
        initSinkReducer();
    }

    public abstract void initSinkReducer();

    @Override
    public void initTransform() throws JdbcContextException {
        if (!jdbcInited) {
            initJdbcContext();
        }
        String sourceConfigurations = config.sourceConfigurations;
        if (StringUtils.isEmpty(sourceConfigurations)) {
            throw new IllegalStateException("source configuration can not be null");
        }
        SourceConfigurations configurations = JsonUtil.parse(sourceConfigurations, SourceConfigurations.class);
        configurations.getSourceConfigurations().forEach(configuration -> {
            String topic = configuration.getTopic();
            ChainTransform chainTransform = getChainTransform(configuration);
            transformContext.addTransforms(topic, chainTransform);
        });
    }

    @Override
    public void initExecutor() throws JdbcContextException {
        if (!jdbcInited) {
            initJdbcContext();
        }
        String executorConfigurations = config.executorConfigurations;
        if (StringUtils.isEmpty(executorConfigurations)) {
            executor = new NoopExecutor();
            return;
        }
        ExecutorConfigurations configurations = JsonUtil.parse(executorConfigurations, ExecutorConfigurations.class);
        if (configurations == null) {
            executor = new NoopExecutor();
            return;
        }
        List<ExecutorConfiguration> configurationList = configurations.getExecutorConfigurations();
        if (CollectionUtils.isEmpty(configurationList)) {
            executor = new NoopExecutor();
        } else {
            executor = new SqlExecutor(configurations, jdbcContext);
        }
    }

    @Override
    public void initJdbcContext() throws JdbcContextException {
        if (jdbcInited) {
            return;
        }
        jdbcInited = true;
        jdbcContext = new JdbcContext();
        initDefaultDataSource();
    }

    private void initDefaultDataSource() throws JdbcContextException {
        //create default mysql jdbc configuration;
        //default jdbc template will be used in SqlTransform
        String url = config.defaultMysqlUrl;
        String username = config.defaultMysqlUsername;
        String password = config.defaultMysqlPassword;
        if (StringUtils.hasLength(url)) {
            registerDataSource(url, username, password, DEFAULT_JDBC_TEMPLATE);
        }

        //register other dataSource
        String registerDataSourceConfigure = config.mysqlDataSourceRegister;
        if (StringUtils.isEmpty(registerDataSourceConfigure)) {
            return;
        }
        DataSourceConfigurations dataSourceConfigurations = JsonUtil.parse(registerDataSourceConfigure, DataSourceConfigurations.class);
        if (dataSourceConfigurations != null) {
            for (DataSourceConfiguration dataSourceConfiguration : dataSourceConfigurations.getDataSources()) {
                String externalUrl = dataSourceConfiguration.getUrl();
                String externalUsername = dataSourceConfiguration.getUsername();
                String externalPassword = dataSourceConfiguration.getPassword();
                String alias = dataSourceConfiguration.getAlias();
                if (StringUtils.hasLength(externalUrl)) {
                    registerDataSource(externalUrl, externalUsername, externalPassword, alias);
                }
            }
        }
    }

    private void registerDataSource(String url, String username, String password, String alias)
            throws JdbcContextException {
        jdbcContext.addDataSource(url, username, password, alias);
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
            transforms.add(new SqlTransform(configuration.getSqlTrans(), jdbcContext));
        }
        return new ChainTransform(transforms);
    }
}
