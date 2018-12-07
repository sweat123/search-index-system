package com.laomei.sis.exception;

/**
 * @author laomei on 2018/12/7 16:55
 */
public class JdbcContextException extends Exception {

    public JdbcContextException(final String message) {
        super(message);
    }

    public JdbcContextException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
