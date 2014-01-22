package psy.ActivityHistory.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import psy.ActivityHistory.ActivityHistory;
import psy.util.TimeRange;

public class DatabasePQCE extends PlayerQueryCommandExecutor{
    public DatabasePQCE(Plugin pl){
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length < 1){
            return false;
        }
            
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
        
        //Analyze command
        String mode = cmd.getName();
        TimeRange range = CmdUtils.parseRange(sender, args, 1);
        if(range==null) return true;
        
        //Player activity percent
        if(mode.equalsIgnoreCase("ppercent")){
            Integer hour = CmdUtils.parseHour(sender, args, 1);
            if(hour==null) return true;
            double percent = ActivityHistory.dbm.tallyActivityPercent(range, hour, args[0]);
            if(percent<=0) sender.sendMessage(ActivityHistory.messages.getString("errors.playerNotFound"));
            else sender.sendMessage("" + percent + "%");
        }
        //Player total ontime
        else if(mode.equalsIgnoreCase("ptotal")){
            sender.sendMessage(ActivityHistory.dbm.tallyActivityTotal(range, -1, args[0]));
        }
        //Player activity percent by hour
        else if(mode.equalsIgnoreCase("phours")){
            double[] data = new double[24];
            String[] messages = {"", "", "", ""};
            for(int i=0; i<24; i++){
                //data[i] = file.tallyActivityPercent(range, i);
                messages[i/6] += "" + i + ":00- " + data[i] + "%   ";
            }
            for(String message : messages)
                sender.sendMessage(message);
        }
        return true;
    }
}
