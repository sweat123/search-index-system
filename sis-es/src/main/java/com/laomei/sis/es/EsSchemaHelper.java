package com.laomei.sis.es;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laomei.sis.SchemaHelper;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yulei
 * @author laomei on 2019/3/12 19:40
 */
public class EsSchemaHelper implements SchemaHelper {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final RestHighLevelClient client;

    private final String              index;

    private final String              type;

    private Map<String, String>       schemaMap;

    public EsSchemaHelper(final RestHighLevelClient client, String index, String type) {
        this.client = client;
        this.index = index;
        this.type = type;
    }

    @Override
    public void init() {
        Response response = null;
        final JsonNode root;
        try {
            response = client.getLowLevelClient().performRequest("GET", String.format("/%s/_mapping/%s", index, type));
            root = OBJECT_MAPPER.readTree(response.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
        if (root == null) {
            throw new RuntimeException("can not get mapping");
        }
        Map<String, String> map = new HashMap<>();
        JsonNode properties = root.iterator().next().get("mappings").get(type).get("properties");
        properties.fields().forEachRemaining(stringJsonNodeEntry -> {
            String propertyName = stringJsonNodeEntry.getKey();
            String propertyType = stringJsonNodeEntry.getValue().get("type").asText();
            map.put(propertyName, propertyType);
        });
        this.schemaMap = map;
    }

    @Override
    public String getTargetClass(final String key) {
        return schemaMap.get(key);
    }

    @Override
    public void close() {
        if (schemaMap != null) {
            schemaMap.clear();
        }
    }
}
