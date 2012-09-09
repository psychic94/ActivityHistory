package psy.ActivityHistory;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public class DisabledGQCE extends GroupQueryCommandExecutor{
    private ActivityHistory plugin;
    public DisabledGQCE(Plugin pl){
        plugin = (ActivityHistory) pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return true;
    }
}
