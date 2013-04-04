package psy.ActivityHistory.cmd;

import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import psy.ActivityHistory.ActivityHistory;

public abstract class PlayerQueryCommandExecutor implements CommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
    
    @SuppressWarnings("deprecation")
    protected Date timeStringToDate(String str){
        Date now = new Date();
        Integer day = 1, month = now.getMonth(), year = now.getYear();
        Integer hour = now.getHours(), minute = 0, second = 0;
        String[] str2 = str.split("-");
        if(!str2[0].trim().equals("")){
            String[] date = str2[0].split("/");
            if(date.length >= 1) month = new Integer(date[0]) - 1;
            if(date.length >= 2) day = new Integer(date[1]);
            if(date.length >= 3) year = new Integer(date[2]) + 100;
        }
        if(!str2[1].trim().equals("")){
            String[] time = str2[1].split(":");
            if(time.length >= 1) hour = new Integer(time[0]);
            if(time.length >= 2) minute = new Integer(time[1]);
            if(time.length >= 3) second = new Integer(time[2]);
        }
        return new Date(year, month, day, hour, minute, second);
    }
}
