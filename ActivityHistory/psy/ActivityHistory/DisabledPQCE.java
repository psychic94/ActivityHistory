package psy.ActivityHistory;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public class DisabledPQCE extends PlayerQueryCommandExecutor{
    private ActivityHistory plugin;
    public DisabledPQCE(Plugin pl){
        plugin = (ActivityHistory) pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return true;
    }
}
