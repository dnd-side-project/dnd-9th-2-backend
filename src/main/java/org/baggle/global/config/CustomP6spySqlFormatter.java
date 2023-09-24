package org.baggle.global.config;

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class CustomP6spySqlFormatter implements MessageFormattingStrategy {
    @Override
    public String formatMessage(int connectionId, String now, long elapsed, String category, String prepared, String sql, String url) {
        sql = formatSql(category, sql);
        return String.format("[SQL] %s | %s | %d ms | %s ", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()), category, elapsed, formatSql(category, sql));
    }

    private String formatSql(String category, String sql) {
        if (sql != null && !sql.trim().isEmpty() && Category.STATEMENT.getName().equals(category)) {
            String trimmedSql = sql.trim().toLowerCase(Locale.ROOT);
            if (trimmedSql.startsWith("create") || trimmedSql.startsWith("alter") || trimmedSql.startsWith("comment")) {
                sql = FormatStyle.DDL.getFormatter().format(sql);
            }
            sql = FormatStyle.BASIC.getFormatter().format(sql);
            return sql;
        }
        return sql;
    }
}
