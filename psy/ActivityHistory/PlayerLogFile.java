package psy.ActivityHistory;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Date;

import psy.util.TimeRange;

public class PlayerLogFile extends File{
    HashMap<Date, Integer> sessions;
    BufferedReader br;
    //This writer appends
    BufferedWriter bw1;
    //This writer overwrites
    BufferedWriter bw2;
    
    @SuppressWarnings("unchecked")
    public PlayerLogFile(String pathname) throws FileNotFoundException, IOException{
        super(pathname);
        sessions = new HashMap();
        loadSessions();
        br = new BufferedReader(new FileReader(this));
        bw1 = new BufferedWriter(new FileWriter(this, true));
        bw2 = new BufferedWriter(new FileWriter(this, false));
    }
    
    private void loadSessions(){
        while(true){
            String line;
            try{
                line = br.readLine();
            }catch(IOException e){
                break;
            }catch(NullPointerException e){
                break;
            }
            if(line==null) break;
            else if (line.trim().equals("")) continue;
            String[] data = line.split(",");
            Date date;
            Integer len;
            try{
                date = new Date(new Long(data[0]));
                if(data.length<2 || data[1].trim().equals("")) len = 15;
                else len = new Integer(data[1]);
            }catch(NumberFormatException e){
                continue;
            }
            sessions.put(date, len);
        }
        //Save any invalid data that was detected and fixed when loading
        saveSessions();
    }
    
    private void saveSessions(){
        for(Date key : sessions.keySet()){
            try{
                bw2.write(key.getTime() + "," + sessions.get(key));
                bw2.newLine();
            }catch(IOException e){
                continue;
            }
        }
        try{
            bw2.flush();
        }catch(IOException e){
        }catch(NullPointerException e){
        }
        
    }
    
    public String tallyActivityPercent(Date start, Date end, int hour){
        if(start==null) start = (Date) sessions.keySet().toArray()[0];
        int time = 0;
        for(Date date : sessions.keySet()){
            if(matchesConditions(date, start, end, hour))
                time+=sessions.get(date);
        }
        long startLong = new Long(start.getTime());
        long dateLong = new Long((new Date()).getTime());
        long timeDiff = dateLong - startLong;
        timeDiff /= 1000;
        timeDiff /= 60;
        if(hour != -1)
            timeDiff /= 24;
        return "" + ((double)time)/timeDiff + "%";
    }
    
    @SuppressWarnings("deprecation")
    private boolean matchesConditions(Date date, Date start, Date end, int hour){
        if(!date.before(end))
            return false;
        if(start != null && !date.after(start))
            return false;
        if(hour != -1 && date.getHours() != hour)
            return false;
        return true;
    }
}
