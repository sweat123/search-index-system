package com.laomei.sis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.Map;

/**
 * Base on spring parser
 * @author luobo 2018/12/4 16:48
 */
public class PlaceHolderParser {

    private static final Logger log = LoggerFactory.getLogger(PlaceHolderParser.class);

    private static PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("${", "}");

    private PropertyPlaceholderHelper.PlaceholderResolver resolver;

    public static PlaceHolderParser getParser(Map<String, Object> context) {
        return new PlaceHolderParser(context);
    }

    private PlaceHolderParser(Map<String, Object> context) {
        resolver = new SisPlaceHolderResolver(context);
    }

    public String parse(String str) {
        return placeholderHelper.replacePlaceholders(str, resolver);
    }

    static class SisPlaceHolderResolver implements PropertyPlaceholderHelper.PlaceholderResolver {

        private Map<String, Object> ctx;

        SisPlaceHolderResolver(Map<String, Object> ctx) {
            this.ctx = ctx;
        }

        @Override
        public String resolvePlaceholder(final String placeholderName) {
            try {
                String[] placeholderKeys = placeholderName.split("\\.");
                Map<String, Object> obj = ctx;
                try {
                    for (int i = 0; i < placeholderKeys.length - 1; i++) {
                        obj = (Map<String, Object>) obj.get(placeholderKeys[i]);
                    }
                } catch (Exception e) {
                    log.debug("parse place holder failed; placeholder name: {}; context: {}", placeholderName, ctx, e);
                    return null;
                }
                if (null == obj) {
                    return null;
                }
                return String.valueOf(obj.get(placeholderKeys[placeholderKeys.length - 1]));
            } catch (Exception e) {
                log.debug("parse place holder failed; placeholder name: {}; context: {}", placeholderName, ctx, e);
                return null;
            }
        }
    }
}
