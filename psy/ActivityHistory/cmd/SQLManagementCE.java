package psy.ActivityHistory.cmd;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import net.minecraft.util.org.apache.commons.io.FilenameUtils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import psy.ActivityHistory.ActivityHistory;
import psy.ActivityHistory.PlayerLogFile;
import psy.util.TimeRange;

/**
 * Handles commands to manage data stored in the database.
 */
public class SQLManagementCE implements CommandExecutor{
	private ActivityHistory plugin;
	
	public SQLManagementCE(ActivityHistory plugin){
		this.plugin = plugin;
	}
	
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        String mode = cmd.getName();
        if(mode.equalsIgnoreCase("ahrestore")){
            File dir = new File(plugin.accessConfig().getString("general.logFilesLocation"));
            for(File file : dir.listFiles()){
                //If file is not a log file, skip it
                if(!file.isFile() || !file.getPath().endsWith(".log"))
                    continue;
                if(file.getPath().endsWith("groups.log")){
                	
                }else{
                	PlayerLogFile pfile;
                	try{
                		pfile = new PlayerLogFile(file);
                	}catch(IOException e){
                		continue;
                	}
                	TimeRange range = CmdUtils.parseRange(sender, args, 0);
                	ArrayList<TimeRange> sessions = pfile.getSessions(range);
                	String playername = FilenameUtils.removeExtension(file.getName());
                	for(TimeRange session : sessions){
                		try{
                			ActivityHistory.dbm.addPlayerSession(playername, session);
                		}catch(SQLException e){
                			sender.sendMessage(ActivityHistory.messages.getString("errors.dbUpdate"));
                		}
                	}
                }
            }
        }
        return true;
    }
}
