package psy.ActivityHistory;

import java.util.HashMap;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class SessionListener implements Listener{
    private HashMap<String, TimeRange> sessions = new HashMap();
    private double minOffTime, minOnTime;
    ActivityHistory plugin;
    public SessionListener(Plugin pl){
        plugin = (ActivityHistory)pl;
        minOffTime = plugin.config.getDouble("players.minOffTime");
        minOnTime = plugin.config.getDouble("players.minOnTime");
    }
    
    //@EventHandler
}
