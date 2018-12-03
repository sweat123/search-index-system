package com.laomei.sis.model;

import java.util.List;
import java.util.Map;

/**
 * @author laomei on 2018/12/3 19:17
 */
public class Filter {

    private List<String> exist;

    private List<String> notExist;

    private Map<String, String> match;

    private Map<String, String> notMatch;

    private Map<String, Map<String, String>> range;

    private Map<String, List<String>> in;

    private Map<String, List<String>> notIn;

    public List<String> getExist() {
        return exist;
    }

    public void setExist(final List<String> exist) {
        this.exist = exist;
    }

    public List<String> getNotExist() {
        return notExist;
    }

    public void setNotExist(final List<String> notExist) {
        this.notExist = notExist;
    }

    public Map<String, String> getMatch() {
        return match;
    }

    public void setMatch(final Map<String, String> match) {
        this.match = match;
    }

    public Map<String, String> getNotMatch() {
        return notMatch;
    }

    public void setNotMatch(final Map<String, String> notMatch) {
        this.notMatch = notMatch;
    }

    public Map<String, Map<String, String>> getRange() {
        return range;
    }

    public void setRange(final Map<String, Map<String, String>> range) {
        this.range = range;
    }

    public Map<String, List<String>> getIn() {
        return in;
    }

    public void setIn(final Map<String, List<String>> in) {
        this.in = in;
    }

    public Map<String, List<String>> getNotIn() {
        return notIn;
    }

    public void setNotIn(final Map<String, List<String>> notIn) {
        this.notIn = notIn;
    }
}
