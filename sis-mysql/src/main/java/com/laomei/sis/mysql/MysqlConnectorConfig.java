package com.laomei.sis.mysql;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * @author laomei on 2019/3/8 11:51
 */
public class MysqlConnectorConfig extends BaseConnectorConfig {

    public static final String SINK_MYSQL_URL = "sink.mysql.url";
    private static final String SINK_MYSQL_URL_DOC = "sink mysql url";
    private static final String SINK_MYSQL_URL_DISPLAY = "sink mysql url";

    public static final String SINK_MYSQL_USERNAME = "sink.mysql.username";
    private static final String SINK_MYSQL_USERNAME_DOC = "sink mysql username";
    private static final String SINK_MYSQL_USERNAME_DISPLAY = "sink mysql username";

    public static final String SINK_MYSQL_PASSWORD = "sink.mysql.password";
    private static final String SINK_MYSQL_PASSWORD_DOC = "sink mysql password";
    private static final String SINK_MYSQL_PASSWORD_DISPLAY = "sink mysql password";

    public static final String SINK_MYSQL_TABLE = "sink.mysql.table";
    private static final String SINK_MYSQL_TABLE_DOC = "the table which sis will upsert or delete data";
    private static final String SINK_MYSQL_TABLE_DISPLAY = "the table which sis will upsert or delete data";

    public static final  String SINK_MYSQL_MODE = "sink.mysql.mode";
    private static final String SINK_MYSQL_MODE_DOC = "mysql mode; delete or update (include insert)";
    private static final String SINK_MYSQL_MODE_DISPLAY = "mysql mode; delete or update (include insert)";

    private static final String MYSQL_GROUP = "Mysql";

    static {
        CONFIG_DEF
                .define(SINK_MYSQL_URL, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SINK_MYSQL_URL_DOC,
                        MYSQL_GROUP, 1, ConfigDef.Width.LONG, SINK_MYSQL_URL_DISPLAY)
                .define(SINK_MYSQL_USERNAME, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SINK_MYSQL_USERNAME_DOC,
                        MYSQL_GROUP, 2, ConfigDef.Width.LONG, SINK_MYSQL_USERNAME_DISPLAY)
                .define(SINK_MYSQL_PASSWORD, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SINK_MYSQL_PASSWORD_DOC,
                        MYSQL_GROUP, 3, ConfigDef.Width.LONG, SINK_MYSQL_PASSWORD_DISPLAY)
                .define(SINK_MYSQL_TABLE, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SINK_MYSQL_TABLE_DOC,
                        MYSQL_GROUP, 4, ConfigDef.Width.LONG, SINK_MYSQL_TABLE_DISPLAY)
                .define(SINK_MYSQL_MODE, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, SINK_MYSQL_MODE_DOC,
                        MYSQL_GROUP, 5, ConfigDef.Width.LONG, SINK_MYSQL_MODE_DISPLAY);
    }

    public static ConfigDef getConfigDef() {
        return CONFIG_DEF;
    }

    public final String sinkMysqlUrl;

    public final String sinkMysqlUsername;

    public final String sinkMysqlPassword;

    public final String sinkMysqlTable;

    public final String sinkMysqlMode;

    public MysqlConnectorConfig(final Map<String, ?> props) {
        super(CONFIG_DEF, props);
        sinkMysqlUrl = getString(SINK_MYSQL_URL);
        sinkMysqlUsername = getString(SINK_MYSQL_USERNAME);
        sinkMysqlPassword = getString(SINK_MYSQL_PASSWORD);
        sinkMysqlTable = getString(SINK_MYSQL_TABLE);
        sinkMysqlMode = getString(SINK_MYSQL_MODE);
    }
}
