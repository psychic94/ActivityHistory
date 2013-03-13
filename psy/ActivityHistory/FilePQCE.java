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
        BufferedReader filebr = null;
        int hour = -1;
        Date start = null;
        Date end = new Date();
        if(args.length < 1){
            sender.sendMessage("no params");
            return false;
        }
        try{
            filebr = loadLogFile(args[0]);
        }catch(FileNotFoundException e){
            sender.sendMessage("Could not find the log file.");
            return true;
        }
        
        //Number parsing
        if(args.length == 1){
            sender.sendMessage("1 param");
        }
        // args: <player> <start>
        else if(args.length == 2){
            sender.sendMessage("2 params");
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
        }
        // args: <player> at <hour>
        else if(args.length == 3 && args[1].equalsIgnoreCase("at")){
            sender.sendMessage("3 params");
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
            sender.sendMessage("4 params");
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
            sender.sendMessage("6 params");
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
        try{
            String timestamp = filebr.readLine();
            if(timestamp.equals(""))
                timestamp = filebr.readLine();
            if(start == null)
                start = new Date(new Long(timestamp));
            while(timestamp != null){
                Date date = new Date(new Long(timestamp));
                if(matchesConditions(date, start, end, hour))
                    times++;
                timestamp = filebr.readLine();
                if(timestamp.equals(""))
                    timestamp = filebr.readLine();
            };
         }catch(IOException e){
            if(player != null)
                player.sendMessage("An error occured while processing the logs.");
            else
                logger.log(Level.WARNING, "An error occured while processing the logs.");
            return true;
        }
        long startLong = new Long(start.getTime());
        long dateLong = new Long((new Date()).getTime());
        long timeDiff = dateLong - startLong;
        timeDiff /= 1000;
        timeDiff /= 60;
        if(hour != -1)
            timeDiff /= 24;
        times *= 1500;
        sender.sendMessage("Activity percentage since " + start +":");
        sender.sendMessage("" + ((double)times)/timeDiff + "%");
        return true;
    }
    
    private BufferedReader loadLogFile(String name)throws FileNotFoundException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + name.toLowerCase() + ".log";
        File file = new File(filename);
        FileReader filer = new FileReader(file);
        return new BufferedReader(filer);
    }
    
    @SuppressWarnings("deprecation")
    private boolean matchesConditions(Date date, Date start, Date end, int hour){
        if(!date.before(end))
            return false;
        if(start != null && !date.after(start))
            return false;
        if(hour != -1 && date.getHours() != hour)
            return false;
        return true;
    }
}
