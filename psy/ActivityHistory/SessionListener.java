package psy.ActivityHistory;

import java.util.HashMap;
import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import psy.util.TimeRange;

public class SessionListener implements Listener{
    private HashMap<String, TimeRange> sessions = new HashMap();
    private double minOffTime, minOnTime;
    ActivityHistory plugin;
    public SessionListener(Plugin pl){
        plugin = (ActivityHistory)pl;
        minOffTime = plugin.config.getDouble("players.minOffTime");
        minOnTime = plugin.config.getDouble("players.minOnTime");
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent pje){
        String playername = pje.getPlayer().getName();
        if(sessions.get(playername) == null){
            sessions.put(playername, new TimeRange(new Date()));
        }else{
            TimeRange lastSession = sessions.remove(playername);
            if(lastSession.minutesUntilEnd()<(0-minOffTime)){
                if(lastSession.lengthInMinutes()>=minOnTime){
                    try{
                        BufferedWriter filebw = loadLogFile(playername);
                        filebw.write(lastSession.toString());
                        filebw.newLine();
                        filebw.flush();
                    }catch(Exception e){
                    }
                }
                lastSession.setStart(new Date());
            }
            lastSession.setEnd(null);
            sessions.put(playername, lastSession);
        }
    }
    
    private BufferedWriter loadLogFile(String name)throws IOException{
        String filename = plugin.accessConfig().getString("general.logFilesLocation") + "/" + name.toLowerCase() + ".log";
        File file = new File(filename);
        FileWriter filer = new FileWriter(file);
        return new BufferedWriter(filer);
    }
}
