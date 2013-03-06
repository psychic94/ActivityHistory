package psy.ActivityHistory;

import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

public class FileSurveyer implements Runnable{
    ActivityHistory plugin;
    public FileSurveyer(Plugin pl){
        plugin = (ActivityHistory) pl;
    }
	
	@SuppressWarnings("unchecked")
    public void run(){
        HashMap<String, Integer> demogrphx = new HashMap();
        if(ActivityHistory.vaultEnabled && plugin.accessConfig().getBoolean("groups.enabled")){
            String[] groups = ActivityHistory.perms.getGroups();
            for(String group : groups)
                demogrphx.put(group, 0);
        }
        long time = (new Date()).getTime();
        if(plugin.accessConfig().getBoolean("players.enabled") && plugin.accessConfig().getString("players.dataCollectionMethod").equalsIgnoreCase("interval")){
            Player[] players = plugin.getServer().getOnlinePlayers();
            for(Player player : players){
                if(ActivityHistory.vaultEnabled){
                    String group = ActivityHistory.perms.getPrimaryGroup(player);
                    demogrphx.put(group, demogrphx.remove(group) + 1);
                }
                try{
                    String filename = (String) plugin.accessConfig().get("general.logFilesLocation");
                    filename += "/" + player.getName().toLowerCase() + ".log";
                    File log = new File(filename);
                    FileWriter logw = new FileWriter(log, true);
                    BufferedWriter logbw = new BufferedWriter(logw);
                    logbw.write("" + time);
                    logbw.newLine();
                    logbw.flush();
                }catch(Exception e){
                    plugin.logException(e, player.getName());
                }
            }
        }
        if(ActivityHistory.vaultEnabled && plugin.accessConfig().getBoolean("groups.enabled")){
            try{
                String filename = (String) plugin.accessConfig().get("general.logFilesLocation");
                filename += "/groups.log";
                File log = new File(filename);
                FileWriter logw = new FileWriter(log, true);
                BufferedWriter logbw = new BufferedWriter(logw);
                String message = "" + time + ": ";
                String[] groups = ActivityHistory.perms.getGroups();
                for(String group : groups){
                    message +=  demogrphx.get(group) + " ";
                    message += group + ", ";
                }
                logbw.newLine();
                logbw.write(message);
                logbw.flush();
            }catch(Exception e){
                plugin.logException(e, "groups");
            }
        }
    }
}
