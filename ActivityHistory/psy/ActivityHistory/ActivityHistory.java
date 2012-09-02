package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class ActivityHistory extends JavaPlugin{
    private static final Logger logger = Logger.getLogger("Minecraft");
    public static Permission perms = null;
    private FileConfiguration config;
    public static boolean vaultEnabled;
    private String debugMode;
    PlayerQueryCommandExecutor ahplayerExec;
    
    @Override
    public void onEnable(){
        config = this.getConfig();
        vaultEnabled = (this.getServer().getPluginManager().getPlugin("Vault")) != null;
        debugMode = (String) config.getString("general.debugMode");
        if(config.getString("players.dataCollectionMethod").equalsIgnoreCase("inverval"))
            ahplayerExec = new IntervalFilePQCE(this);
        else if(config.getString("players.dataCollectionMethod").equalsIgnoreCase("continual"))
            ahplayerExec = new ContinualFilePQCE(this);
        if(vaultEnabled)
            setupPermissions();
        if(config.getBoolean("groups.enabled") || (config.getBoolean("players.enabled") && config.getString("players.dataCollectionMethod").equalsIgnoreCase("interval")))
            scheduleSurvey();
        getCommand("ahplayer").setExecutor(ahplayerExec);
    }
    
    public FileConfiguration accessConfig(){
        return config;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    public void logException(Exception e, String logFile){
        if(debugMode.equalsIgnoreCase("basic"))
            logger.log(Level.WARNING, "Error while updating log file for " + logFile + ".");
        else if(debugMode.equalsIgnoreCase("advanced"))
            e.printStackTrace();
    }
    
    private void scheduleSurvey(){
        Date time = new Date();
        int minute = time.getMinutes();
        int offset = 0;
        do{
            minute++;
            offset++;
        }while(minute%15 != 0);
        offset *= 60;
        offset -= time.getSeconds();
        offset *= 20;
        this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable(){
            public void run(){
                survey();
            }
        }, offset, (15*60*20));
    }
    
    public void survey(){
        HashMap<String, Integer> demogrphx = new HashMap();
        if(vaultEnabled && config.getBoolean("groups.enabled")){
            String[] groups = perms.getGroups();
            for(String group : groups)
                demogrphx.put(group, 0);
        }
        long time = (new Date()).getTime();
        if(config.getBoolean("players.enabled") && config.getString("players.dataCollectionMethod").equalsIgnoreCase("interval")){
            Player[] players = getServer().getOnlinePlayers();
            for(Player player : players){
                if(vaultEnabled){
                    String group = perms.getPrimaryGroup(player);
                    demogrphx.put(group, demogrphx.remove(group) + 1);
                }
                try{
                    String filename = (String) config.get("general.logFilesLocation");
                    filename += "/" + player.getName().toLowerCase() + ".log";
                    File log = new File(filename);
                    FileWriter logw = new FileWriter(log, true);
                    BufferedWriter logbw = new BufferedWriter(logw);
                    logbw.write("" + time);
                    logbw.newLine();
                    logbw.flush();
                }catch(Exception e){
                    logException(e, player.getName());
                }
            }
        }
        if(vaultEnabled && config.getBoolean("groups.enabled")){
            try{
                String filename = (String) config.get("general.logFilesLocation");
                filename += "/groups.log";
                File log = new File(filename);
                FileWriter logw = new FileWriter(log, true);
                BufferedWriter logbw = new BufferedWriter(logw);
                String message = "" + time + ": ";
                String[] groups = perms.getGroups();
                for(String group : groups){
                    message +=  demogrphx.get(group) + " ";
                    message += group + ", ";
                }
                logbw.newLine();
                logbw.write(message);
                logbw.flush();
            }catch(Exception e){
                logException(e, "groups");
            }
        }
    }
}

