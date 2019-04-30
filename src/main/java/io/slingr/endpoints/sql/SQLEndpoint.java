package io.slingr.endpoints.sql;

import com.zaxxer.hikari.HikariDataSource;
import io.slingr.endpoints.Endpoint;
import io.slingr.endpoints.framework.annotations.ApplicationLogger;
import io.slingr.endpoints.framework.annotations.EndpointFunction;
import io.slingr.endpoints.framework.annotations.EndpointProperty;
import io.slingr.endpoints.framework.annotations.SlingrEndpoint;
import io.slingr.endpoints.services.AppLogs;
import io.slingr.endpoints.utils.Json;
import io.slingr.endpoints.ws.exchange.FunctionRequest;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>SQL endpoint
 * <p/>
 * Created by oazcurra on 31/10/18.
 */
@SlingrEndpoint(name = "sql")
public class SQLEndpoint extends Endpoint {

    private static final Logger logger = LoggerFactory.getLogger(SQLEndpoint.class);

    @ApplicationLogger
    private AppLogs appLogger;

    @EndpointProperty
    private String dbUser;

    @EndpointProperty
    private String dbPassword;

    @EndpointProperty
    private String connectionString;

    @EndpointProperty
    private String dbType;

    @EndpointProperty
    private String maximumPoolSize;

    private HikariDataSource dataSource;


    @Override
    public void endpointStarted() {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(connectionString);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPassword);
        dataSource.setMaximumPoolSize(Integer.valueOf(maximumPoolSize));
        logger.debug(String.format("Data source with pool name [%s] has been initialized.", dataSource.getPoolName()));
    }

    @Override
    public void endpointStopped(String cause) {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.debug(String.format("Closing Data source with pool name [%s].", dataSource.getPoolName()));
            dataSource.close();
        }
    }


    @EndpointFunction(name = "query")
    public Json runQuery(FunctionRequest functionRequest) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            String queryString = SQLEndpointHelper.getQueryFromRequest(functionRequest);
            if (StringUtils.isNotEmpty(queryString)) {
                try {
                    conn = dataSource.getConnection();
                } catch (SQLException sqle) {
                    appLogger.error(String.format("Error trying to get connection instance using connector for [%s]", dbType), sqle);
                    return SQLEndpointHelper.convertSingleResponseToJson(sqle.getMessage());
                }
                try {
                    Statement statement = CCJSqlParserUtil.parse(queryString);
                    preparedStatement = conn.prepareStatement(queryString);
                    if (statement instanceof Insert || statement instanceof Update || statement instanceof Delete) {
                        // insert, update or delete case
                        int response = preparedStatement.executeUpdate();
                        return SQLEndpointHelper.convertSingleResponseToJson(response);
                    } else {
                        // select case
                        rs = preparedStatement.executeQuery();
                        return SQLEndpointHelper.convertResultSetToJson(rs);
                    }
                } catch (SQLException e) {
                    appLogger.error(String.format("There was an error trying to run query [%s] and using connector for [%s]", queryString, dbType), e);
                    return SQLEndpointHelper.convertSingleResponseToJson(e.getMessage());
                } catch (JSQLParserException e) {
                    appLogger.error(String.format("There was an error trying to parse query [%s] and using connector for [%s]", queryString, dbType), e);
                    return SQLEndpointHelper.convertSingleResponseToJson(e.getMessage());
                }
            } else {
                appLogger.error("Cannot run empty query");
                return SQLEndpointHelper.convertSingleResponseToJson("Cannot run empty query");
            }
        } catch (Exception ex) {
            appLogger.error(String.format("Error trying to run query for [%s]", dbType), ex);
            return SQLEndpointHelper.convertSingleResponseToJson(ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("There was an error trying to close result set", e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    logger.error("There was an error trying to close connection", ex);
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error("There was an error trying to close prepared statement", e);
                }
            }
        }
    }
}