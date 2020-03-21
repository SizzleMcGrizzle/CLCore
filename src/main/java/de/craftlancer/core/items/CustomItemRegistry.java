package de.craftlancer.core.items;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import de.craftlancer.core.CLCore;

/**
 * Register custom items under a unique name to be used by other plugins.
 * Keys used are case sensitive.
 */
public class CustomItemRegistry {
    
    private Map<String, ItemStack> items = new HashMap<>();
    private final File file;
    
    public CustomItemRegistry(CLCore plugin) {
        this.file = new File(plugin.getDataFolder(), "itemRegistry.yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.getValues(false).forEach((a, b) -> items.put(a, (ItemStack) b));
        
        plugin.getCommand("registerItem").setExecutor(new CustomItemCommandHandler(plugin, this));
    }
    
    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        items.forEach(config::set);
        try {
            config.save(file);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Adds an {@link ItemStack} under the given key to the registry. 
     * If the key has been in use previously the old value will be overwritten.
     * 
     * @param key the key to be used
     * @param item the item to be associated with the key
     * @return the item previously associated with that key or null
     */
    @Nullable
    public ItemStack addItem(@Nonnull String key, @Nonnull ItemStack item) {
        Validate.notNull(item, "the given ItemStack may not be null!");
        Validate.notNull(key, "the given key may not be null!");
        return items.put(key, item);
    }
    
    /**
     * Removes a key->item association from the registry.
     * 
     * @param key the key to remove
     * @return true when an association has been successfully removed, false otherwise
     */
    public boolean removeItem(@Nonnull String key) {
        return items.remove(key) == null;
    }
    
    /**
     * Gets the item associated with the given key, or null if non is found.
     * 
     * @param key the key to look for
     * @return the item associated with the given key, or null if non is found
     */
    @Nullable
    public ItemStack getItem(@Nonnull String key) {
        return items.get(key);
    }
    
    /**
     * Gets whether an item is associated with the given key.
     * 
     * @param key the key to look for
     * @return true when an item is associated with the key, false otherwise
     */
    public boolean hasItem(@Nonnull String key) {
        return items.containsKey(key);
    }
    
    /**
     * Gets an immutable view of the key->ItemStack map.
     * 
     * @return an immutable view of the key->ItemStack map
     */
    @Nonnull
    public Map<String, ItemStack> getItems() {
        return Collections.unmodifiableMap(items);
    }

    public Collection<String> getKeys() {
        return Collections.unmodifiableSet(items.keySet());
    }
}