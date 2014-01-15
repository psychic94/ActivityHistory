package psy.ActivityHistory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import psy.util.TimeRange;

/**
 * Handles all database manipulation.
 */
public class DatabaseManager{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private SQLSurveyer surveyer;
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
        logger.log(Level.INFO, ActivityHistory.messages.getString("info.dbConnect"));
        surveyer = new SQLSurveyer();createPlayerTable();
        
        DatabaseMetaData meta = con.getMetaData();
        ResultSet tables = meta.getTables(null, null, "Players", null);
        if(!tables.next()) createPlayerTable();
        
        tables = meta.getTables(null, null, "Groups", null);
        if(!tables.next()) createGroupTable();
    }
    
    public void createPlayerTable() throws SQLException{
        Statement stmnt = con.createStatement();
        stmnt.executeUpdate(
            "CREATE TABLE Attendance("
            + "EntryID INT NOT NULL AUTO_INCREMENT, Player VARCHAR(16) NOT NULL, "
            + "Day DATE NOT NULL, Hour0 BINARY(60), Hour1 BINARY(60), "
            + "Hour2 BINARY(60), Hour3 BINARY(60), Hour4 BINARY(60), "
            + "Hour5 BINARY(60), Hour6 BINARY(60), Hour7 BINARY(60), "
            + "Hour8 BINARY(60), Hour9 BINARY(60), Hour10 BINARY(60), "
            + "Hour11 BINARY(60), Hour12 BINARY(60), Hour13 BINARY(60), "
            + "Hour14 BINARY(60), Hour15 BINARY(60), Hour16 BINARY(60), "
            + "Hour17 BINARY(60), Hour18 BINARY(60), Hour19 BINARY(60), "
            + "Hour20 BINARY(60), Hour21 BINARY(60), Hour22 BINARY(60), "
            + "Hour23 BINARY(60), PRIMARY KEY (EntryID))"
        );
    }
    
    public void createGroupTable() throws SQLException{
        Statement stmnt = con.createStatement();
        stmnt.executeUpdate(
            "CREATE TABLE Groups(EntryID int NOT NULL AUTO_INCREMENT, SurveyTime timestamp NOT NULL, "
            + "PRIMARY KEY (EntryID))"
        );
    }
    
    public double tallyActivityPercent(TimeRange range, int hour, String playername){
        try{
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Players WHERE PlayerName = " + playername;
            sql += "ORDER BY SurveyTime";
            ResultSet result = stmt.executeQuery(sql);
            if(range.getStart() == null){
                range.setStart(result.getTimestamp(1));
            }
            Timestamp start = new Timestamp(range.getStart().getTime());
            Timestamp end = new Timestamp(range.getEnd().getTime());
            sql = "SELECT SUM(SurveyInterval) FROM " + result + " WHERE SurveyTime BETWEEN " + start + " AND " + end;
            int time = stmt.executeQuery(sql).getInt(1);
            
            long startLong = new Long(range.getStart().getTime());
            long endLong = new Long(range.getEnd().getTime());
            long timeDiff = endLong - startLong;
            timeDiff /= 1000;
            timeDiff /= 60;
            if(hour != -1)
               timeDiff /= 24;
            time *= 100;
            double percent = ((double)time)/timeDiff;
            percent *= 100;
            percent = Math.round(percent);
            percent /= 100;
            return percent;
        }catch(Exception e){
        }
        return 0;
    }
    
    public SQLSurveyer getSurveyer(){
        return surveyer;
    }
    
    public class SQLSurveyer implements Runnable{
        public void run(){
            HashMap<String, Integer> demogrphx = new HashMap<String, Integer>();
            long time = (new Date()).getTime();
            Player[] players = plugin.getServer().getOnlinePlayers();
            if(plugin.accessConfig().getBoolean("players.enabled")){
                for(Player player : players){
                    try{
                        Statement stmt = con.createStatement();
                        String sql = "INSERT INTO Players (SurveyTime, SurveyInterval, PlayerName) VALUES (";
                        sql += new Timestamp(time) + ", " + plugin.accessConfig().getInt("general.surveyInterval") + ", " + player.getName() + ")";
                        stmt.executeUpdate(sql);
                    }catch(Exception e){
                        logger.log(Level.WARNING, ActivityHistory.messages.getString("errors.dbUpdate"));
                    }
                }
            }
            if(ActivityHistory.vaultEnabled && plugin.accessConfig().getBoolean("groups.enabled")){
                String[] groups = ActivityHistory.perms.getGroups();
                for(String group : groups)
                    demogrphx.put(group, 0);
                //Collect data on the groups
                for(Player player : players){
                    String group = ActivityHistory.perms.getPrimaryGroup(player);
                    demogrphx.put(group, demogrphx.remove(group) + 1);
                }
                //Write the data
                try{
                    groups = ActivityHistory.perms.getGroups();
                    for(String group : groups){
                        Statement stmt = con.createStatement();
                        String sql = "INSERT INTO Groups (SurveyTime, SurveyInterval, GroupName, GroupCount) VALUES (";
                        sql += new Timestamp(time) + ", " + plugin.accessConfig().getInt("general.surveyInterval") + ", ";
                        sql += group + ", " + demogrphx.get(group) + ")";
                        stmt.executeUpdate(sql);
                    }
                }catch(Exception e){
                    logger.log(Level.WARNING, ActivityHistory.messages.getString("errors.dbUpdate"));
                }
            }
        }
    }
}
