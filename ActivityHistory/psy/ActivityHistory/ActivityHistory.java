package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
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
    public FileConfiguration config;
    public static boolean vaultEnabled;
    private String debugMode;
    PlayerQueryCommandExecutor ahplayerExec;
    GroupQueryCommandExecutor ahgroupExec;
    
    @Override
    public void onEnable(){
        config = this.getConfig();
        vaultEnabled = (this.getServer().getPluginManager().getPlugin("Vault")) != null;
        debugMode = (String) config.getString("general.debugMode");
        
        if(config.getString("players.dataCollectionMethod").equalsIgnoreCase("interval"))
            ahplayerExec = new IntervalFilePQCE(this);
        else if(config.getString("players.dataCollectionMethod").equalsIgnoreCase("continual"))
            ahplayerExec = new ContinualFilePQCE(this);
        else
            ahplayerExec = new DisabledPQCE(this);
            
        if(vaultEnabled)
            setupPermissions();
        if(config.getBoolean("groups.enabled") || (config.getBoolean("players.enabled") && config.getString("players.dataCollectionMethod").equalsIgnoreCase("interval")))
            scheduleSurvey();
            
        if(vaultEnabled && config.getBoolean("groups.enabled"))
            ahgroupExec = new FileGQCE(this);
        else
            ahgroupExec = new DisabledGQCE(this);
        
        getCommand("ahplayer").setExecutor(ahplayerExec);
        getCommand("ahgroup").setExecutor(ahgroupExec);
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
        if(config.getString("general.storageType").equals("file")){
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new FileSurveyer(this), offset, (15*60*20));
        }
    }
}

