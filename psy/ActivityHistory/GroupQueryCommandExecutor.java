package psy.ActivityHistory;

import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public abstract class GroupQueryCommandExecutor implements CommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
    
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
