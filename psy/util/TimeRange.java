package psy.util;

import java.util.Date;

public class TimeRange{
    private Date start, end;
    public TimeRange(Date s){
        this(s, null);
    }
    public TimeRange(Date s, Date e){
        start = s;
        end = e;
    }
    
    public TimeRange(String str){
        String[] times = str.split("-");
        start = new Date(new Integer(times[0].trim()));
        end = new Date(new Integer(times[1].trim()));
    }
    
    public Date getStart(){
        return start;
    }
    
    public Date getEnd(){
        return end;
    }
    
    public void setStart(Date s){
        start = s;
    }
    
    public void setEnd(Date e){
        end = e;
    }
    
    public boolean includes(Date date){
        if(start==null)
            return date.before(end);
        else if(end==null)
            return date.after(start);
        else
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
    
    public long overlap(TimeRange other){
        if(other.getEnd().before(this.start))
            return 0;
        else if(other.getStart().after(this.end))
            return 0;
        else{
            Date overlapStart, overlapEnd;
            if(other.getStart().before(this.start)){
                overlapStart = this.start;
            }else{
                overlapStart = other.getStart();
            }
            if(other.getEnd().before(this.end)){
                overlapEnd = other.getEnd();
            }else{
                overlapEnd = this.end;
            }
            return (overlapEnd.getTime() - overlapStart.getTime());
        }
    }
    
    public long timeUntilEnd(){
        Date now = new Date();
        return (end.getTime() - now.getTime());
    }
    
    public double secondsUntilEnd(){
        return (timeUntilEnd()/1000);
    }
    
    public double minutesUntilEnd(){
        return (secondsUntilEnd()/60);
    }
    
    public double hoursUntilEnd(){
        return (minutesUntilEnd()/60);
    }
    
    public double daysUntilEnd(){
        return (hoursUntilEnd()/24);
    }
    
    public double yearsUntilEnd(){
        return (daysUntilEnd()/365);
    }
}
