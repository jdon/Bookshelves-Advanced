package me.jdon.ludus.book.shelf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.NBTTagString;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	Logger log = Logger.getLogger("Minecraft.LudusCraft");
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	public static String tag = ChatColor.BLUE + "[" + ChatColor.AQUA + "Bookshelves Advanced" + ChatColor.BLUE + "]" + ChatColor.YELLOW + " ";
	static HashMap<String, Location> Players = new HashMap<String, Location>();
    private String author;
    private String title;
    private String[] pages;
	
	public void onEnable() {
		//register events in this class
		getServer().getPluginManager().registerEvents(this, this);
		// set the config
		this.getConfig().options().copyDefaults(true);
		// save the config
		this.saveConfig();
		this.getConfig().addDefault("Size", 9);
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
		// print to the console that the plugin in enabled
		log.info("[Bookshelves Advanced] has been Enabled!");
	}
			@EventHandler
			public void breaks(BlockBreakEvent ev){
				if(ev.isCancelled())return;
				if(ev.getBlock().getTypeId() == 47){
					int x = 0;
					int size = this.getConfig().getInt("Size")-1;
					while (x <= size){
						this.reloadCustomConfig(ev.getBlock().getLocation());
						if(this.getCustomConfig(ev.getBlock().getLocation()).getItemStack("b"+x)!=null){
							ev.getBlock().getLocation();
							if(this.getCustomConfig(ev.getBlock().getLocation()).getItemStack("b"+x).getTypeId() == 387){
								CraftItemStack newbook = new CraftItemStack(Material.WRITTEN_BOOK);
						        NBTTagCompound newBookData = new NBTTagCompound();
						        newBookData.setString("author",this.getCustomConfig(ev.getBlock().getLocation()).getString("author"+x));
						        newBookData.setString("title",this.getCustomConfig(ev.getBlock().getLocation()).getString("title"+x));
						        NBTTagList nPages = new NBTTagList();
						        List<String> pages = this.getCustomConfig(ev.getBlock().getLocation()).getStringList("content"+x);
						        String[] thepages = new String[pages.size()];
						        thepages = pages.toArray(thepages);
						        for(int i1 = 0;i1<thepages.length;i1++)
						        {  
						            nPages.add(new NBTTagString(thepages[i1],thepages[i1]));
						        }
						        newBookData.set("pages", nPages);
						        newbook.getHandle().tag = newBookData;
						        if(ev.getPlayer().getInventory().firstEmpty() == -1){
						        	ev.setCancelled(true);
						        	ev.getPlayer().sendMessage(tag+"You cant break a bookshelf, as you dont have enough room in your inventory for the books");
						        }else{
								       ev.getPlayer().getInventory().addItem(newbook);
										this.getCustomConfig(ev.getBlock().getLocation()).set("b"+x, null);
						        }
							}
						}
					    x++;
					}
				}
			}
			
	@EventHandler
	public void interact(PlayerInteractEvent ev){
		if(ev.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(ev.getClickedBlock().getType() == Material.BOOKSHELF ){
				if(ev.getPlayer().getItemInHand().getType() == Material.AIR){
					int size = this.getConfig().getInt("Size")-1;
					Inventory i = getServer().createInventory(null, size+1, "BookShelf");
					int x = 0;
					while(x <= size){
						this.reloadCustomConfig(ev.getClickedBlock().getLocation());
						if(this.getCustomConfig(ev.getClickedBlock().getLocation()).getItemStack("b"+x)!=null){
							if(this.getCustomConfig(ev.getClickedBlock().getLocation()).getItemStack("b"+x).getTypeId() == 387){
								CraftItemStack newbook = new CraftItemStack(Material.WRITTEN_BOOK);
						        NBTTagCompound newBookData = new NBTTagCompound();
						        newBookData.setString("author",this.getCustomConfig(ev.getClickedBlock().getLocation()).getString("author"+x));
						        newBookData.setString("title",this.getCustomConfig(ev.getClickedBlock().getLocation()).getString("title"+x));
						        NBTTagList nPages = new NBTTagList();
						        List<String> pages = this.getCustomConfig(ev.getClickedBlock().getLocation()).getStringList("content"+x);
						        String[] thepages = new String[pages.size()];
						        thepages = pages.toArray(thepages);
						        for(int i1 = 0;i1<thepages.length;i1++)
						        {  
						            nPages.add(new NBTTagString(thepages[i1],thepages[i1]));
						        }
						        newBookData.set("pages", nPages);
						        newbook.getHandle().tag = newBookData;
						        i.setItem(x, newbook);
							}else{
								i.setItem(x, this.getCustomConfig(ev.getClickedBlock().getLocation()).getItemStack("b"+x));
						}
						}
						x++;
					}
					ev.getPlayer().openInventory(i);
					Players.put(ev.getPlayer().getName(), ev.getClickedBlock().getLocation());
				}
				}
			}
		}
	
	@EventHandler
	public void closeinv(InventoryCloseEvent ev){
		if(ev.getPlayer() instanceof Player){
			Player p = (Player)ev.getPlayer();
		if(Players.containsKey(p.getName())){
			int x= 0;
			int size = this.getConfig().getInt("Size")-1;
			while( x <= size){
				this.reloadCustomConfig(Players.get(p.getName()));
				if(ev.getInventory().getItem(x) != null){
				if(ev.getInventory().getItem(x).getTypeId() == 387){
					ItemStack item = ev.getInventory().getItem(x);
					this.getCustomConfig(Players.get(p.getName())).set("b"+x, ev.getInventory().getItem(x));
					this.saveCustomConfig(Players.get(p.getName()));
					booksave(item,p.getName(),x);
				}else{
					ev.getPlayer().getWorld().dropItemNaturally(ev.getPlayer().getLocation(), ev.getInventory().getItem(x));
					p.sendMessage(tag+"You can't put a "+ChatColor.RED+ev.getInventory().getItem(x).getType().toString().toLowerCase()+ChatColor.YELLOW+" in a bookshelf");
				}
				}else{
					this.getCustomConfig(Players.get(p.getName())).set("b"+x, null);
				}
				this.saveCustomConfig(Players.get(p.getName()));
					x++;
				}
			Players.remove(p.getName());
		}
		}
	}
	
	public void reloadCustomConfig(Location l) {
		String name = l.getWorld().getName()+","+l.getBlockX()+","+l.getBlockY()+","+l.getBlockZ()+".yml";
	    customConfigFile = new File(getDataFolder() + File.separator + "data", name);
	    customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = this.getResource(name);
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        customConfig.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getCustomConfig(Location l) {
	    if (customConfig == null) {
	        this.reloadCustomConfig(l);
	    }
	    return customConfig;
	}
	
	public void saveCustomConfig(Location l) {
	    if (customConfig == null || customConfigFile == null) {
	    return;
	    }
	    try {
	        getCustomConfig(l).save(customConfigFile);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
	    }
	}
	
	public void booksave(ItemStack bookItem,String name,int x){
NBTTagCompound bookData = ((CraftItemStack) bookItem).getHandle().tag;
        
        this.author = bookData.getString("author");
        this.title = bookData.getString("title");
                
        NBTTagList nPages = bookData.getList("pages");

        String[] sPages = new String[nPages.size()];
        for(int i = 0;i<nPages.size();i++)
        {
            sPages[i] = nPages.get(i).toString();
        }
                
        this.pages = sPages;
        this.reloadCustomConfig(Players.get(name));
		this.getCustomConfig(Players.get(name)).set("author"+x, this.author);
		this.getCustomConfig(Players.get(name)).set("title"+x, this.title);
		this.getCustomConfig(Players.get(name)).set("content"+x, this.pages);
		this.saveCustomConfig(Players.get(name));
	}
	
	@EventHandler
	public void piston (BlockPistonRetractEvent ev){
		int x = 0;
		int size = this.getConfig().getInt("Size")-1;
		while (x <= size){
			this.reloadCustomConfig(ev.getBlock().getLocation());
			if(this.getCustomConfig(ev.getBlock().getLocation()).getItemStack("b"+x)!=null){
				ev.setCancelled(true);
			}
			x++;
			}
	}
	@EventHandler
	public void pistonextend (BlockPistonExtendEvent ev){
		int x = 0;
		int size = this.getConfig().getInt("Size")-1;
		while (x <= size){
			this.reloadCustomConfig(ev.getBlock().getLocation());
			if(this.getCustomConfig(ev.getBlock().getLocation()).getItemStack("b"+x)!=null){
				ev.setCancelled(true);
			}
			x++;
			}
	}
}
