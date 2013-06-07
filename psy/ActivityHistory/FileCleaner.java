package psy.ActivityHistory;

import java.io.File;
import java.util.Date;

import psy.ActivityHistory.ActivityHistory;
import psy.util.TimeRange;

public class FileCleaner implements Runnable{
    ActivityHistory plugin;
    public FileCleaner(ActivityHistory pl){
        plugin = pl;
    }
    
    public void run(){
        File dir = new File(plugin.accessConfig().getString("general.logFilesLocation"));
        Date date = new Date((new Date()).getTime() - 86400000 * plugin.accessConfig().getInt("cleaner.removeLogsOlderThan"));
        
        for(File file : dir.listFiles()){
            //If file is not a log file, skip it
            if(!file.isFile() || !file.getPath().endsWith(".log"))
                continue;
            if(file.getPath().endsWith("groups.log")){
                //GroupLogFile is not yet fully implemented
                //GroupLogFile pfile = new GroupLogFile(file);
                //boolean successful = pfile.removeSessions();
                //if(!successful) System.out.println(ActivityHistory.messages.getString("error.clean"));
            }else{
                try{
                    PlayerLogFile pfile = new PlayerLogFile(file);
                    pfile.removeSessions(new TimeRange(null, date));
                }catch(Exception e){
                    System.out.println(ActivityHistory.messages.getString("errors.cleaning"));
                    return;
                }
            }
        }
        plugin.accessConfig().set("cleaner.lastCleanTime", (new Date()).getTime());
    }
}
