package co.edu.icesi.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class NotificationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("message", table, columnPrefix + "_message"));
        columns.add(Column.aliased("sent_at", table, columnPrefix + "_sent_at"));
        columns.add(Column.aliased("recipient_id", table, columnPrefix + "_recipient_id"));

        return columns;
    }
}
