package psy.ActivityHistory;

import psy.ActivityHistory.ActivityHistory;

public class FileCleaner implements Runnable{
    ActivityHistory plugin;
    public FileCleaner(ActivityHistory pl){
        plugin = pl;
    }
    
    public void run(){
        File dir = new File(plugin.accessConfig().getString("general.logFilesLocation"));
        
        for(File file : dir.listFiles()){
            //If file is not a log file, skip it
            if(!file.isFile() || !file.getPath().endsWith(".log"))
                continue;
            if(file.getPath().endsWith("groups.log")){
                GroupLogFile pfile = new GroupLogFile(file);
                boolean successful = pfile.removeSessions();
                if(!successful) System.out.println(ActivityHistory.messages.getString("error.clean");
            }else{
                PlayerLogFile pfile = new PlayerLogFile(file);
                boolean successful = pfile.removeSessions();
                if(!successful) System.out.println(ActivityHistory.messages.getString("error.clean");
            }
        }
        plugin.accessConfig().set("cleaner.lastCleanTime", (new Date()).getTime());
    }
}
