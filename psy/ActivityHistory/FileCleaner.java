package psy.ActivityHistory;

import psy.ActivityHistory.ActivityHistory;

public class FileCleaner implements Runnable{
    ActivityHistory plugin;
    public FileCleaner(ActivityHistory pl){
        plugin = pl;
    }
    
    public void run(){
    }
}
