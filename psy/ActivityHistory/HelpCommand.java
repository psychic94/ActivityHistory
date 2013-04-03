package psy.ActivityHistory;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public abstract class GroupQueryCommandExecutor implements CommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length == 0 || args[0].toLowerCase().contains("player"){
            sender.sendMessage("/ahplayer <name> prcnt - displays the percentage of the time the player was online since first logon");
            sender.sendMessage("/ahplayer <name> prcnt <start> - displays the percentage of the time the player was online since <start>");
            sender.sendMessage("/ahplayer <name> prcnt <start> to <end> - displays the percentage of the time the player was online between <start> and <end>");
            sender.sendMessage("/ahplayer <name> prcnt past <length> - displays the percentage of the time the player was online in the past <length>");
            sender.sendMessage("Any of the above commands can have \"at <hour>\" at the end to calculate the percentage of the time the player was on at <hour>");
            sender.sendMessage("Replace \"prcnt\" with \"dist\" to show a graph of the online time by hour.");
            sender.sendMessage("Replace \"prcnt\" with \"total\" to show total online time instead of a percentage.");
        }
        if(args.length > 0 && !args[0].trim().equalsIgnoreCase("formats")
            sender.sendMessage("\"<start>\" and \"<end>\" have the format \"DD/MM/YY-hh:mm:ss\". \"<length>\" has the format \"#M#D\". Use \"/ahhelp formats\" for details.");
}
