package psy.ActivityHistory.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Handles commands to manage data stored in the database.
 */
public class SQLManagementCE implements CommandExecutor{
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return true;
    }
}
