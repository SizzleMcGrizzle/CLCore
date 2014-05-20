package de.craftlancer.core.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;

import de.craftlancer.core.Utils;

// TODO externalize common code between CommandHandler and SubCommandHandler
public abstract class CommandHandler implements TabExecutor
{
    private Map<String, SubCommand> commands = new HashMap<String, SubCommand>();
    private Plugin plugin;
    
    public CommandHandler(Plugin plugin)
    {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        args = parseArgumentStrings(args);
        
        String message = null;
        
        if (args.length == 0 || !commands.containsKey(args[0]))
            if (commands.containsKey("help"))
                message = commands.get("help").execute(sender, cmd, label, args);
            else
                return false;
        else
            message = commands.get(args[0]).execute(sender, cmd, label, args);
        
        if (message != null)
            sender.sendMessage(message);
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args)
    {
        switch (args.length)
        {
            case 0:
                return null;
            case 1:
                List<String> l = Utils.getMatches(args[0], commands.keySet());
                for (String str : l)
                    if (!sender.hasPermission(commands.get(str).getPermission()))
                        l.remove(l);
                return l;
            default:
                if (!commands.containsKey(args[0]))
                    return null;
                else
                    return commands.get(args[0]).onTabComplete(sender, args);
        }
    }
    
    public void registerSubCommand(String name, SubCommand command, String... alias)
    {
        Validate.notNull(command, "Command can't be null!");
        Validate.notEmpty(name, "Commandname can't be empty!");
        Validate.isTrue(!commands.containsKey(name), "Command " + name + " is already defined");
        for (String a : alias)
            Validate.isTrue(!commands.containsKey(a), "Command " + a + " is already defined");
        
        if (command instanceof SubCommandHandler)
            Validate.isTrue(((SubCommandHandler) command).getDepth() == 1, "The depth of a SubCommandHandler must be the depth of the previous Handler + 1!");
        
        commands.put(name, command);
        for (String s : alias)
            commands.put(s, command);
    }
    
    protected Plugin getPlugin()
    {
        return plugin;
    }
    
    protected Map<String, SubCommand> getCommands()
    {
        return commands;
    }
    
    public static String[] parseArgumentStrings(String[] args)
    {
        List<String> tmp = new ArrayList<String>();
        
        StringBuilder b = null;
        boolean open = false;
        
        for (String s : args)
        {
            if (b == null)
            {
                if (s.startsWith("\"") && !s.endsWith("\""))
                {
                    b = new StringBuilder();
                    b.append(s.substring(1));
                    b.append(" ");
                }
                else
                    tmp.add(s);
            }
            else
            {
                if ((s.endsWith("\"") && !open) || s.endsWith("\"\""))
                {
                    b.append(s.substring(0, s.length() - 1));
                    tmp.add(b.toString());
                    b = null;
                }
                else if (s.startsWith("\""))
                {
                    if (open)
                        return null;
                    
                    open = true;
                    b.append(s);
                    b.append(" ");
                }
                else
                {
                    if (s.endsWith("\""))
                        if (open)
                            open = false;
                        else
                            return null;
                    
                    b.append(s);
                    b.append(" ");
                }
            }
            
        }
        if (b != null)
            return null;
        
        return tmp.toArray(new String[tmp.size()]);
    }
}
