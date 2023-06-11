package co.edu.icesi.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ReservationSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("parking_spot_id", table, columnPrefix + "_parking_spot_id"));
        columns.add(Column.aliased("start_time", table, columnPrefix + "_start_time"));
        columns.add(Column.aliased("end_time", table, columnPrefix + "_end_time"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("reservation_code", table, columnPrefix + "_reservation_code"));

        columns.add(Column.aliased("customer_id_id", table, columnPrefix + "_customer_id_id"));
        columns.add(Column.aliased("customer_id_id", table, columnPrefix + "_customer_id_id"));
        columns.add(Column.aliased("notifications_id", table, columnPrefix + "_notifications_id"));
        return columns;
    }
}
