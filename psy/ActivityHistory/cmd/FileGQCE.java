package psy.ActivityHistory.cmd;

import java.util.Date;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import psy.ActivityHistory.ActivityHistory;
import psy.ActivityHistory.GroupLogFile;
import psy.util.TimeRange;

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
            sender.sendMessage("Could not find the log file.");
            return true;
        }catch(IOException e){
            sender.sendMessage("An error occurred when loading the log file.");
            return true;
        }
            
        Player player = null;
        if(sender instanceof Player)
            player = (Player) sender;
            
        String mode = cmd.getName();
        TimeRange range = CmdUtils.parseRange(sender, args);
        if(range==null) return true;
        if(mode.equalsIgnoreCase("ppercent")){
            Integer hour = CmdUtils.parseHour(sender, args);
            if(hour==null) return true;
            double percent = file.tallyActivityPercent(range, hour);
            if(percent<=0) sender.sendMessage("There is no record of that player.");
            else sender.sendMessage("" + percent + "%");
        }else if(mode.equalsIgnoreCase("ptotal")){
            sender.sendMessage(file.tallyActivityTotal(range));
        }else if(mode.equalsIgnoreCase("phours")){
            double[] data = new double[24];
            String[] messages = {"", "", "", ""};
            for(int i=0; i<24; i++){
                data[i] = file.tallyActivityPercent(range, i);
                messages[i/6] += "" + i + ":00- " + data[i] + "%   ";
            }
            for(String message : messages)
                sender.sendMessage(message);
        }
        return true;
    }

//     @SuppressWarnings({"deprecation", "unchecked"})
//     private boolean staffDist(CommandSender sender, Command cmd, String label, String[] args){
//         GroupLogFile file = loadLogFile();
//         int hour = -1;
//         Date start = null;
//         Date end = new Date();
//         
//         //Parse args
// 
//         TimeRange range = new TimeRange(start, end);
//         double[] pcnt = new double[24];
//         int[] denom = new int[24];
//         Player player = null;
//         if(sender instanceof Player)
//             player = (Player) sender;
// 
//         try{
//             String line = filebr.readLine();
//             while(line != null){
//                 String[] info = line.split(":");
//                 Date date = new Date(new Long(info[0]));
//                 if(range.includes(date)){
//                     String[] groups = info[1].split(",");
//                     int total = 0;
//                     int staff = 0;
//                     for(String group : groups){
//                         if(!group.equals(" ") && !group.equals("")){
//                             group = group.trim();
//                             int space = group.indexOf(" ");
//                             try{
//                                 int number = new Integer(group.substring(0, space));
//                                 group = group.substring(space+1);
//                                 total += number;
//                                 if(Arrays.asList(plugin.config.getStringList("groups.staffGroups")).contains(group))
//                                     staff += number;
//                             }catch(Exception e){
//                             }
//                         }
//                     }
//                     denom[date.getHours()]++;
//                     pcnt[date.getHours()] += (((double)staff)/total);
//                 }
//                 line = filebr.readLine();
//             }
//          }catch(IOException e){
//             if(player != null)
//                 player.sendMessage("An error occured while processing the logs.");
//             else
//                 logger.log(Level.WARNING, "An error occured while processing the logs.");
//             return true;
//         }
//         for(int i=0; i<24; i++){
//             pcnt[i] = pcnt[i]/denom[i];
//         }
//         String[] graph = new String[20];
//         for(int i=19; i>=0; i--){
//             String line = "";
//             for(int j=0; j<24; j++){
//                 if(pcnt[j]>=(i*5))
//                     line += "#";
//                 else
//                     line += "-";
//             }
//             graph[i] = line;
//         }
//         for(String line : graph)
//             sender.sendMessage(line);
//         hour = 0;
//         double num = pcnt[0];
//         for(int i=1; i<24; i++){
//             if(num < pcnt[i]){
//                 hour = i;
//                 num = pcnt[i];
//             }
//         }
//         sender.sendMessage("Staff peak is " + num + "% at " + hour + ".");
//         return true;
//     }

    private GroupLogFile loadLogFile()throws IOException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/groups.log";
        GroupLogFile file = new GroupLogFile(filename);
        return file;
    }
}
