package de.craftlancer.core.items;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import de.craftlancer.core.CLCore;
import de.craftlancer.core.Utils;
import de.craftlancer.core.command.SubCommand;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class CustomItemListCommand extends SubCommand {
    private static final long PAGE_ENTRY_COUNT = 10;

    private CustomItemRegistry registry;
    
    public CustomItemListCommand(CLCore plugin, CustomItemRegistry registry) {
        super("clcore.itemregistry.list", plugin, true);
        this.registry = registry;
    }
    
    @Override
    protected String execute(CommandSender sender, Command cmd, String label, String[] args) {
        if(!checkSender(sender))
            return "You can't run this command.";
        
        int page = (args.length == 2 ? Integer.parseInt(args[1]) : 1) - 1;
        
        if(page < 0)
            return "Page can't be negative!";
        
        BaseComponent base = new TextComponent("Key - Item - Action");
        
        registry.getItems().entrySet().stream().skip(page * PAGE_ENTRY_COUNT).forEach(a -> {
            BaseComponent delete = new TextComponent("[Delete]");
            delete.setColor(ChatColor.RED);
            delete.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/registeritem remove " + a.getKey()));
            
            BaseComponent entry = new TextComponent(a.getKey());
            entry.addExtra(" - ");
            entry.addExtra(Utils.getItemComponent(a.getValue()));
            entry.addExtra(" | ");
            entry.addExtra(delete);
            
            base.addExtra("\n");
            base.addExtra(entry);
        });
        
        sender.spigot().sendMessage(base);
        return null;
    }
    
    @Override
    public void help(CommandSender sender) {
        // TODO Auto-generated method stub
        
    }
    
}
