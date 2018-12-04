package com.laomei.sis;

import com.alibaba.fastjson.JSON;
import com.laomei.sis.model.ExecutorConfigurations;
import com.laomei.sis.model.Fields;
import com.laomei.sis.model.SourceConfiguration;
import com.laomei.sis.model.SourceConfigurations;
import com.laomei.sis.transform.ChainTransform;
import com.laomei.sis.transform.FieldTransform;
import com.laomei.sis.transform.FilterTransform;
import com.laomei.sis.transform.PlaceholderTransform;
import com.laomei.sis.transform.RecordTransform;

import java.util.ArrayList;
import java.util.List;

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
    public abstract void initSolrCloudReducer();

    @Override
    public void initTransform() {
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
        String executorConfigrations = config.executorConfigrations;
        ExecutorConfigurations configurations = JSON.parseObject(executorConfigrations, ExecutorConfigurations.class);

    }

    private ChainTransform getChainTransform(SourceConfiguration configuration) {
        List<Transform> transforms = new ArrayList<>(4);
        if (configuration.getFields() != null) {
            Fields fields = configuration.getFields();
            transforms.add(new FieldTransform(fields.getFields()));
        }
        //FIXME now record transform is add into chain transform by default;
        transforms.add(new RecordTransform());
        if (configuration.getFilter() != null) {
            transforms.add(new FilterTransform(configuration.getFilter()));
        }
        if (configuration.getPlaceholder() != null) {
            transforms.add(new PlaceholderTransform(configuration.getPlaceholder().getConfig()));
        }
        return new ChainTransform(transforms);
    }
}
