package psy.ActivityHistory.cmd;

import java.util.Date;
import psy.util.TimeRange;
import psy.ActivityHistory.ActivityHistory;

import org.bukkit.command.CommandSender;

public class CmdUtils{
    /**Iterprets the arguments from the command to determine the range to search
     * @param offset The number of arguments to skip
    */
    public static TimeRange parseRange(CommandSender sender, String[] args, int offset){
        Date start = null;
        Date end = new Date();
        
        if(args.length <= (0+offset)){
        }
        // args: <start>
        else if(args.length == (1+offset)){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage(ActivityHistory.messages.getString("errors.firstDate"));
                return null;
            }
        }
        // args: <start> <end>
        else if(args.length == (2+offset)){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage(ActivityHistory.messages.getString("errors.firstDate"));
                return null;
            }
            try{
                end = timeStringToDate(args[4]);
            }catch(Exception e){
                sender.sendMessage(ActivityHistory.messages.getString("errors.secondDate"));
                return null;
            }
        }
        else{
            return null;
        }
        return new TimeRange(start, end);
    }
    
    
    /**Iterprets the arguments from the command to determine the time of day to search
     * @param offset The number of arguments to skip
    */
    public static Integer parseHour(CommandSender sender, String[] args, int offset){
        // args: <player> <start> at <hour>
        if(args.length == 4 && args[2].equalsIgnoreCase("at")){
            try{
                return (new Integer(args[3]) - 1);
            }catch(NumberFormatException e){
                sender.sendMessage(ActivityHistory.messages.getString("errors.invalidHour"));
                return null;
            }
        }
        // args: <player> <start> <end> at <hour>
        if(args.length == 5 && args[3].equalsIgnoreCase("at")){
            try{
                return (new Integer(args[4]) - 1);
            }catch(NumberFormatException e){
                sender.sendMessage(ActivityHistory.messages.getString("errors.invalidHour"));
                return null;
            }
        }
        else{
            return -1;
        }
    }
    
    @SuppressWarnings("deprecation")
    /**Translates the format used in the command arguements into java.util.Date
     * @param str The string to be translated
    */
    protected static Date timeStringToDate(String str){
        Date now = new Date();
        Integer day = 1, month = now.getMonth(), year = now.getYear();
        Integer hour = now.getHours(), minute = 0, second = 0;
        String[] str2 = str.split("-");
        if(!str2[0].trim().equals("")){
            String[] date = str2[0].split("/");
            if(date.length >= 1) month = new Integer(date[0]) - 1;
            if(date.length >= 2) day = new Integer(date[1]);
            if(date.length >= 3) year = new Integer(date[2]) + 100;
        }
        if(!str2[1].trim().equals("")){
            String[] time = str2[1].split(":");
            if(time.length >= 1) hour = new Integer(time[0]);
            if(time.length >= 2) minute = new Integer(time[1]);
            if(time.length >= 3) second = new Integer(time[2]);
        }
        return new Date(year, month, day, hour, minute, second);
    }
}
