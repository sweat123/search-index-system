package com.laomei.sis.solr;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import com.laomei.sis.JavaTypeConverterUtil;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author laomei on 2018/12/1 20:43
 */
public class SolrUpdateReducer extends AbstractSolrReducer {

    public SolrUpdateReducer(SchemaHelper schemaHelper, SolrConnectorConfig configs, final SolrClient solrClient) {
        super(schemaHelper, solrClient, configs.solrCloudCollection);
    }

    @Override
    public void reduce(List<SisRecord> sisRecords) {
        List<SolrInputDocument> documents = new ArrayList<>(sisRecords.size());
        for (SisRecord sisRecord : sisRecords) {
            Map<String, Object> ctx = sisRecord.getContext();
            SolrInputDocument document = getSolrDocument(ctx);
            if (document != null) {
                documents.add(document);
            }
        }
        if (documents.isEmpty()) {
            return;
        }
        try {
            solrClient.add(solrCollection, documents);
            solrClient.commit(solrCollection);
        } catch (Exception e) {
            logger.error("update solr collection {} failed.", solrCollection, e);
            return;
        }
        logger.info("update solr collection {}, records size {}", solrCollection, documents.size());
    }

    private SolrInputDocument getSolrDocument(Map<String, Object> context) {
        SolrInputDocument document = new SolrInputDocument();
        try {
            enumerateContext(context, document);
        } catch (Exception e) {
            logger.error("get solr document failed.", e);
            return null;
        }
        return document;
    }

    private void enumerateContext(Map<String, Object> context, SolrInputDocument document) {
        if (null == context) {
            logger.error("context can not be null!");
            throw new NullPointerException("context can not be null!");
        }
        for (Map.Entry<String, Object> element : context.entrySet()) {
            String key = element.getKey();
            Object value = element.getValue();
            if (value instanceof Map) {
                enumerateContext((Map<String, Object>) value, document);
            } else {
                String targetClass = schemaHelper.getTargetClass(key);
                if (StringUtils.isEmpty(targetClass)) {
                    // There is no field in solr having the same name with current key;
                    continue;
                }
                String javaType = toJavaType(targetClass).getSimpleName();
                Object solrTypeObj = JavaTypeConverterUtil.javaTypeConvertToTargetType(value, javaType);
                document.addField(key, solrTypeObj);
            }
        }
    }

    private Class toJavaType(String clazzImpl) {
        switch (clazzImpl) {
        case "solr.BoolField":
            return Boolean.class;
        case "solr.LongPointField":
            // fall through
        case "solr.TrieLongField":
            return Long.class;
        case "solr.IntPointField":
            // fall through
        case "solr.TrieIntField":
            return Integer.class;
        case "solr.FloatPointField":
            // fall through
        case "solr.TrieFloatField":
            return Float.class;
        case "solr.DoublePointField":
            // fall through
        case "solr.TrieDoubleField":
            return Double.class;
        case "solr.DatePointField":
            // fall through
        case "solr.TrieDateField":
            return DateTime.class;
        default:
            return String.class;
        }
    }
}
