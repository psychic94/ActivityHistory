package psy.ActivityHistory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        surveyer = new SQLSurveyer();
        
        DatabaseMetaData meta = con.getMetaData();
        ResultSet tables = meta.getTables(null, null, "Players", null);
        if(!tables.first()) createPlayerTable();
        
        tables = meta.getTables(null, null, "Groups", null);
        if(!tables.first()) createGroupTable();
    }
    
    public void createPlayerTable() throws SQLException{
        Statement stmnt = con.createStatement();
        stmnt.executeUpdate(
            "CREATE TABLE Players("
            + "EntryID INT NOT NULL AUTO_INCREMENT, PlayerName VARCHAR(16) NOT NULL, "
            + "Start DATE NOT NULL, End DATE NOT NULL, PRIMARY KEY (EntryID))"
        );
    }
    
    public void createGroupTable() throws SQLException{
        Statement stmnt = con.createStatement();
        stmnt.executeUpdate(
            "CREATE TABLE Groups(EntryID int NOT NULL AUTO_INCREMENT, SurveyTime timestamp NOT NULL, "
        	+ "Varchar(25) GroupName NOT NULL, int GroupCount NOT NULL,"
            + "PRIMARY KEY (EntryID))"
        );
    }
    
    public String tallyActivityTotal(TimeRange range, int hour, String playername){
        try{
        	//Get data
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Players WHERE PlayerName = " + playername;
            sql += "AS Sessions ORDER BY Start";
            ResultSet result = stmt.executeQuery(sql);
            //Set the range start, if necessary
            if(range.getStart() == null){
            	result.first();
                range.setStart(result.getDate(3));
            }
            
            //Sum the overlaps of the sessions and the search TimeRange
            long ontime = 0;
            result.beforeFirst();
            while(result.next()){
            	TimeRange temp = new TimeRange(result.getDate(3), result.getDate(4));
            	ontime += range.overlap(temp);
            }
            
            //Calculate activity percent to two decimal places
            return ontime + " minutes";
        }catch(Exception e){
            if(plugin.accessConfig().getString("general.logMode").equalsIgnoreCase("basic"))
            	logger.log(Level.WARNING, e.getMessage());
            else if(plugin.accessConfig().getString("general.logMode").equalsIgnoreCase("advanced"))
                e.printStackTrace();
        }
        return "0 minutes";
    }
    
    public double tallyActivityPercent(TimeRange range, int hour, String playername){
        try{
        	//Get data
            Statement stmt = con.createStatement();
            String sql = "SELECT * FROM Players WHERE PlayerName = " + playername;
            sql += "AS Sessions ORDER BY Start";
            ResultSet result = stmt.executeQuery(sql);
            //Set the range start, if necessary
            if(range.getStart() == null){
            	result.first();
                range.setStart(result.getDate(3));
            }
            
            //Sum the overlaps of the sessions and the search TimeRange
            long ontime = 0;
            result.beforeFirst();
            while(result.next()){
            	TimeRange temp = new TimeRange(result.getDate(3), result.getDate(4));
            	ontime += range.overlap(temp);
            }
            
            //Calculate activity percent to two decimal places
            double percent = ontime/range.length();
            percent *= 100;
            percent = Math.round(percent);
            percent /= 100;
            if(hour!=-1){
            	//Compensate for only 1 hour a day being searched
            	percent *= 24;
            }
            return percent;
        }catch(Exception e){
        	if(plugin.accessConfig().getString("general.logMode").equalsIgnoreCase("basic"))
            	logger.log(Level.WARNING, e.getMessage());
        	else if(plugin.accessConfig().getString("general.logMode").equalsIgnoreCase("advanced"))
                e.printStackTrace();
        }
        return 0;
    }
    
    public void addPlayerSession(String playername, TimeRange session) throws SQLException{
    	addPlayerSession(playername, session.getStart(), session.getEnd());
    }
    
    public void addPlayerSession(String playername, Date start, Date end) throws SQLException{
        Statement stmt = con.createStatement();
        String sql = "INSERT INTO Players (PlayerName, Start, End) VALUES (";
        sql += playername + "," + start + ", " + end + ")";
        stmt.executeUpdate(sql);
    }
    
    public SQLSurveyer getSurveyer(){
        return surveyer;
    }
    
    public class SQLSurveyer implements Runnable{
        public void run(){
            HashMap<String, Integer> demogrphx = new HashMap<String, Integer>();
            long start = (new Date()).getTime();
            long end = start + plugin.accessConfig().getInt("general.surveyInterval")*60000;
            Player[] players = plugin.getServer().getOnlinePlayers();
            if(plugin.accessConfig().getBoolean("players.enabled")){
                for(Player player : players){
                    try{
                        addPlayerSession(player.getName(), new Date(start), new Date(end));
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
                        sql += new Date(start) + ", " + plugin.accessConfig().getInt("general.surveyInterval") + ", ";
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
