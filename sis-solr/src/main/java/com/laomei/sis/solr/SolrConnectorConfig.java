package com.laomei.sis.solr;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * @author laomei on 2018/12/1 12:50
 */
public class SolrConnectorConfig extends BaseConnectorConfig {

    public static final  String SOLR_CLOUD_ZK_HOST = "solr.cloud.zk.host";
    private static final String SOLR_CLOUD_ZK_HOST_DOC = "solr cloud zookeeper host";
    private static final String SOLR_CLOUD_ZK_HOST_DISPLAY = "solr cloud zk host";

    public static final  String SOLR_CLOUD_USERNAME = "solr cloud username";
    private static final String SOLR_CLOUD_USERNAME_DOC = "solr cloud username";
    private static final String SOLR_CLOUD_USERNAME_DISPLAY = "solr cloud username";

    public static final  String SOLR_CLOUD_PASSWORD = "solr.cloud.password";
    private static final String SOLR_CLOUD_PASSWORD_DOC = "solr cloud password";
    private static final String SOLR_CLOUD_PASSWORD_DISPLAY = "solr cloud password";

    public static final  String SOLR_CLOUD_COLLECTION = "solr.cloud.collection";
    private static final String SOLR_CLOUD_COLLECTION_DOC = "solr cloud collection name";
    private static final String SOLR_CLOUD_COLLECTION_DISPLAY = "solr cloud collection name";

    public static final  String SOLR_CLOUD_INDEX_MODE = "solr.cloud.index.mode";
    private static final String SOLR_CLOUD_INDEX_MODE_DOC = "solr cloud index mode; delete or update (include insert)";
    private static final String SOLR_CLOUD_INDEX_MODE_DISPLAY = "solr cloud index mode; delete or update (include insert)";

    public static final  String SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE = "solr.cloud.pool.max.per.route";
    private static final String SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE_DOC = "solr cloud connect pool max per route";
    private static final String SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE_DISPLAY = "solr cloud connect pool max per route";

    public static final  String SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL = "solr.cloud.pool.max.total";
    private static final String SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL_DOC = "solr cloud connect pool max total value";
    private static final String SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL_DISPLAY = "solr cloud connect pool max total";

    public static final  String SOLR_CLOUD_SOCKET_TIMEOUT = "solr.cloud.socket.timeout";
    private static final String SOLR_CLOUD_SOCKET_TIMEOUT_DOC = "solr cloud socket timeout in ms";
    private static final String SOLR_CLOUD_SOCKET_TIMEOUT_DISPLAY = "solr cloud socket timeout in ms";

    public static final  String SOLR_CLOUD_CONNECT_TIMEOUT = "solr.cloud.connect.timeout";
    private static final String SOLR_CLOUD_CONNECT_TIMEOUT_DOC = "solr cloud connect timeout in ms";
    private static final String SOLR_CLOUD_CONNECT_TIMEOUT_DISPLAY = "solr cloud connect timeout in ms";

    public static final  String SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT = "solr.cloud.connect.request.timeout";
    private static final String SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT_DOC = "solr cloud connect request timeout in ms";
    private static final String SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT_DISPLAY = "solr cloud connect request timeout in ms";

    //group
    private static final String SOLR_GROUP = "Solr";

    static {
        CONFIG_DEF
                .define(SOLR_CLOUD_ZK_HOST, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SOLR_CLOUD_ZK_HOST_DOC,
                        SOLR_GROUP, 1, ConfigDef.Width.LONG, SOLR_CLOUD_ZK_HOST_DISPLAY)
                .define(SOLR_CLOUD_USERNAME, ConfigDef.Type.STRING, "",
                        ConfigDef.Importance.LOW, SOLR_CLOUD_USERNAME_DOC,
                        SOLR_GROUP, 2, ConfigDef.Width.LONG, SOLR_CLOUD_USERNAME_DISPLAY)
                .define(SOLR_CLOUD_PASSWORD, ConfigDef.Type.STRING, "",
                        ConfigDef.Importance.LOW, SOLR_CLOUD_PASSWORD_DOC,
                        SOLR_GROUP, 3, ConfigDef.Width.LONG, SOLR_CLOUD_PASSWORD_DISPLAY)
                .define(SOLR_CLOUD_COLLECTION, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SOLR_CLOUD_COLLECTION_DOC,
                        SOLR_GROUP, 4, ConfigDef.Width.LONG, SOLR_CLOUD_COLLECTION_DISPLAY)
                .define(SOLR_CLOUD_INDEX_MODE, ConfigDef.Type.STRING, "update",
                        ConfigDef.Importance.MEDIUM, SOLR_CLOUD_INDEX_MODE_DOC,
                        SOLR_GROUP, 5, ConfigDef.Width.MEDIUM, SOLR_CLOUD_INDEX_MODE_DISPLAY)
                .define(SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE, ConfigDef.Type.INT, 20,
                        ConfigDef.Importance.LOW, SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE_DOC,
                        SOLR_GROUP, 6, ConfigDef.Width.MEDIUM, SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE_DISPLAY)
                .define(SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL, ConfigDef.Type.INT, 100,
                        ConfigDef.Importance.LOW, SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL_DOC,
                        SOLR_GROUP, 7, ConfigDef.Width.MEDIUM, SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL_DISPLAY)
                .define(SOLR_CLOUD_SOCKET_TIMEOUT, ConfigDef.Type.INT, 30000,
                        ConfigDef.Importance.LOW, SOLR_CLOUD_SOCKET_TIMEOUT_DOC,
                        SOLR_GROUP, 8, ConfigDef.Width.MEDIUM, SOLR_CLOUD_SOCKET_TIMEOUT_DISPLAY)
                .define(SOLR_CLOUD_CONNECT_TIMEOUT, ConfigDef.Type.INT, 30000,
                        ConfigDef.Importance.LOW, SOLR_CLOUD_CONNECT_TIMEOUT_DOC,
                        SOLR_GROUP, 9, ConfigDef.Width.MEDIUM, SOLR_CLOUD_CONNECT_TIMEOUT_DISPLAY)
                .define(SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT, ConfigDef.Type.INT, 30000,
                        ConfigDef.Importance.LOW, SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT_DOC,
                        SOLR_GROUP, 10, ConfigDef.Width.MEDIUM, SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT_DISPLAY);
    }

    public static ConfigDef getConfigDef() {
        return CONFIG_DEF;
    }

    public final String solrCloudZkHost;

    public final String solrCloudUsername;

    public final String solrCloudPassword;

    public final String solrCloudCollection;

    public final String solrCloudIndexMode;

    public final int solrCloudConnectPoolMaxPerRoute;

    public final int solrCloudConnectPoolMaxTotal;

    public final int solrCloudSocketTimeout;

    public final int solrCloudConnectTimeout;

    public final int solrCloudConnectRequestTimeout;

    public SolrConnectorConfig(Map<String, ?> props) {
        super(CONFIG_DEF, props);
        solrCloudZkHost = getString(SOLR_CLOUD_ZK_HOST);
        solrCloudUsername = getString(SOLR_CLOUD_USERNAME);
        solrCloudPassword = getString(SOLR_CLOUD_PASSWORD);
        solrCloudCollection = getString(SOLR_CLOUD_COLLECTION);
        solrCloudIndexMode = getString(SOLR_CLOUD_INDEX_MODE);
        solrCloudConnectPoolMaxPerRoute = getInt(SOLR_CLOUD_CONNECT_POOL_MAX_PER_ROUTE);
        solrCloudConnectPoolMaxTotal = getInt(SOLR_CLOUD_CONNECT_POOL_MAX_TOTAL);
        solrCloudSocketTimeout = getInt(SOLR_CLOUD_SOCKET_TIMEOUT);
        solrCloudConnectTimeout = getInt(SOLR_CLOUD_CONNECT_TIMEOUT);
        solrCloudConnectRequestTimeout = getInt(SOLR_CLOUD_CONNECT_REQUEST_TIMEOUT);
    }
}
