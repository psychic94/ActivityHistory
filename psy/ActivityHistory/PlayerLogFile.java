package psy.ActivityHistory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import psy.util.TimeRange;

public class PlayerLogFile{
    private HashMap<Date, Integer> sessions;
    private Date firstSession;
    private File file;
    
    /**
     * Creates and wraps a new {@link File} from a pathname.
     * @param pathname The path of the file to be created
     */
    public PlayerLogFile(String pathname) throws FileNotFoundException, IOException{
        this(new File(pathname));
    }
    
    /**
     * Wraps a pre-existing {@link File}.
     * @param file The file to be wrapped
     */
    public PlayerLogFile(File initFile) throws FileNotFoundException, IOException{
        file = initFile;
        file.createNewFile();
        sessions = new HashMap<Date, Integer>();
        firstSession = null;
        loadSessions();
        firstSession = getFirstSession();
    }
    
    //Returns true if loading successful, false if an error was caught
    private void loadSessions() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(file));
        while(true){
            String line = new String();
                line = br.readLine();
            if(line==null) break;
            else if (line.trim().equals("")) continue;
            String[] data = line.split(":");
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
        br.close();
        //Save any changes made when fixing invalid data
        saveSessions();
    }
    
    private void saveSessions() throws IOException{
        BufferedWriter bw = writer(false);
        for(Date key : sessions.keySet()){
            bw.write(key.getTime() + ":" + sessions.get(key));
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }
    
    /**Adds a session to the file
     * @param time The time of the survey
     * @param len The survey interval or session length
     */
    public void addSession(long time, int len) throws IOException{
        BufferedWriter bw = writer(true);
        bw.write("" + time + ":" + len);
        bw.newLine();
        bw.flush();
    }
    
    /**Removes sessions from within a TimeRange
     * @param range Delete sessions from within this range
     */
    public void removeSessions(TimeRange range) throws IOException{
        for(Date date : sessions.keySet())
            if(range.includes(date))
                sessions.remove(date);
        saveSessions();
    }
    
    public ArrayList<TimeRange> getSessions(TimeRange range){
    	ArrayList<TimeRange> matches = new ArrayList<TimeRange>();
    	for(Date date : sessions.keySet()){
    		if(range.includes(date)){
    			matches.add(new TimeRange(date, sessions.get(date)));
    		}
    	}
    	return matches;
    }
    
    public String tallyActivityTotal(TimeRange range){
        if(range.getStart() == null) range.setStart(firstSession);
        int time = 0;
        for(Date date : sessions.keySet()){
            if(range.includes(date))
                time+=sessions.get(date);
        }
        if(time == -1 || range.getStart() == null) return ActivityHistory.messages.getString("errors.playerNotFound");
        int hours = time / 60, minutes = time % 60;
        return "" + hours + "hours" + minutes + "minutes";
    }
    
    @SuppressWarnings("deprecation")
    public double tallyActivityPercent(TimeRange range, int hour){
        if(range.getStart() == null) range.setStart(firstSession);
        //If the above line didnt work, set the start to when this plugin was first published
        if(range.getStart() == null) range.setStart(new Date(112, 8, 17));
        int time = 0;
        for(Date date : sessions.keySet()){
            if(range.includes(date) && (hour == -1 || date.getHours() == hour))
                time+=sessions.get(date);
        }
        if(time == 0) return -1;
        
        //Calculate activity percent to two decimal places
        double percent;
        if(hour == -1)
        	percent = (double)time/range.lengthInMinutes();
        else
            //Compensate for only 1 hour a day being searched
        	percent = 24.0*time/range.lengthInMinutes();
        percent *= 100;
        percent = Math.round(percent);
        percent /= 100;
        return percent;
    }
    
    private BufferedWriter writer(boolean append) throws FileNotFoundException, IOException{
        return new BufferedWriter(new FileWriter(file, append));
    }
    
    private Date getFirstSession(){
        Date first = new Date();
        Date[] dates = new Date[sessions.size()];
        sessions.keySet().toArray(dates);
        for(int i=0; i<dates.length; i++){
            if(dates[i].before(first))
                first = dates[i];
        }
        return first;
    }
}
