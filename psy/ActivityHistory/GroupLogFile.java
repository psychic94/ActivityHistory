package psy.ActivityHistory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import psy.util.TimeRange;

public class GroupLogFile{
    private HashMap<Date, Integer> sessions;
    private HashMap<Date, HashMap<String, Integer>> demogrphx;
    private Date firstSession;
    private File file;
    
    public GroupLogFile(String pathname){
        this(new File(pathname));
    }
    
    public GroupLogFile(File file){
        try{
            file.createNewFile();
        }catch(Exception e){
        }
        sessions = new HashMap<Date, Integer>();
        firstSession = null;
        loadSessions();
        firstSession = getFirstSession();
    }
    
    //Returns true if loading successful, false if an error was caught
    private boolean loadSessions(){
        BufferedReader br = reader();
        while(true){
            String line = new String();
            try{
                line = br.readLine();
            }catch(IOException e){
                break;
            }catch(NullPointerException e){
                break;
            }
            if(line==null) break;
            else if (line.trim().equals("")) continue;
            String[] data = line.split(":");
            Date date;
            Integer len;
            try{
                date = new Date(new Long(data[0]));
                len = new Integer(data[1]);
            }catch(NumberFormatException e){
                continue;
            }
            data = data[2].split(",");
            HashMap<String, Integer> temp = new HashMap<String, Integer>();
            for(String datum : data){
                String[] temp2 = datum.trim().split(" ");
                temp.put(temp2[1], new Integer(temp2[0]));
            }
            sessions.put(date, len);
            demogrphx.put(date, temp);
        }
        //Save any changes made when fixing invalid data
        return saveSessions();
    }
    
    //Returns true if saving was successful, false if an error was caught
    private boolean saveSessions(){
        BufferedWriter bw = writer(false);
        for(Date key : sessions.keySet()){
            String line = key.getTime() + ":" + sessions.get(key) + ": ";
            HashMap<String, Integer> temp = demogrphx.get(key);
            for(String key2 : temp.keySet()){
                line += temp.get(key2) + " " + key2 + ", ";
            }
            try{
                bw.write(line);
                bw.newLine();
            }catch(IOException e){
                continue;
            }
        }
        try{
            bw.flush();
        }catch(IOException e){
            return false;
        }catch(NullPointerException e){
            return false;
        }
        try{
            bw.close();
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    //Returns true if addition was successful, false if an error was caught
    public boolean addSession(long time, int len, HashMap<String, Integer> map){
        BufferedWriter bw = writer(true);
        String line = "" + time + ":" + len + ": ";
        for(String key : map.keySet()){
            line += map.get(key) + " " + key + ", ";
        }
        try{
            bw.write(line);
            bw.newLine();
            bw.flush();
        }catch(IOException e){
            return false;
        }
        return true;
    }
    
    public String tallyActivityTotal(TimeRange range){
        if(range.getStart() == null) range.setStart(firstSession);
        int time = 0;
        for(Date date : sessions.keySet()){
            if(range.includes(date) || range.getStart().equals(firstSession))
                time+=sessions.get(date);
        }
        if(time == -1 || range.getStart() == null) return "There is no record of that player.";
        int hours = time / 60, minutes = time % 60;
        return "" + hours + "hours" + minutes + "minutes";
    }
    
    @SuppressWarnings("deprecation")
    public double tallyActivityPercent(TimeRange range, int hour){
        if(range.getStart() == null) range.setStart(firstSession);
        int time = 0;
        for(Date date : sessions.keySet()){
            if((range.includes(date) || range.getStart().equals(firstSession)) && (hour == -1 || date.getHours() == hour))
                time+=sessions.get(date);
        }
        if(time == -1 || range.getStart() == null) return -1;
        long startLong = new Long(range.getStart().getTime());
        long dateLong = new Long((new Date()).getTime());
        long timeDiff = dateLong - startLong;
        timeDiff /= 1000;
        timeDiff /= 60;
        if(hour != -1)
            timeDiff /= 24;
        time *= 100;
        double percent = ((double)time)/timeDiff;
        percent *= 100;
        percent = Math.round(percent);
        percent /= 100;
        return percent;
    }
    
    private BufferedReader reader(){
        try{
            return new BufferedReader(new FileReader(file));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    private BufferedWriter writer(boolean append){
        try{
            return new BufferedWriter(new FileWriter(file, append));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    @Deprecated
    private boolean matchesConditions(Date date, Date start, Date end, int hour){
        if(!date.before(end))
            return false;
        if(start != null && !date.after(start) && !date.equals(start))
            return false;
        if(hour != -1 && date.getHours() != hour)
            return false;
        return true;
    }
    
    private Date getFirstSession(){
        Date first = new Date();
        for(Date date : sessions.keySet())
            if(date.before(first))
                first = date;
        return first;
    }
}