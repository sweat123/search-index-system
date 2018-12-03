package com.laomei.sis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author laomei on 2018/12/3 15:55
 */
public class IoUtil {

    public static Properties loadProperties(String location, ClassLoader classLoader) {
        try(InputStream stream = classLoader.getResourceAsStream(location)) {
            Properties props = new Properties();
            props.load(stream);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to find or read the " + location + " file");
        }
    }
}
