package psy.ActivityHistory.cmd;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import psy.ActivityHistory.ActivityHistory;
import psy.ActivityHistory.PlayerLogFile;
import psy.util.TimeRange;

public class FilePQCE extends PlayerQueryCommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    
    public FilePQCE(Plugin pl){
        plugin = (ActivityHistory)pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length < 1){
            return false;
        }
        
        PlayerLogFile file = null;
        String debugMode = plugin.accessConfig().getString("general.debugMode");
        		
        //Load log file		
        try{
            file = loadLogFile(args[0]);
        }catch(FileNotFoundException e){
            sender.sendMessage(ActivityHistory.messages.getString("errors.fileNotFound"));
            if(debugMode.equalsIgnoreCase("basic"))
            	logger.log(Level.WARNING, e.getMessage());
            else if(debugMode.equalsIgnoreCase("advanced"))
                e.printStackTrace();
            return true;
        }catch(IOException e){
            sender.sendMessage(ActivityHistory.messages.getString("errors.fileLoad"));
            if(debugMode.equalsIgnoreCase("basic"))
            	logger.log(Level.WARNING, e.getMessage());
            else if(debugMode.equalsIgnoreCase("advanced"))
                e.printStackTrace();
            return true;
        }
        
        //Analyze command     
        String mode = cmd.getName();
        TimeRange range = CmdUtils.parseRange(sender, args, 1);
        if(debugMode.equalsIgnoreCase("advanced"))
            sender.sendMessage("Range: " + range);
        if(range==null) return true;
        
        //Player activity percent
        if(mode.equalsIgnoreCase("ppercent")){
            Integer hour = CmdUtils.parseHour(sender, args, 1);
            if(hour==null) return true;
            double percent = file.tallyActivityPercent(range, hour);
            if(percent<=0) sender.sendMessage(ActivityHistory.messages.getString("errors.playerNotFound"));
            else sender.sendMessage("" + percent + "%");
        }
        //Player total ontime
        else if(mode.equalsIgnoreCase("ptotal")){
            sender.sendMessage(file.tallyActivityTotal(range));
        }
        //Player activity percent by hour
        else if(mode.equalsIgnoreCase("phours")){
            double[] data = new double[24];
            String[] messages = {"", "", "", ""};
            for(int i=0; i<24; i++){
                data[i] = file.tallyActivityPercent(range, i);
                messages[i/6] += "" + i + ":00- " + data[i] + "%   ";
            }
            for(String message : messages)
                sender.sendMessage(message);
        }
        return true;
    }
    
    private PlayerLogFile loadLogFile(String name)throws IOException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + name.toLowerCase() + ".log";
        PlayerLogFile file = new PlayerLogFile(filename);
        return file;
    }
}
