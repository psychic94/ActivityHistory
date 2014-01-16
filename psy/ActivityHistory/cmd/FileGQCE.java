package psy.ActivityHistory.cmd;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import psy.ActivityHistory.ActivityHistory;
import psy.ActivityHistory.GroupLogFile;
import psy.util.TimeRange;

public class FileGQCE extends GroupQueryCommandExecutor{
    private ActivityHistory plugin;
    public FileGQCE(Plugin pl){
        plugin = (ActivityHistory) pl;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(args.length < 1){
            return false;
        }
        
        GroupLogFile file = null;
        try{
            file = loadLogFile();
        }catch(FileNotFoundException e){
            sender.sendMessage(ActivityHistory.messages.getString("errors.fileNotFound"));
            return true;
        }catch(IOException e){
            sender.sendMessage(ActivityHistory.messages.getString("errors.fileLoad"));
            return true;
        }
            
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
            
        String mode = cmd.getName();
        TimeRange range = null;
        if(mode.equalsIgnoreCase("gpercent")){
            range = CmdUtils.parseRange(sender, args, 1);
        }else if(mode.equalsIgnoreCase("staffdist")){
            range = CmdUtils.parseRange(sender, args, 0);
        }
        if(range==null) return true;
        if(mode.equalsIgnoreCase("gpercent")){
            Integer hour = CmdUtils.parseHour(sender, args, 1);
            if(hour==null) return true;
            double percent = file.tallyActivityPercent(range, hour);
            if(percent<=0) sender.sendMessage(ActivityHistory.messages.getString("errors.playerNotFound"));
            else sender.sendMessage("" + percent + "%");
        }else if(mode.equalsIgnoreCase("staffdist")){
        }
        return true;
    }

    private GroupLogFile loadLogFile()throws IOException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/groups.log";
        GroupLogFile file = new GroupLogFile(filename);
        return file;
    }
}
