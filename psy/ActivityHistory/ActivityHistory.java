package psy.ActivityHistory;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Date;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import psy.ActivityHistory.cmd.*;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.permission.Permission;

public class ActivityHistory extends JavaPlugin{
    private static final Logger logger = Logger.getLogger("Minecraft");
    public static Permission perms = null;
    public FileConfiguration config;
    public static boolean vaultEnabled;
    private String debugMode;
    PlayerQueryCommandExecutor playerExec;
    GroupQueryCommandExecutor groupExec;
    public static DatabaseManager dbm;
    public static YamlConfiguration messages;
    
    @Override
    public void onEnable(){
        saveDefaultConfig();
        config = this.getConfig();
        vaultEnabled = (this.getServer().getPluginManager().getPlugin("Vault")) != null;
        debugMode = (String) config.getString("general.debugMode");
        
        //Load the localization of messages
        InputStream stream = getResource(config.getString("general.language").toLowerCase() + ".yml");
        if (stream != null)
            messages = YamlConfiguration.loadConfiguration(stream);
        
        String logpath = config.getString("logFilesLocation");
        if(logpath==null) logpath="plugins/ActivityHistory/logs";
        File logfolder = new File(logpath);
        if(!logfolder.exists()) logfolder.mkdir();
        
        if(config.getBoolean("players.enabled")){
            if(config.getString("general.storageType").equalsIgnoreCase("file"))
                playerExec = new FilePQCE(this);
            else if(config.getString("general.storageType").equalsIgnoreCase("sql"))
                try{    
                    dbm = new DatabaseManager(this);
                    playerExec = new DatabasePQCE(this);
                }catch(SQLException e){
                    logger.log(Level.SEVERE, messages.getString("errors.dbConnect"));
                    dbm = null;
                    playerExec = new DisabledPQCE(this);
                }
        }else
            playerExec = new DisabledPQCE(this);
            
        if(vaultEnabled)
            setupPermissions();
        if(config.getBoolean("groups.enabled") || config.getBoolean("players.enabled"))
            scheduleSurvey();
            
        if(vaultEnabled && config.getBoolean("groups.enabled"))
            groupExec = new FileGQCE(this);
        else
            groupExec = new DisabledGQCE(this);
        
        getCommand("ppercent").setExecutor(playerExec);
        getCommand("ptotal").setExecutor(playerExec);
        getCommand("phours").setExecutor(playerExec);
        
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
        }
    }
    
    public FileConfiguration accessConfig(){
        return config;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
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
        if(config.getString("general.storageType").equalsIgnoreCase("file")){
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new FileSurveyer(this), offset, (interval*60*20));
        }else if(config.getString("general.storageType").equalsIgnoreCase("sql") && dbm != null){
            this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, dbm.getSurveyer(), offset, (interval*60*20));
        }
    }
}

