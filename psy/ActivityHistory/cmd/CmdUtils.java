package psy.ActivityHistory.cmd;

import java.util.Date;
import psy.util.TimeRange;

import org.bukkit.command.CommandSender;

public class CmdUtils{
    public static TimeRange parseRange(CommandSender sender, String[] args){
        Date start = null;
        Date end = new Date();
        
        // args: <player> <start>
        if(args.length == 2){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return null;
            }
        }
        // args: <player> <start> <end>
        else if(args.length == 3){
            try{
                start = timeStringToDate(args[1]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the start date. Use format MM/DD/YY-hh:mm:ss");
                return null;
            }
            try{
                end = timeStringToDate(args[4]);
            }catch(Exception e){
                sender.sendMessage("Error while parsing the end date. Use format MM/DD/YY-hh:mm:ss");
                return null;
            }
        }
        else{
            return null;
        }
        return new TimeRange(start, end);
    }
    
    public static Integer parseHour(CommandSender sender, String[] args){
        // args: <player> <start> at <hour>
        if(args.length == 4 && args[2].equalsIgnoreCase("at")){
            try{
                return (new Integer(args[3]) - 1);
            }catch(NumberFormatException e){
                sender.sendMessage("Invalid hour. Use an integer between 1 and 24 inclusive.");
                return null;
            }
        }
        // args: <player> <start> <end> at <hour>
        if(args.length == 5 && args[3].equalsIgnoreCase("at")){
            try{
                return (new Integer(args[4]) - 1);
            }catch(NumberFormatException e){
                sender.sendMessage("Invalid hour. Use an integer between 1 and 24 inclusive.");
                return null;
            }
        }
        else{
            return -1;
        }
    }
    
    @SuppressWarnings("deprecation")
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
