package co.edu.icesi.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ParkingSpotSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("number", table, columnPrefix + "_number"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));
        columns.add(Column.aliased("spot_type", table, columnPrefix + "_spot_type"));
        columns.add(Column.aliased("spot_vehicle", table, columnPrefix + "_spot_vehicle"));

        columns.add(Column.aliased("parking_lot_id_id", table, columnPrefix + "_parking_lot_id_id"));
        columns.add(Column.aliased("parking_lot_id_id", table, columnPrefix + "_parking_lot_id_id"));
        return columns;
    }
}
