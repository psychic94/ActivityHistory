package psy.ActivityHistory.cmd;

import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import psy.ActivityHistory.ActivityHistory;
import psy.ActivityHistory.PlayerLogFile;

public class FilePQCE extends PlayerQueryCommandExecutor{
    private ActivityHistory plugin;
    public FilePQCE(Plugin pl){
        plugin = (ActivityHistory)pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length < 1){
            return false;
        }
        
        String mode;
        
        PlayerLogFile file = null;
        
        try{
            file = loadLogFile(args[0]);
        }catch(FileNotFoundException e){
            sender.sendMessage("Could not find the log file.");
            return true;
        }catch(IOException e){
            sender.sendMessage("An error occurred when loading the log file.");
            return true;
        }
            
        int times = 0;
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
        if(mode.equals("prcnt"))
            sender.sendMessage(file.tallyActivityPercent(start, end, hour));
        else if(mode.equals("dist")){
            String[] data = new String[24];
            for(int i=0; i<24; i++){
                data[i] = file.tallyActivityPercent(start, end, i);
            }
        }
        return true;
    }
    
    private PlayerLogFile loadLogFile(String name)throws FileNotFoundException, IOException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + name.toLowerCase() + ".log";
        PlayerLogFile file = new PlayerLogFile(filename);
        return file;
    }
}
