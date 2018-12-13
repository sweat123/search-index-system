package com.laomei.sis.model;

/**
 * @author laomei on 2018/12/13 15:34
 */
public class DataSourceConfiguration {

    private String url;

    private String username;

    private String password;

    private String alias;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(final String alias) {
        this.alias = alias;
    }

    @Override
    public String toString() {
        return "{ url: " + url + ", username: " + username + ", password: " + password + ", alias: " + alias + " }";
    }
}
