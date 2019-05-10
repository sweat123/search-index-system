package com.laomei.sis.es;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * @author laomei on 2019/3/12 19:30
 */
public class EsConnectorConfig extends BaseConnectorConfig {

    public static final  String ES_ADDRESS = "es.address";
    private static final String ES_ADDRESS_DOC = "es address";
    private static final String ES_ADDRESS_DISPLAY = "es address";

    public static final  String ES_USERNAME = "es.username";
    private static final String ES_USERNAME_DOC = "es username";
    private static final String ES_USERNAME_DISPLAY = "es username";

    public static final  String ES_PASSWORD = "es.password";
    private static final String ES_PASSWORD_DOC = "es password";
    private static final String ES_PASSWORD_DISPLAY = "es password";

    public static final  String ES_INDEX = "es.index";
    private static final String ES_INDEX_DOC = "es index";
    private static final String ES_INDEX_DISPLAY = "es index";

    public static final  String ES_MODE = "es.mode";
    private static final String ES_MODE_DOC = "es mode; delete or update (include insert)";
    private static final String ES_MODE_DISPLAY = "es mode; delete or update (include insert)";

    //group
    private static final String ES_GROUP = "ElasticSearch";


    private static final ConfigDef CONFIG_DEF;

    static {
        CONFIG_DEF = configDef()
                .define(ES_ADDRESS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, ES_ADDRESS_DOC,
                        ES_GROUP, 1, ConfigDef.Width.LONG, ES_ADDRESS_DISPLAY)
                .define(ES_USERNAME, ConfigDef.Type.STRING, "",
                        ConfigDef.Importance.LOW, ES_USERNAME_DOC,
                        ES_GROUP, 2, ConfigDef.Width.LONG, ES_USERNAME_DISPLAY)
                .define(ES_PASSWORD, ConfigDef.Type.STRING, "",
                        ConfigDef.Importance.LOW, ES_PASSWORD_DOC,
                        ES_GROUP, 3, ConfigDef.Width.LONG, ES_PASSWORD_DISPLAY)
                .define(ES_INDEX, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                        ConfigDef.Importance.HIGH, ES_INDEX_DOC,
                        ES_GROUP, 4, ConfigDef.Width.LONG, ES_INDEX_DISPLAY)
                .define(ES_MODE, ConfigDef.Type.STRING, "update",
                        ConfigDef.Importance.LOW, ES_MODE_DOC,
                        ES_GROUP, 6, ConfigDef.Width.MEDIUM, ES_MODE_DISPLAY);
    }

    public static ConfigDef getConfigDef() {
        return CONFIG_DEF;
    }

    public final String esAddress;

    public final String esUsername;

    public final String esPassword;

    public final String esIndex;

    public final String esMode;

    public EsConnectorConfig(Map<String, ?> props) {
        super(CONFIG_DEF, props);
        esAddress = getString(ES_ADDRESS);
        esUsername = getString(ES_USERNAME);
        esPassword = getString(ES_PASSWORD);
        esIndex = getString(ES_INDEX);
        esMode = getString(ES_MODE);
    }
}
