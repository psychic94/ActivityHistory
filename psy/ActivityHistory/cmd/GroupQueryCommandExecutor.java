package psy.ActivityHistory.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Used to create polymorphism.
 */
public abstract class GroupQueryCommandExecutor implements CommandExecutor{    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
}
