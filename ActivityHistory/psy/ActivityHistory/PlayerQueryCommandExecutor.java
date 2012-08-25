package psy.ActivityHistory;

import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PlayerQueryCommandExecutor implements CommandExecutor{
    private ActivityHistory plugin;
    public PlayerQueryCommandExecutor(Plugin pl){
        plugin = (ActivityHistory)pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
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
    }
    
    private boolean activityPercent(CommandSender sender, Command cmd, String label, String[] args){
        if(args[1].equalsIgnoreCase("since")){
            Date start;
            try{
                start = timeStringToDate(args[2]);
            }catch(Exception e){
                sender.sendMessage("An error occured while parsing the date. Use format MM.DD.YY-hh:mm:ss.");
                return true;
            }
            
            int times = 0;
            String filename = plugin.accessConfig().getString("general.logFilesLocation") + args[3].toLowerCase() + ".log";
            try{
                File file = new File(filename);
                FileReader filer = new FileReader(file);
                BufferedReader filebr = new BufferedReader(filer);
                String timestamp;
                do{
                    timestamp = filebr.readLine();
                    Date date = new Date(new Long(timestamp));
                    if(date.after(start))
                        times++;
                }while(timestamp != null);
            }catch(Exception e){
                sender.sendMessage("An error occured while processing the logs.");
                return true;
            }
            long startLong = new Long(start.toString());
            long dateLong = new Long((new Date()).toString());
            long timeDiff = dateLong - startLong;
            timeDiff /= 1000;
            timeDiff /= 3600;
            timeDiff /= 24;
            times *= 15;
            sender.sendMessage("" + times/timeDiff + "%");
            return true;
        }
        return false;
    }
    
    @SuppressWarnings("Deprecated")
    private Date timeStringToDate(String str) throws Exception{
        String[] date = (str.split("-")[0]).split(".");
        String[] time = (str.split("-")[1]).split(":");
        Integer[] ints = {
            new Integer(date[2]),
            new Integer(date[0]),
            new Integer(date[1]),
            new Integer(time[0]),
            new Integer(time[1]),
            new Integer(time[2]),
        };
        return new Date(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5]);
    }
}
