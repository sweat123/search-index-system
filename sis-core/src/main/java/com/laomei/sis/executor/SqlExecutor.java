package com.laomei.sis.executor;

import com.laomei.sis.Executor;
import com.laomei.sis.JdbcContext;
import com.laomei.sis.PlaceHolderParser;
import com.laomei.sis.SisRecord;
import com.laomei.sis.exception.ErrorSqlConfigException;
import com.laomei.sis.exception.NullSqlResultException;
import com.laomei.sis.model.ExecutorConfiguration;
import com.laomei.sis.model.ExecutorConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.laomei.sis.JdbcContext.DEFAULT_JDBC_TEMPLATE;

/**
 * @author laomei on 2018/12/4 16:25
 */
public class SqlExecutor implements Executor {

    private static final Logger log = LoggerFactory.getLogger(SqlExecutor.class);

    private final ExecutorConfigurations configurations;

    private final JdbcContext jdbcContext;

    public SqlExecutor(final ExecutorConfigurations configurations, final JdbcContext jdbcContext) {
        this.configurations = configurations;
        this.jdbcContext = jdbcContext;
    }

    @Override
    public List<SisRecord> execute(final List<SisRecord> sisRecords) {
        List<SisRecord> records = new ArrayList<>(sisRecords.size());
        for (SisRecord record : sisRecords) {
            SisRecord sisRecord = execute(record);
            if (sisRecord != null) {
                records.add(sisRecord);
            }
        }
        return records;
    }

    @Override
    public void close() {
    }

    private SisRecord execute(SisRecord sisRecord) {
        for (ExecutorConfiguration configuration : configurations.getExecutorConfigurations()) {
            String sql = configuration.getSql();
            String name = configuration.getName();
            boolean required = configuration.isRequired();
            String alias = configuration.getDsAlias();
            if (StringUtils.isEmpty(alias)) {
                alias = DEFAULT_JDBC_TEMPLATE;
            }
            JdbcTemplate jdbcTemplate = jdbcContext.getJdbcTemplateByAlias(alias);
            try {
                Map<String, Object> sqlResult = executeSql(sisRecord.getContext(), sql, required, jdbcTemplate);
                if (null == sqlResult) {
                    continue;
                }
                sisRecord.addValue(name, sqlResult);
            } catch (ErrorSqlConfigException e) {
                log.error("Throw ErrorSqlConfigException in executeEntitySql(); " +
                        " You need to confirm your config message. " + e .getMessage());
                return null;
            } catch (NullSqlResultException e) {
                log.debug("Throw NullSqlResultException in executeEntitySql(); " +
                        "Result of executing SQL was null and result is not necessary in " +
                        "your config message. " + e.getMessage());
            }
        }
        return sisRecord;
    }

    private Map<String, Object> executeSql(Map<String, Object> context, String sql, boolean required, JdbcTemplate jdbcTemplate)
            throws ErrorSqlConfigException, NullSqlResultException {

        PlaceHolderParser placeHolderParser = PlaceHolderParser.getParser(context);
        String parsedSql = placeHolderParser.parse(sql);
        List<Map<String, Object>> sqlResults = null;
        if (parsedSql != null) {
            try {
                sqlResults = jdbcTemplate.queryForList(parsedSql);
            } catch (EmptyResultDataAccessException e) {
                log.error("JdbcTemplate queryForMap return null; sql: {}", parsedSql, e);
            } catch (Exception e) {
                log.error("execute SQL error; sql: {}; context: {}", parsedSql, context, e);
                sqlResults = null;
            }
        }
        if (sqlResults == null) {
            return null;
        }
        validateResults(parsedSql, required, sqlResults);
        return handleResults(sqlResults);
    }

    private Map<String, Object> handleResults(List<Map<String, Object>> results) {
        if (results.size() < 2) {
            return results.get(0);
        }
        Map<String, Object> multiResult = new HashMap<>();
        for (Map<String, Object> result : results) {
            if (CollectionUtils.isEmpty(result)) {
                continue;
            }
            handleMultiValueResult(multiResult, result);
        }
        return multiResult;
    }

    private void handleMultiValueResult(Map<String, Object> context, Map<String, Object> result) {
        for (Map.Entry<String, Object> entry : result.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            context.computeIfAbsent(key, k -> new ArrayList<>());
            ((List<Object>) context.get(key)).add(value);
        }
    }

    private void validateResults(String sql, boolean required, List<Map<String, Object>> results)
            throws ErrorSqlConfigException, NullSqlResultException {
        if (required && emptyResults(results)) {
            throw new ErrorSqlConfigException("The result of SQL can't be null when required is true in config message! SQL: " + sql);
        }
        if (emptyResults(results)) {
            throw new NullSqlResultException("SQL result is null and required is false; SQL: " + sql);
        }
    }

    private boolean emptyResults(List<Map<String, Object>> results) {
        return CollectionUtils.isEmpty(results) || CollectionUtils.isEmpty(results.get(0));
    }
}
