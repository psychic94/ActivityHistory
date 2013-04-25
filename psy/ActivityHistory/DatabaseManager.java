package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    public static surveyer;
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
        logger.log(Level.INFO, ActivityHistory.messages.getString("info.dbConnect");
        surveyer = new SQLServeyer();
        Statement stmt = con.createStatement();
        String sql = "CREATE TABLE Players(EntryID int NOT NULL AUTO_INCREMENT, SurveyTime timestamp NOT NULL, SurveyInterval int NOT NULL, PlayerName varchar(20) NOT NULL, PRIMARY KEY (EntryID))";
        stmt.executeUpdate(sql);
        sql = "CREATE TABLE Groups(EntryID int NOT NULL AUTO_INCREMENT, SurveyTime timestamp NOT NULL, SurveyInterval int NOT NULL, GroupName text NOT NULL, GroupCount int NOT NULL, PRIMARY KEY (EntryID))";
        stmt.executeUpdate(sql);
    }
    
    private class SQLServeyer implements Runnable{
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
                        sql +=  new Timestamp(time) + ", " + plugin.accessConfig().getInt("general.surveyInterval") + ", " + player.getName() + ")";
                        stmt.executeUpdate(sql);
                    }catch(Exception e){
                        plugin.logException(e, player.getName());
                    }
                }
            }
        }
    }
}
