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
        long time = (new Date()).getTime();
        Player[] players = plugin.getServer().getOnlinePlayers();
        if(plugin.accessConfig().getBoolean("players.enabled")){
            for(Player player : players){
                try{
                    String filename = (String) plugin.accessConfig().get("general.logFilesLocation");
                    filename += "/" + player.getName().toLowerCase() + ".log";
                    PlayerLogFile log = new PlayerLogFile(filename);
                    log.addSession(time, plugin.accessConfig().getInt("player.surveyInterval"));
                }catch(Exception e){
                    plugin.logException(e, player.getName());
                }
            }
        }
        if(ActivityHistory.vaultEnabled && plugin.accessConfig().getBoolean("groups.enabled")){
            String[] groups = ActivityHistory.perms.getGroups();
            for(String group : groups)
                demogrphx.put(group, 0);
            for(Player player : players){
                String group = ActivityHistory.perms.getPrimaryGroup(player);
                demogrphx.put(group, demogrphx.remove(group) + 1);
            }
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
            }catch(Exception e){
                plugin.logException(e, "groups");
            }
        }
    }
}
