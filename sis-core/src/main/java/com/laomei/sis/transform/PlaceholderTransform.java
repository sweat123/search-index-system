package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2018/12/3 14:00
 */
public class PlaceholderTransform implements Transform {

    private final String[]            placeholders;

    private final Map<String, String> fieldAlias;

    public PlaceholderTransform(final String config) {
        this.placeholders = Arrays.stream(config.split(",")).map(String::trim).toArray(String[]::new);
        this.fieldAlias = new HashMap<>(placeholders.length);
        initFieldsAlis();
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        SisRecord record = new SisRecord(sisRecord.getTopic());
        for (Map.Entry<String, String> entry : fieldAlias.entrySet()) {
            String key = entry.getKey();
            String alias = entry.getValue();
            Object value = sisRecord.getValue(key);
            record.addValue(alias, value);
        }
        SisRecord result = new SisRecord(sisRecord.getTopic());
        result.addValue(SIS_TRANSFORMED_RESULT, Collections.singletonList(record.getContext()));
        return result;
    }

    @Override
    public void close() {
    }

    private void initFieldsAlis() {
        /*
            If field in desc has an alias, word 'as' must be in the desc;
         */
        for (String placeholder : placeholders) {
            if (placeholder.contains("as")) {
                String[] words = placeholder.split("as");
                String word1 = words[0].trim();
                String word2 = words[1].trim();
                fieldAlias.put(word1.substring(2, word1.length() - 1), word2);
            } else {
                String word = placeholder.substring(2, placeholder.length() - 1);
                fieldAlias.put(word, word);
            }
        }
    }
}
