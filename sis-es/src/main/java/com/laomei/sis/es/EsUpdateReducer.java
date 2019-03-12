package com.laomei.sis.es;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.laomei.sis.es.EsUtil.transformJavaObjectToEs;

/**
 * @author yulei
 * @author laomei on 2019/3/12 20:20
 */
public class EsUpdateReducer extends AbstractEsReducer {


    public EsUpdateReducer(final SchemaHelper schemaHelper, final BulkProcessor bulkProcessor,
            final RestHighLevelClient client, EsConnectorConfig config) {
        super(schemaHelper, bulkProcessor, client, config);
    }

    @Override
    public void reduce(final List<SisRecord> sisRecords) {
        List<IndexRequest> requests = sisRecords.stream().filter(c -> c.hasValue(ID))
                .map(c -> {
                    Map<String, Object> context = c.getContext();
                    String id = context.get(ID).toString();
                    IndexRequest request = new IndexRequest(index, type, id);
                    request.source(expose(new HashMap<>(), context));
                    return request;
                }).collect(Collectors.toList());
        if (!requests.isEmpty()) {
            return;
        }
        for (IndexRequest request : requests) {
            doReducer(request);
        }
    }

    private Map<String, Object> expose(final Map<String, Object> dest, final Map<String, Object> src) {
        src.forEach((k, v) -> {
            if (v == null) {
                return;
            }
            if (v instanceof Map) {
                expose(dest, (Map<String, Object>) v);
                return;
            }
            String esType = schemaHelper.getTargetClass(k);
            final Object value;
            if (v instanceof Iterable) {
                final ArrayList<Object> temp = new ArrayList<>();
                ((Iterable) v).forEach(a -> {
                    final Object b = transformJavaObjectToEs(esType, a);
                    if (b != null) {
                        temp.add(b);
                    }
                });
                value = temp;
            } else if (v.getClass().isArray()) {
                int length = Array.getLength(v);
                final ArrayList<Object> temp = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    Object arrayElement = Array.get(v, i);
                    final Object item = transformJavaObjectToEs(esType, arrayElement);
                    temp.add(item);
                }
                value = temp;
            } else {
                value = transformJavaObjectToEs(esType, v);
            }
            if (value != null) {
                dest.put(k, value);
            }
        });
        return dest;
    }
}
