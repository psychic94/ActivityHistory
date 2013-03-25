package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    Connection con;
    public DatabaseManager(ActivityHistory ah) throws SQLException{
        plugin = ah;
        Properties conProps = new Properties();
        conProps.put("user", plugin.accessConfig().get("SQL.username"));
        conProps.put("password", plugin.accessConfig().get("SQL.password"));
        con = DriverManager.getConnection(
            "jdbc:mysql://" + plugin.accessConfig().getString("SQL.ip") + ":" +
            plugin.accessConfig().getString("SQL.port") + "/", conProps
        );
        logger.log(Level.INFO, "Successfully connected to database");
    }
}
