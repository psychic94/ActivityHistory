package psy.ActivityHistory;

import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public class FilePQCE extends PlayerQueryCommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    public FilePQCE(Plugin pl){
        plugin = (ActivityHistory)pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        PlayerLogFile file = null;
        int hour = -1;
        Date start = null;
        Date end = new Date();
        if(args.length < 1){
            sender.sendMessage("no params");
            return false;
        }
        try{
            file = loadLogFile(args[0]);
        }catch(FileNotFoundException e){
            sender.sendMessage("Could not find the log file.");
            return true;
        }catch(IOException e){
            sender.sendMessage("An error occurred when loading the log file.");
        }
        
        //Number parsing
        if(args.length == 1){
        }
        // args: <player> <start>
        else if(args.length == 2){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
        }
        // args: <player> at <hour>
        else if(args.length == 3 && args[1].equalsIgnoreCase("at")){
            try{
                hour = new Integer(args[2]);
                if(hour < 0 || hour > 23){
                    sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                    return true;
                }
            }catch(Exception e){
                sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                return true;
            }
        }else if(args.length == 4){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            // args: <player> <start> at <hour>
            if(args[2].equalsIgnoreCase("at")){
                try{
                    hour = new Integer(args[3]);
                    if(hour < 0 || hour > 23){
                        sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                        return true;
                    }
                }catch(Exception e){
                    sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                    return true;
                }
            }
            // args: <player> <start> to <end>
            else{
                try{
                    end = timeStringToDate(args[3]);
                }catch(Exception e){
                    sender.sendMessage("Error while parsing the end date. Use format MM/DD/YY-hh:mm:ss");
                    return true;
                }
            }
        }
        // args: <player> <start> to <end> at <hour>
        else if(args.length == 6){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            try{
                end = timeStringToDate(args[3]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the end date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            try{
                hour = new Integer(args[5]);
                if(hour < 0 || hour > 23){
                    sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                    return true;
                }
            }catch(Exception e){
                sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                return true;
            }
        }
        // no params
        else{
            return false;
        }
            
        int times = 0;
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
        sender.sendMessage(file.tallyActivityPercent(start, end, hour));
        return true;
    }
    
    private PlayerLogFile loadLogFile(String name)throws FileNotFoundException, IOException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + name.toLowerCase() + ".log";
        PlayerLogFile file = new PlayerLogFile(filename);
        return file;
    }
}
