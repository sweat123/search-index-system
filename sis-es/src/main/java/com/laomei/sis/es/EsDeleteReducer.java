package com.laomei.sis.es;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yulei
 * @author laomei on 2019/3/12 20:22
 */
public class EsDeleteReducer extends AbstractEsReducer {

    public EsDeleteReducer(final SchemaHelper schemaHelper, final BulkProcessor bulkProcessor,
            final RestHighLevelClient client, final EsConnectorConfig config) {
        super(schemaHelper, bulkProcessor, client, config);
    }

    @Override
    public void reduce(final List<SisRecord> sisRecords) {
        List<DeleteRequest> requests = sisRecords.stream().filter(record -> record.hasValue(ID))
                .map(record -> record.getValue(ID))
                .map(id -> new DeleteRequest(index, type, String.valueOf(id)))
                .collect(Collectors.toList());
        if (requests.isEmpty()) {
            return;
        }
        for (DeleteRequest request : requests) {
            doReducer(request);
        }
    }
}
