package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Date;
import java.sql.*;
import java.util.Properties;

import org.bukkit.entity.Player;
import psy.util.TimeRange;

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
        surveyer = new SQLSurveyer();
        Statement stmt = con.createStatement();
        String sql = "CREATE TABLE Players(EntryID int NOT NULL AUTO_INCREMENT, SurveyTime timestamp NOT NULL, SurveyInterval int NOT NULL, PlayerName varchar(20) NOT NULL, PRIMARY KEY (EntryID))";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE Groups(EntryID int NOT NULL AUTO_INCREMENT, SurveyTime timestamp NOT NULL, SurveyInterval int NOT NULL, GroupName text NOT NULL, GroupCount int NOT NULL, PRIMARY KEY (EntryID))";
        stmt.executeUpdate(sql);
    }
    
    public double tallyActivityPercent(TimeRange range, int hour, String playername){
        try{
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Players WHERE PlayerName = " + playername;
            sql += "ORDER BY SurveyTime";
            ResultSet result = stmt.executeQuery(sql);
            if(range.getStart() == null){
                ResultSet temp = stmt.executeQuery(sql);
                range.setStart(temp.getTimestamp(1));
            }
            Timestamp start = new Timestamp(range.getStart().getTime());
            Timestamp end = new Timestamp(range.getEnd().getTime());
            sql = "SELECT SUM(SurveyInterval) FROM " + result + " WHERE SurveyTime BETWEEN " + start + " AND " + end;
            int time = stmt.executeQuery(sql).getInt(1);
            
            long startLong = new Long(range.getStart().getTime());
            long dateLong = new Long((new Date()).getTime());
//         long timeDiff = dateLong - startLong;
//         timeDiff /= 1000;
//         timeDiff /= 60;
//         if(hour != -1)
//             timeDiff /= 24;
//         time *= 100;
//         double percent = ((double)time)/timeDiff;
//         percent *= 100;
//         percent = Math.round(percent);
//         percent /= 100;
//         return percent;
        }catch(Exception e){
        }
        return 0;
    }
    
    public SQLSurveyer getSurveyer(){
        return surveyer;
    }
    
    public class SQLSurveyer implements Runnable{
        @SuppressWarnings("unchecked")
        public void run(){
            HashMap<String, Integer> demogrphx = new HashMap();
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
