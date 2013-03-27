package psy.ActivityHistory;

import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
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
    public FilePQCE(Plugin pl){
        plugin = (ActivityHistory)pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length < 2){
            return false;
        }
        String mode = args[1].toLowerCase();
        PlayerLogFile file = null;
        int hour = -1;
        Date start = null;
        Date end = new Date();
        try{
            file = loadLogFile(args[0]);
        }catch(FileNotFoundException e){
            sender.sendMessage("Could not find the log file.");
            return true;
        }catch(IOException e){
            sender.sendMessage("An error occurred when loading the log file.");
        }
        
        //Number parsing
        if(args.length == 2){
        }
        // args: <player> <prcnt|dist> <start>
        else if(args.length == 3){
            try{
                start = timeStringToDate(args[2]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
        }
        // args: <player> <prcnt> at <hour>
        else if(mode.equals("prcnt") && args.length == 4 && args[2].equalsIgnoreCase("at")){
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
        }else if(args.length == 6){
            try{
                start = timeStringToDate(args[2]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            // args: <player> <prcnt> <start> at <hour>
            if(mode.equals("prcnt") && args[3].equalsIgnoreCase("at")){
                try{
                    hour = new Integer(args[4]);
                    if(hour < 0 || hour > 23){
                        sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                        return true;
                    }
                }catch(Exception e){
                    sender.sendMessage("Invalid hour number. Use an integer for 0 to 23");
                    return true;
                }
            }
            // args: <player> <prcnt|dist> <start> to <end>
            else{
                try{
                    end = timeStringToDate(args[4]);
                }catch(Exception e){
                    sender.sendMessage("Error while parsing the end date. Use format MM/DD/YY-hh:mm:ss");
                    return true;
                }
            }
        }
        // args: <player> <prcnt> <start> to <end> at <hour>
        else if(args.length == 7 && mode.equals("prcnt"){
            try{
                start = timeStringToDate(args[2]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            try{
                end = timeStringToDate(args[4]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the end date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            try{
                hour = new Integer(args[6]);
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
        if(mode.equals("prcnt")
            sender.sendMessage(file.tallyActivityPercent(start, end, hour)+"%");
        else if(mode.equals("dist"){
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
