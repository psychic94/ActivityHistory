package psy.ActivityHistory;

import java.util.Date;

public class TimeRange{
    private Date start, end;
    public TimeRange(Date s, Date e){
        start = s;
        end = e;
    }
    
    public TimeRange(String str){
        String[] times = str.split("-");
        start = new Date(new Integer(times[0].trim()));
        end = new Date(new Integer(times[1].trim()));
    }
    
    public boolean includes(Date date){
        return (date.before(end) && date.after(start));
    }
    
    public long length(){
        return (end.getTime() - start.getTime());
    }
    
    public double lengthInSeconds(){
        return (length()/1000);
    }
    
    public double lengthInMinutes(){
        return (lengthInSeconds()/60);
    }
    
    public double lengthInHours(){
        return (lengthInMinutes()/60);
    }
    
    public double lengthInDays(){
        return (lengthInHours()/24);
    }
    
    public double lengthInYears(){
        return (lengthInDays()/365);
    }
    
    public String toString(){
        return ("" + start.getTime() + "-" + end.getTime());
    }
    //Not yet implemented
    public long overlap(TimeRange other){
        return 0;
    }
}
