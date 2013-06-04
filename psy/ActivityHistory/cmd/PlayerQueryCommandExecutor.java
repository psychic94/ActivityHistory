package psy.ActivityHistory.cmd;

import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import psy.ActivityHistory.ActivityHistory;

/**
 * Used to create polymorphism.
 */
public abstract class PlayerQueryCommandExecutor implements CommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    
    public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);
}
