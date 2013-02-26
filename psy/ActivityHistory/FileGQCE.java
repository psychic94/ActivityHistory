package psy.ActivityHistory;

import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandException;

public class FileGQCE extends GroupQueryCommandExecutor{
    private ActivityHistory plugin;
    private static final Logger logger = Logger.getLogger("Minecraft");
    public FileGQCE(Plugin pl){
        plugin = (ActivityHistory) pl;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        BufferedReader filebr = null;
        int hour = -1;
        Date start = null;
        Date end = new Date();
        if(args.length < 1 || !args[0].equalsIgnoreCase("staffdist"))
            return false;
        try{
            filebr = loadLogFile("groups");
        }catch(FileNotFoundException e){
            sender.sendMessage("Could not find the log file.");
            return true;
        }
        
        // args: staffdist <start>
        if(args.length == 2){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
        }
        // args: staffdist <start> <end>
        else if(args.length == 3){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
            try{
                end = timeStringToDate(args[2]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the end date. Use format MM/DD/YY-hh:mm:ss");
                return true;
            }
        }else{
            return false;
        }
        
        TimeRange range = new TimeRange(start, end);
        double[] pcnt = new double[24];
        int[] denom = new int[24];
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
            
        try{
            String line = filebr.readLine();
            while(line != null){
                String[] info = line.split(":");
                Date date = new Date(new Long(info[0]));
                if(range.includes(date)){
                    String[] groups = info[1].split(",");
                    int total = 0;
                    int staff = 0;
                    for(String group : groups){
                        if(!group.equals(" ") && !group.equals("")){
                            group = group.trim();
                            int space = group.indexOf(" ");
                            try{
                                int number = new Integer(group.substring(0, space));
                                group = group.substring(space+1);
                                total += number;
                                if(Arrays.asList(plugin.config.getStringList("groups.staffGroups")).contains(group))
                                    staff += number;
                            }catch(Exception e){
                            }
                        }
                    }
                    denom[date.getHours()]++;
                    pcnt[date.getHours()] += (((double)staff)/total);
                }
                line = filebr.readLine();
            }
         }catch(IOException e){
            if(player != null)
                player.sendMessage("An error occured while processing the logs.");
            else
                logger.log(Level.WARNING, "An error occured while processing the logs.");
            return true;
        }
        for(int i=0; i<24; i++){
            pcnt[i] = pcnt[i]/denom[i];
        }
        String[] graph = new String[20];
        for(int i=19; i>=0; i--){
            String line = "";
            for(int j=0; j<24; j++){
                if(pcnt[j]>=(i*5))
                    line += "#";
                else
                    line += "-";
            }
            graph[i] = line;
        }
        for(String line : graph)
            sender.sendMessage(line);
        hour = 0;
        double num = pcnt[0];
        for(int i=1; i<24; i++){
            if(num < pcnt[i]){
                hour = i;
                num = pcnt[i];
            }
        }
        sender.sendMessage("Staff peak is " + num + "% at " + hour + ".");
        return true;
    }
    
    private BufferedReader loadLogFile(String name)throws FileNotFoundException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + name.toLowerCase() + ".log";
        File file = new File(filename);
        FileReader filer = new FileReader(file);
        return new BufferedReader(filer);
    }
    
    @SuppressWarnings("Deprecated")
    private Date timeStringToDate(String str) throws Exception{
        String[] str2 = str.split("-");
        String[] date = str2[0].split("/");
        String[] time = str2[1].split(":");
        Integer[] ints = {
            new Integer(date[2]) + 100,
            new Integer(date[0]) - 1,
            new Integer(date[1]),
            new Integer(time[0]),
            new Integer(time[1]),
            new Integer(time[2]),
        };
        return new Date(ints[0], ints[1], ints[2], ints[3], ints[4], ints[5]);
    }
}
