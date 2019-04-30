package io.slingr.endpoints.sql;

import io.slingr.endpoints.services.exchange.Parameter;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.ws.exchange.FunctionRequest;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Helper utils for SQL endpoint
 * <p/>
 * Created by oazcurra on 01/11/18.
 */
public abstract class SQLEndpointHelper {

    public static String getQueryFromRequest(FunctionRequest request) {
        Json params = request.getJsonParams();
        String queryString = null;
        if (params != null && !params.isEmpty(Parameter.REQUEST_WRAPPED)) {
            queryString = params.string(Parameter.REQUEST_WRAPPED).replaceAll("\\\\", "").replaceAll("\"", "").replaceAll("\'", "");
        }
        return queryString;
    }

    public static Json convertSingleResponseToJson (Object result) {
        return Json.map().set("result", result);
    }

    public static Json convertResultSetToJson(ResultSet rs) throws SQLException {
        Json jsonList = Json.list();
        ResultSetMetaData resultSetMetadata = rs.getMetaData();

        while (rs.next()) {
            int numColumns = resultSetMetadata.getColumnCount();
            Json jsonMap = Json.map();

            for (int i = 1; i < numColumns + 1; i++) {
                String column_name = resultSetMetadata.getColumnName(i);

                if (resultSetMetadata.getColumnType(i) == java.sql.Types.ARRAY) {
                    jsonMap.set(column_name, rs.getArray(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.BIGINT) {
                    jsonMap.set(column_name, rs.getInt(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.BOOLEAN) {
                    jsonMap.set(column_name, rs.getBoolean(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.BLOB) {
                    jsonMap.set(column_name, rs.getBlob(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.DOUBLE) {
                    jsonMap.set(column_name, rs.getDouble(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.FLOAT) {
                    jsonMap.set(column_name, rs.getFloat(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.INTEGER) {
                    jsonMap.set(column_name, rs.getInt(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.NVARCHAR) {
                    jsonMap.set(column_name, rs.getNString(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.VARCHAR) {
                    jsonMap.set(column_name, rs.getString(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.TINYINT) {
                    jsonMap.set(column_name, rs.getInt(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.SMALLINT) {
                    jsonMap.set(column_name, rs.getInt(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.DATE) {
                    jsonMap.set(column_name, rs.getDate(column_name));
                } else if (resultSetMetadata.getColumnType(i) == java.sql.Types.TIMESTAMP) {
                    jsonMap.set(column_name, rs.getTimestamp(column_name));
                } else {
                    jsonMap.set(column_name, rs.getObject(column_name));
                }
            }

            jsonList.push(jsonMap);
        }

        return jsonList;
    }

}