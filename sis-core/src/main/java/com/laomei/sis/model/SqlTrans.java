package com.laomei.sis.model;

/**
 * @author laomei on 2018/12/4 15:25
 */
public class SqlTrans {

    private String sql;

    private String dsAlias;

    public String getSql() {
        return sql;
    }

    public void setSql(final String sql) {
        this.sql = sql;
    }

    public String getDsAlias() {
        return dsAlias;
    }

    public void setDsAlias(final String dsAlias) {
        this.dsAlias = dsAlias;
    }
}
