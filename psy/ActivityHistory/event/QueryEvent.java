package psy.ActivityHistory.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.command.CommandSender;

public class QueryEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final CommandSender sender;
    
    public QueryEvent(CommandSender sender){
        super(true);
        this.sender = sender;
    }
    public HandlerList getHandlers(){
        return handlers;
    }
}
