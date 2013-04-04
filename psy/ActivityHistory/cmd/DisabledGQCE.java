package psy.ActivityHistory.cmd;

import org.bukkit.plugin.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DisabledGQCE extends GroupQueryCommandExecutor{
    public DisabledGQCE(Plugin pl){
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        return true;
    }
}
