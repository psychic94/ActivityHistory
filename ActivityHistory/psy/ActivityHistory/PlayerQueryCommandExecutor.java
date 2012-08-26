package psy.ActivityHistory;

import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public class PlayerQueryCommandExecutor implements CommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    public PlayerQueryCommandExecutor(Plugin pl){
        plugin = (ActivityHistory)pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        try{
            Player player = null;
            if (sender instanceof Player)
                player = (Player) sender;
                
            if(player == null && !player.hasPermission("ah.query.player"))
                return false;
            
            String subCommand = args[1];
                
            if(subCommand.equalsIgnoreCase("online")){}
            
            if(subCommand.equalsIgnoreCase("activitypercent") || subCommand.equalsIgnoreCase("actpcnt") || subCommand.equalsIgnoreCase("ap"))
                return activityPercent(sender, cmd, label, args);
                
            return false;
        }catch(CommandException e){
            sender.sendMessage("Invalid arguements. For help use /ahhelp player.");
            return true;
        }
    }
    
    private boolean activityPercent(CommandSender sender, Command cmd, String label, String[] args){
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
        if(args[2].equalsIgnoreCase("since")){
            Date start;
            try{
                start = timeStringToDate(args[3]);
            }catch(Exception e){
                e.printStackTrace();
                if(player != null)
                    player.sendMessage("An error occured while parsing the date. Use format MM/DD/YY-hh:mm:ss.");
                else
                    logger.log(Level.WARNING, "An error occured while parsing the date. Use format MM.DD.YY-hh:mm:ss.");
                 return true;
            }
            
            int times = 0;
            String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + args[0].toLowerCase() + ".log";
            try{
                File file = new File(filename);
                if(!file.exists()){
                    if(player != null)
                        player.sendMessage("Could not find the file specified.");
                    else
                        logger.log(Level.WARNING, "Could not find the file specified.");
                    return true;
                }
                FileReader filer = new FileReader(file);
                BufferedReader filebr = new BufferedReader(filer);
                String timestamp = filebr.readLine();
                if(timestamp.equals(""))
                    timestamp = filebr.readLine();
                while(timestamp != null){
                    Date date = new Date(new Long(timestamp));
                    if(date.after(start)){
                        times++;
                    }
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
            times *= 1500;
            sender.sendMessage("Activity percentage since " + start +":");
            sender.sendMessage("" + ((double)times)/timeDiff + "%");
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("Deprecated")
    private Date timeStringToDate(String str) throws Exception{
        String[] str2 = str.split("-");
        String[] date = str2[0].split("/");
        String[] time = str2[1].split(":");
        Integer[] ints = {
            new Integer(date[2]) + 100,
            new Integer(date[0]) - 1,
            new Integer(date[1]),
            new Integer(time[0]),
            new Integer(time[1]),
            new Integer(time[2]),
        };
        return new Date(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5]);
    }
}
