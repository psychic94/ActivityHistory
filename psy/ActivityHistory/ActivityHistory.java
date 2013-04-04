package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import psy.ActivityHistory.cmd.*;

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
    public static DatabaseManager dbm;
    
    @Override
    public void onEnable(){
        saveDefaultConfig();
        config = this.getConfig();
        vaultEnabled = (this.getServer().getPluginManager().getPlugin("Vault")) != null;
        debugMode = (String) config.getString("general.debugMode");
        
        String logpath = config.getString("logFilesLocation");
        if(logpath==null) logpath="plugins/ActivityHistory/logs";
        File logfolder = new File(logpath);
        if(!logfolder.exists()) logfolder.mkdir();
        
        if(config.getBoolean("players.enabled")){
            if(config.getString("general.storageType").equalsIgnoreCase("file"))
                ahplayerExec = new FilePQCE(this);
            else if(config.getString("general.storageType").equalsIgnoreCase("sql"))
                try{    
                    dbm = new DatabaseManager(this);
                }catch(SQLException e){
                    logger.log(Level.SEVERE, "Could not connect to database");
                }
        }else
            ahplayerExec = new DisabledPQCE(this);
            
        if(vaultEnabled)
            setupPermissions();
        if(config.getBoolean("groups.enabled") || config.getBoolean("players.enabled"))
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
    
    @SuppressWarnings("deprecation")
    private void scheduleSurvey(){
        Date time = new Date();
        int minute = time.getMinutes();
        int offset = 0;
        int interval = accessConfig().getInt("general.surveyInterval");
        //Set initial delay to the amount of time until the time is divisible by interval
        do{
            minute++;
            offset++;
        }while(minute%interval != 0);
        offset *= 60;
        offset -= time.getSeconds();
        offset *= 20;
        if(config.getString("general.storageType").equals("file")){
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new FileSurveyer(this), offset, (interval*60*20));
        }
    }
}

