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
    private HashMap<Date, Integer> surveys;
    private HashMap<Date, HashMap<String, Integer>> demogrphx;
    private File file;
    private ActivityHistory plugin;
    private Date firstSurvey;
    
    public GroupLogFile(String pathname, ActivityHistory plugin){
        this(new File(pathname), plugin);
        
    }
    
    public GroupLogFile(File file, ActivityHistory plugin){
        try{
            file.createNewFile();
        }catch(Exception e){
        }
        surveys = new HashMap<Date, Integer>();
        demogrphx = new HashMap<Date, HashMap<String, Integer>>();
        loadSurveys();
        firstSurvey=getFirstSurvey();
        this.plugin = plugin;
    }
    
    //Returns true if loading successful, false if an error was caught
    private boolean loadSurveys(){
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
            surveys.put(date, len);
            demogrphx.put(date, temp);
        }
        //Save any changes made when fixing invalid data
        return saveSurveys();
    }
    
    //Returns true if saving was successful, false if an error was caught
    private boolean saveSurveys(){
        BufferedWriter bw = writer(false);
        for(Date key : surveys.keySet()){
            String line = key.getTime() + ":" + surveys.get(key) + ": ";
            HashMap<String, Integer> temp = demogrphx.get(key);
            for(String key2 : temp.keySet()){
            	if(plugin.accessConfig().getBoolean("groups.cancelLogWhenEmpty") && temp.get(key2)==0)
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
            return false;
        }
        return true;
    }
    
    //Returns true if addition was successful, false if an error was caught
    public boolean addSurvey(long time, int len, HashMap<String, Integer> map){
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
        if(range.getStart() == null) range.setStart(firstSurvey);
        int time = 0;
        for(Date date : surveys.keySet()){
            if(range.includes(date) || range.getStart().equals(firstSurvey))
                time+=surveys.get(date);
        }
        if(time == -1 || range.getStart() == null) return "There is no record of that player.";
        int hours = time / 60, minutes = time % 60;
        return "" + hours + "hours" + minutes + "minutes";
    }
    
    @SuppressWarnings("deprecation")
    public double tallyActivityPercent(TimeRange range, int hour){
        if(range.getStart() == null) range.setStart(firstSurvey);
        int time = 0;
        for(Date date : surveys.keySet()){
            if((range.includes(date) || range.getStart().equals(firstSurvey)) && (hour == -1 || date.getHours() == hour))
                time+=surveys.get(date);
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
    
    private Date getFirstSurvey(){
        Date first = new Date();
        for(Date date : surveys.keySet())
            if(date.before(first))
                first = date;
        return first;
    }
}
