package psy.ActivityHistory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Surveys players and records data to files.
 */
public class FileSurveyer implements Runnable{
    ActivityHistory plugin;
    private Logger logger = Logger.getLogger("Minecraft");
    String debugMode;
    
    public FileSurveyer(Plugin pl){
        plugin = (ActivityHistory) pl;
        debugMode = plugin.accessConfig().getString("general.debugMode");
    }
    
    public void run(){
        HashMap<String, Integer> demogrphx = new HashMap<String, Integer>();
        long time = (new Date()).getTime();
        Player[] players = plugin.getServer().getOnlinePlayers();
        if(plugin.accessConfig().getBoolean("players.enabled")){
            for(Player player : players){
                try{
                    String filename = (String) plugin.accessConfig().get("general.logFilesLocation");
                    filename += "/" + player.getName().toLowerCase() + ".log";
                    PlayerLogFile log = new PlayerLogFile(filename);
                    log.addSession(time, plugin.accessConfig().getInt("general.surveyInterval"));
                }catch(Exception e){
                	if(debugMode.equalsIgnoreCase("basic")){
                		String message = ActivityHistory.messages.getString("errors.playerUpdate");
                		message = message.replaceAll("%p", player.getName());
                    	logger.log(Level.WARNING, message);
                	}else if(debugMode.equalsIgnoreCase("advanced"))
                        e.printStackTrace();
                }
            }
        }
        if(ActivityHistory.vaultEnabled && plugin.accessConfig().getBoolean("groups.enabled")){
            String[] groups = ActivityHistory.perms.getGroups();
            for(String group : groups)
                demogrphx.put(group.toLowerCase(), 0);
            //Collect data on the groups
            for(Player player : players){
                String group = ActivityHistory.perms.getPrimaryGroup(player).toLowerCase();
                Integer oldCount = demogrphx.remove(group);
                if(oldCount==null){
                	if(debugMode.equalsIgnoreCase("basic") || debugMode.equalsIgnoreCase("advanced")){
                		String message = ActivityHistory.messages.getString("errors.playerUpdate");
                		message = message.replaceAll("%p", player.getName());
                		message = message.replaceAll("%g", group);
                		logger.log(Level.WARNING, message);
                	}
                	continue;
                }
                demogrphx.put(group, oldCount + 1);
            }
            //Write the data
            try{
                String filename = (String) plugin.accessConfig().get("general.logFilesLocation");
                filename += "/groups.log";
                File log = new File(filename);
                FileWriter logw = new FileWriter(log, true);
                BufferedWriter logbw = new BufferedWriter(logw);
                String message = "" + time + ": ";
                groups = ActivityHistory.perms.getGroups();
                for(String group : groups){
                    message +=  demogrphx.get(group) + " ";
                    message += group + ", ";
                }
                logbw.newLine();
                logbw.write(message);
                logbw.flush();
                logbw.close();
            }catch(Exception e){
            }
        }
    }
}
