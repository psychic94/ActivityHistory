package psy.ActivityHistory.cmd;

import org.bukkit.plugin.Plugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import psy.ActivityHistory.ActivityHistory;

/**
 * Used as a command placeholder for when group queries are disabled.
 */
public class DisabledGQCE extends GroupQueryCommandExecutor{
    public DisabledGQCE(Plugin pl){
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        sender.sendMessage(ActivityHistory.messages.getString("info.playerDisabled"));
        return true;
    }
}
