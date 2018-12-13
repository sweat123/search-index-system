package com.laomei.sis.transform;

import com.laomei.sis.JdbcContext;
import com.laomei.sis.PlaceHolderParser;
import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import com.laomei.sis.model.SqlTrans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

import static com.laomei.sis.JdbcContext.DEFAULT_JDBC_TEMPLATE;

/**
 * @author laomei on 2018/12/4 15:20
 */
public class SqlTransform implements Transform {

    private static final Logger log = LoggerFactory.getLogger(SqlTransform.class);

    private final String        sql;

    private final JdbcTemplate  jdbcTemplate;

    public SqlTransform(final SqlTrans sqlTrans, final JdbcContext jdbcContext) {
        this.sql = sqlTrans.getSql();
        String alias = sqlTrans.getDsAlias();
        if (StringUtils.isEmpty(alias)) {
            alias = DEFAULT_JDBC_TEMPLATE;
        }
        this.jdbcTemplate = jdbcContext.getJdbcTemplateByAlias(alias);
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        PlaceHolderParser parser = PlaceHolderParser.getParser(sisRecord.getContext());
        String parsedSql = parser.parse(sql);
        if (StringUtils.isEmpty(parsedSql)) {
            return null;
        }
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql);
            if (CollectionUtils.isEmpty(results)) {
                return null;
            }
            SisRecord record = new SisRecord(sisRecord.getTopic());
            record.addValue(SIS_TRANSFORMED_RESULT, results);
            return record;
        } catch (EmptyResultDataAccessException e) {
            log.warn("JdbcTemplate queryForMap return null;", e);
            return null;
        }
    }

    @Override
    public void close() {
    }
}
