package com.laomei.sis.solr;

import com.laomei.sis.SchemaHelper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author laomei on 2018/12/4 19:28
 */
public class SolrSchemaHelper implements SchemaHelper {
    private static Logger log = LoggerFactory.getLogger(SolrSchemaHelper.class);
    private static final String SCHEMA = "schema";
    private static final String FIELDS = "fields";
    private static final String FIELD_TYPES = "fieldTypes";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String CLASS = "class";

    private final SolrClient solrClient;

    private final String solrCollection;

    private Map<String, String> schemaMap;

    public SolrSchemaHelper(final String solrCollection, SolrClient solrClient) {
        this.solrCollection = solrCollection;
        this.schemaMap = Collections.emptyMap();
        this.solrClient = solrClient;
    }

    @Override
    public void init() {
        try {
            NamedList<Object> namedList = solrClient.request(new SchemaRequest(), solrCollection);
            SimpleOrderedMap orderedMap = (SimpleOrderedMap) namedList.get(SCHEMA);
            List<SimpleOrderedMap> fields = (List<SimpleOrderedMap>) orderedMap.get(FIELDS);
            List<SimpleOrderedMap> fieldTypes = (List<SimpleOrderedMap>) orderedMap.get(FIELD_TYPES);
            schemaMap = combine(fields, fieldTypes);
        } catch (Exception e) {
            log.error("get solr schema failed", e);
            throw new RuntimeException("get solr schema failed", e);
        }
    }

    @Override
    public String getTargetClass(final String key) {
        return schemaMap.get(key);
    }

    @Override
    public void close() {
    }

    private Map<String, String> combine(List<SimpleOrderedMap> fields, List<SimpleOrderedMap> fieldTypes) {
        Map<String, String> schemaMap = new HashMap<>();
        Map<String, String> fieldTypeMap = new HashMap<>();
        fieldTypes.forEach(k -> fieldTypeMap.put((String) k.get(NAME), (String) k.get(CLASS)));
        fields.forEach(k -> {
            String name = (String) k.get(NAME);
            String type = (String) k.get(TYPE);
            schemaMap.put(name, fieldTypeMap.get(type));
        });
        return schemaMap;
    }
}
