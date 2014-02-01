package com.Shockwave317.mineside;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.confuser.barapi.BarAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;


public class Main extends JavaPlugin implements Listener {
	
	public static Main plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	
	private FileConfiguration settingsConfig = null;
    private File settingsConfigFile = null;
	private FileConfiguration playersConfig = null;
    private File playersConfigFile = null;
    /** YML FILES */
    public void reloadsettingsConfig() {
        if (settingsConfigFile == null) {
        	settingsConfigFile = new File(getDataFolder(), "settings.yml");
        }
        settingsConfig = YamlConfiguration.loadConfiguration(settingsConfigFile);
 
        // Look for defaults in the jar
        InputStream defConfigStream = this.getResource("settings.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            settingsConfig.setDefaults(defConfig);
        }
    }
 
    //Method from http://wiki.bukkit.org/Configuration_API_Reference
    public FileConfiguration getsettingsConfig() {
        if (settingsConfig == null) {
            this.reloadsettingsConfig();
        }
        return settingsConfig;
    }
 
    //Method from http://wiki.bukkit.org/Configuration_API_Reference
    public void savesettingsConfig() {
        if (settingsConfig == null || settingsConfigFile == null) {
        return;
        }
        try {
            getsettingsConfig().save(settingsConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + settingsConfigFile, ex);
        }
    }
    
    public void reloadplayersConfig() {
        if (playersConfigFile == null) {
        	playersConfigFile = new File(getDataFolder(), "players.yml");
        }
        playersConfig = YamlConfiguration.loadConfiguration(playersConfigFile);
 
        // Look for defaults in the jar
        InputStream defConfigStream = this.getResource("players.yml");
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            playersConfig.setDefaults(defConfig);
        }
    }
 
    //Method from http://wiki.bukkit.org/Configuration_API_Reference
    public FileConfiguration getplayersConfig() {
        if (playersConfig == null) {
            this.reloadplayersConfig();
        }
        return playersConfig;
    }
 
    //Method from http://wiki.bukkit.org/Configuration_API_Reference
    public void saveplayersConfig() {
        if (playersConfig == null || playersConfigFile == null) {
        return;
        }
        try {
            getplayersConfig().save(playersConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + playersConfigFile, ex);
        }
    }
    
    /** START OF CLASS */
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Has Been Disabled!");
		this.reloadConfig();
		this.reloadsettingsConfig();
		this.reloadplayersConfig();
		this.saveConfig();
		this.savesettingsConfig();
		this.saveplayersConfig();
	}
	
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version: " + pdfFile.getVersion() + " Has Been Enabled!");
	    getCommand("mineside").setExecutor(this);
	    PluginManager pm = this.getServer().getPluginManager();
	    pm.registerEvents(this, this); 
	    
    	
		this.reloadConfig();
		this.reloadsettingsConfig();
		this.reloadplayersConfig();
		this.saveConfig();
		this.savesettingsConfig();
		this.saveplayersConfig();
        
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) sender;
		String intro = ChatColor.BLACK + "[" + ChatColor.YELLOW + "Mineside" + ChatColor.BLACK + "] " + ChatColor.RESET + "";
		if (args.length == 0) {
			player.sendMessage("========== " + intro + "==========");
			player.sendMessage("/mineside admin");
			player.sendMessage("========== " + intro + "==========");
		}
		if (args.length == 1) {

			if (args[0].equalsIgnoreCase("status")) {
				Chunk chunk = player.getLocation().getChunk();
				
				String faction = getplayersConfig().getString("Players." + player.getName() + ".team");
				String chunkName = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".name");
				player.sendMessage(ChatColor.GREEN + "Current Status:");
				player.sendMessage("Chunk: " + chunkName);
				player.sendMessage("Faction: " + faction);
				
			}
			if (args[0].equalsIgnoreCase("admin")) {
				player.sendMessage("========== " + intro + "==========");
				player.sendMessage("/mineside settings set spawn IE/DR");
				player.sendMessage("/mineside settings set chunks <name>");
				player.sendMessage("/mineside settings set cp <name>");
				player.sendMessage("/mineside admin reload");
				player.sendMessage("/mineside admin resetsettings");
				player.sendMessage("========== " + intro + "==========");
			}
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("admin")) {
				if (args[1].equalsIgnoreCase("resetsettings")){
		    		getsettingsConfig().set("Settings.PlayerJoinEvent.MultipleServers.use", false);
		    		getsettingsConfig().set("Settings.PlayerJoinEvent.MultipleServers.hubserver", false);
		    		savesettingsConfig();
				}
				if (args[1].equalsIgnoreCase("reload")){
		    		player.sendMessage(intro + "Plugin Reloaded");
		    		reloadsettingsConfig();
		    		reloadConfig();
		    		reloadplayersConfig();
				}
			}
			if (player.hasPermission("mineside.play")) {
				if (args[0].equalsIgnoreCase("Join")){
					
					//Imperial Empire Join
					if (args[1].equalsIgnoreCase("IE")){
							getplayersConfig().set("Players." + player.getName() + ".team", "Imperial Empire");
							getplayersConfig().set("Players." + player.getName() + ".score", 0);
							saveplayersConfig();
							String world = getsettingsConfig().getString("Locations.IE.world");
			                String x = getsettingsConfig().getString("Locations.IE.x");
			                String y = getsettingsConfig().getString("Locations.IE.y");
			                String z = getsettingsConfig().getString("Locations.IE.z");
			                String pitch = getsettingsConfig().getString("Locations.IE.pitch");
			                String yaw = getsettingsConfig().getString("Locations.IE.yaw");
			                double xd = Double.parseDouble(x);
			                double yd = Double.parseDouble(y);
			                double zd = Double.parseDouble(z);
			                float pPitch = (float) Double.parseDouble(pitch);
			                float pYaw = (float) Double.parseDouble(yaw);
			                Location IESpawn = new Location(Bukkit.getWorld(world),xd,yd,zd);
			                IESpawn.setPitch(pPitch);
			                IESpawn.setYaw(pYaw);
			                player.teleport(IESpawn);
						
						
					}
					//Delfacto Rebels Join
					if (args[1].equalsIgnoreCase("DR")){
							getplayersConfig().set("Players." + player.getName() + ".team", "Delfacto Rebels");
							getplayersConfig().set("Players." + player.getName() + ".score", 0);
							saveplayersConfig();
							String world = getsettingsConfig().getString("Locations.DR.world");
			                String x = getsettingsConfig().getString("Locations.DR.x");
			                String y = getsettingsConfig().getString("Locations.DR.y");
			                String z = getsettingsConfig().getString("Locations.DR.z");
			                String pitch = getsettingsConfig().getString("Locations.DR.pitch");
			                String yaw = getsettingsConfig().getString("Locations.DR.yaw");
			                double xd = Double.parseDouble(x);
			                double yd = Double.parseDouble(y);
			                double zd = Double.parseDouble(z);
			                float pPitch = (float) Double.parseDouble(pitch);
			                float pYaw = (float) Double.parseDouble(yaw);
			                Location DRSpawn = new Location(Bukkit.getWorld(world),xd,yd,zd);
			                DRSpawn.setPitch(pPitch);
			                DRSpawn.setYaw(pYaw);
			                player.teleport(DRSpawn);
						
					}
					
				}
			}
		}
		if (args.length == 4) {
			if (player.hasPermission("mineside.admin")) {
				if (args[0].equalsIgnoreCase("settings")) {
					if (args[1].equalsIgnoreCase("set")) {
						if (args[2].equalsIgnoreCase("spawn")) {
							if (args[3].equalsIgnoreCase("IE")) {
								player.sendMessage(intro + "Imperial Empires spawn has been set!");
								String IEchunkName = "Imperial Empire Gate";
							    Chunk IEchunk = ((Player)sender).getLocation().getChunk();
							 
							    getConfig().set("Chunks." + IEchunk.getX() + "|" + IEchunk.getZ() + ".name", IEchunkName);
							    getConfig().set("Chunks." + IEchunk.getX() + "|" + IEchunk.getZ() + ".team", "Imperial Empire");
							    saveConfig();
							}
							if (args[3].equalsIgnoreCase("DR")) {
								player.sendMessage(intro + "Delfacto Rebels spawn has been set!");
								String DRchunkName = "Delfacto Rebels Gate";
							    Chunk DRchunk = ((Player)sender).getLocation().getChunk();
							 
							    getConfig().set("Chunks." + DRchunk.getX() + "|" + DRchunk.getZ() + ".name", DRchunkName);
							    getConfig().set("Chunks." + DRchunk.getX() + "|" + DRchunk.getZ() + ".team", "Delfacto Rebels");
							    saveConfig();
							    
							}
							if (args[3].equalsIgnoreCase("Lobby")) {
	                			
		                		Player p = (Player)sender;
		                        double x = p.getLocation().getX();
		                        double y = p.getLocation().getY();
		                        double z = p.getLocation().getZ();
		                        float pPitch = p.getLocation().getPitch();
		                        float pYaw = p.getLocation().getYaw();
		                        String w = p.getWorld().getName();
		                        getsettingsConfig().set("Locations.Lobby.world", w);
		                        getsettingsConfig().set("Locations.Lobby.x", x);
		                        getsettingsConfig().set("Locations.Lobby.y", y);
		                        getsettingsConfig().set("Locations.Lobby.z", z);
		                        getsettingsConfig().set("Locations.Lobby.pitch", pPitch);
				                getsettingsConfig().set("Locations.Lobby.yaw", pYaw);
		                        savesettingsConfig();
		                        p.sendMessage(ChatColor.DARK_GRAY + "You have set the spawn of " + ChatColor.BLUE + "Lobby!");
                		}
							if (args[3].equalsIgnoreCase("DRSpawn")) {
	                			
		                		Player p = (Player)sender;
		                        double x = p.getLocation().getX();
		                        double y = p.getLocation().getY();
		                        double z = p.getLocation().getZ();
		                        float pPitch = p.getLocation().getPitch();
		                        float pYaw = p.getLocation().getYaw();
		                        String w = p.getWorld().getName();
		                        getsettingsConfig().set("Locations.DR.world", w);
		                        getsettingsConfig().set("Locations.DR.x", x);
		                        getsettingsConfig().set("Locations.DR.y", y);
		                        getsettingsConfig().set("Locations.DR.z", z);
		                        getsettingsConfig().set("Locations.DR.pitch", pPitch);
				                getsettingsConfig().set("Locations.DR.yaw", pYaw);
		                        savesettingsConfig();
		                        p.sendMessage(ChatColor.DARK_GRAY + "You have set the spawn of " + ChatColor.BLUE + "DR Spawn!");
                		}
							if (args[3].equalsIgnoreCase("IESpawn")) {
	                			
		                		Player p = (Player)sender;
		                        double x = p.getLocation().getX();
		                        double y = p.getLocation().getY();
		                        double z = p.getLocation().getZ();
		                        float pPitch = p.getLocation().getPitch();
		                        float pYaw = p.getLocation().getYaw();
		                        String w = p.getWorld().getName();
		                        getsettingsConfig().set("Locations.IE.world", w);
		                        getsettingsConfig().set("Locations.IE.x", x);
		                        getsettingsConfig().set("Locations.IE.y", y);
		                        getsettingsConfig().set("Locations.IE.z", z);
		                        getsettingsConfig().set("Locations.IE.pitch", pPitch);
				                getsettingsConfig().set("Locations.IE.yaw", pYaw);
		                        savesettingsConfig();
		                        p.sendMessage(ChatColor.DARK_GRAY + "You have set the spawn of " + ChatColor.BLUE + "IE Spawn!");
                		}
						}
						if (args[2].equalsIgnoreCase("chunks")) {
							if (args[3].equalsIgnoreCase(args[3])) {
								String chunkName = args[3];
								player.sendMessage(intro + "Chunk " + chunkName + " has been saved.");
							    Chunk chunk = ((Player)sender).getLocation().getChunk();
							 
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".name", chunkName);
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team", "neutral");
							    saveConfig();
							}
						}
						if (args[2].equalsIgnoreCase("cp")) {
							if (args[3].equalsIgnoreCase(args[3])) {
								Chunk chunk = ((Player)sender).getLocation().getChunk();
								String chunkName = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".name");
								player.sendMessage(intro + "Control Point " + args[3] + " Placed in " + chunkName + "!");
								// Get the player's location.
							    Location loc = player.getLocation();
							    Location loc2 = player.getLocation();
							    // Sets loc to five above where it used to be. Note that this doesn't change the player's position.
							    loc.setY(loc.getY() + 0);
							    loc2.setY(loc2.getY() + 1);
							    World w = loc.getWorld();
							    World w2 = loc2.getWorld();
							    // Gets the block at the new location.
							    Block b = w.getBlockAt(loc);
							    Block b2 = w2.getBlockAt(loc2);
							    // Sets the block to type id 1 (stone).
							    b.setTypeId(89);
							    b2.setTypeId(35);
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team" + ".cp_name" , args[3]);
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team" + ".team" , "neutral");
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team" + ".x" , b2.getX());
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team" + ".y" , b2.getY());
							    getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team" + ".z" , b2.getZ());
							    saveConfig();
							}
						}
					}
				}
			}
		}
		
		return false;
		
	}
	

	HashMap<String, String> oldChunk = new HashMap<String, String>();
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
	       Chunk chunk = e.getPlayer().getLocation().getChunk();
	       Player player = e.getPlayer();
	       String chunkName = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".name");
	       String chunkTeam = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team");
	       String playerName = player.getName();
	       if (oldChunk.containsValue(chunkName)) {
	       } else {
	    	   if (chunkTeam == null) {
	    		   player.sendMessage("Entering " + chunkName);
	    		   oldChunk.put(playerName, chunkName);
		       } else if (chunkTeam.equals("Imperial Empire")) {
		    	   player.sendMessage("Entering " + ChatColor.RED + chunkName);
		    	   oldChunk.put(playerName, chunkName);
		       } else if (chunkTeam.equals("Delfacto Rebels")) {
		    	   player.sendMessage("Entering " + ChatColor.BLUE + chunkName);
		    	   oldChunk.put(playerName, chunkName);
		       } else {
		    	   player.sendMessage("Entering " + ChatColor.GREEN + chunkName);
		    	   oldChunk.put(playerName, chunkName);
		       }
	       }
	    }
	
	public void onPlayerRespawn (PlayerRespawnEvent e) {
		Player user = e.getPlayer();
    	Player p = (Player)user;
    	this.RespawnTimerArena1(p);
		
	}
	public void RespawnTimerArena1(final Player player) {
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			public void run() {
				
				if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Delfacto Rebels")) {
					getplayersConfig().set("Players." + player.getName() + ".team", "Delfacto Rebels");
					getplayersConfig().set("Players." + player.getName() + ".score", 0);
					saveplayersConfig();
					String world = getsettingsConfig().getString("Locations.DR.world");
		            String x = getsettingsConfig().getString("Locations.DR.x");
		            String y = getsettingsConfig().getString("Locations.DR.y");
		            String z = getsettingsConfig().getString("Locations.DR.z");
		            String pitch = getsettingsConfig().getString("Locations.DR.pitch");
		            String yaw = getsettingsConfig().getString("Locations.DR.yaw");
		            double xd = Double.parseDouble(x);
		            double yd = Double.parseDouble(y);
		            double zd = Double.parseDouble(z);
		            float pPitch = (float) Double.parseDouble(pitch);
		            float pYaw = (float) Double.parseDouble(yaw);
		            Location DRSpawn = new Location(Bukkit.getWorld(world),xd,yd,zd);
		            DRSpawn.setPitch(pPitch);
		            DRSpawn.setYaw(pYaw);
		            player.teleport(DRSpawn);
		 	   } else
			    if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Imperial Empire")) {
			    	getplayersConfig().set("Players." + player.getName() + ".team", "Imperial Empire");
					getplayersConfig().set("Players." + player.getName() + ".score", 0);
					saveplayersConfig();
					String world = getsettingsConfig().getString("Locations.IE.world");
		            String x = getsettingsConfig().getString("Locations.IE.x");
		            String y = getsettingsConfig().getString("Locations.IE.y");
		            String z = getsettingsConfig().getString("Locations.IE.z");
		            String pitch = getsettingsConfig().getString("Locations.IE.pitch");
		            String yaw = getsettingsConfig().getString("Locations.IE.yaw");
		            double xd = Double.parseDouble(x);
		            double yd = Double.parseDouble(y);
		            double zd = Double.parseDouble(z);
		            float pPitch = (float) Double.parseDouble(pitch);
		            float pYaw = (float) Double.parseDouble(yaw);
		            Location IESpawn = new Location(Bukkit.getWorld(world),xd,yd,zd);
		            IESpawn.setPitch(pPitch);
		            IESpawn.setYaw(pYaw);
		            player.teleport(IESpawn);
			    }
	            
	            
				}
			},20L); //20 = 1 second | (doing 60L for testing purposes)
		}
	
	/**@EventHandler
	public void onFreezeEvent(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Delfacto Rebels")) {
	    	   e.setCancelled(false);
	       } else if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Imperial Empire")) {
	    	   e.setCancelled(false);
	       } else {
	    	   e.setCancelled(true);
	       } 
	}*/
	
	@EventHandler
	public void onHealthGate(PlayerMoveEvent e) {
		Chunk chunk = e.getPlayer().getLocation().getChunk();
	       Player player = e.getPlayer();
	       String chunkName = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".name");
		if (chunkName == null) {
	    	   
	       } else if (chunkName.equals("Imperial Empire Gate")) {
	    	   if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Imperial Empire")) {
	    		   player.setHealth(20);
	    		   player.setFoodLevel(20);
	    	   }
	    	   
	       } else if (chunkName.equals("Delfacto Rebels Gate")) {
	    	   if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Delfacto Rebels")) {
	    		   player.setHealth(20);
	    		   player.setFoodLevel(20);
	    	   }
	       }
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		String intro = ChatColor.BLACK + "[" + ChatColor.YELLOW + "Mineside" + ChatColor.BLACK + "] " + ChatColor.RESET + "";
		Player player = e.getPlayer();
		if (getsettingsConfig().getBoolean("Settings.PlayerJoinEvent.MultipleServers.use", true)) {
			e.setJoinMessage(intro + player.getName() + " Has Joined!");
			
		}
		if (getplayersConfig().contains("Players." + player.getName())) {
			
		
		
		if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Delfacto Rebels")) {
			getplayersConfig().set("Players." + player.getName() + ".team", "Delfacto Rebels");
			getplayersConfig().set("Players." + player.getName() + ".score", 0);
			saveplayersConfig();
			String world = getsettingsConfig().getString("Locations.DR.world");
            String x = getsettingsConfig().getString("Locations.DR.x");
            String y = getsettingsConfig().getString("Locations.DR.y");
            String z = getsettingsConfig().getString("Locations.DR.z");
            String pitch = getsettingsConfig().getString("Locations.DR.pitch");
            String yaw = getsettingsConfig().getString("Locations.DR.yaw");
            double xd = Double.parseDouble(x);
            double yd = Double.parseDouble(y);
            double zd = Double.parseDouble(z);
            float pPitch = (float) Double.parseDouble(pitch);
            float pYaw = (float) Double.parseDouble(yaw);
            Location DRSpawn = new Location(Bukkit.getWorld(world),xd,yd,zd);
            DRSpawn.setPitch(pPitch);
            DRSpawn.setYaw(pYaw);
            player.teleport(DRSpawn);
 	   } else
	    if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Imperial Empire")) {
	    	getplayersConfig().set("Players." + player.getName() + ".team", "Imperial Empire");
			getplayersConfig().set("Players." + player.getName() + ".score", 0);
			saveplayersConfig();
			String world = getsettingsConfig().getString("Locations.IE.world");
            String x = getsettingsConfig().getString("Locations.IE.x");
            String y = getsettingsConfig().getString("Locations.IE.y");
            String z = getsettingsConfig().getString("Locations.IE.z");
            String pitch = getsettingsConfig().getString("Locations.IE.pitch");
            String yaw = getsettingsConfig().getString("Locations.IE.yaw");
            double xd = Double.parseDouble(x);
            double yd = Double.parseDouble(y);
            double zd = Double.parseDouble(z);
            float pPitch = (float) Double.parseDouble(pitch);
            float pYaw = (float) Double.parseDouble(yaw);
            Location IESpawn = new Location(Bukkit.getWorld(world),xd,yd,zd);
            IESpawn.setPitch(pPitch);
            IESpawn.setYaw(pYaw);
            player.teleport(IESpawn);
	    }
		} else {
			String world = getsettingsConfig().getString("Locations.Lobby.world");
            String x = getsettingsConfig().getString("Locations.Lobby.x");
            String y = getsettingsConfig().getString("Locations.Lobby.y");
            String z = getsettingsConfig().getString("Locations.Lobby.z");
            String pitch = getsettingsConfig().getString("Locations.Lobby.pitch");
            String yaw = getsettingsConfig().getString("Locations.Lobby.yaw");
            double xd = Double.parseDouble(x);
            double yd = Double.parseDouble(y);
            double zd = Double.parseDouble(z);
            float pPitch = (float) Double.parseDouble(pitch);
            float pYaw = (float) Double.parseDouble(yaw);
            Location Lobby = new Location(Bukkit.getWorld(world),xd,yd,zd);
            Lobby.setPitch(pPitch);
            Lobby.setYaw(pYaw);
            player.teleport(Lobby);
            player.sendMessage(intro + "Pick your team! /mineside join DR/IE");
		}
		
		
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		String intro = ChatColor.BLACK + "[" + ChatColor.YELLOW + "Mineside" + ChatColor.BLACK + "] " + ChatColor.RESET + "";
		Player player = e.getPlayer();
		if (getsettingsConfig().getBoolean("Settings.PlayerJoinEvent.MultipleServers.use", true)) {
			e.setQuitMessage(intro + player.getName() + " Has Left!");
			
		}
		
	}
	
	@EventHandler
	public void onPlayerChat (AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Delfacto Rebels")) {
			event.setFormat(ChatColor.BLUE + player.getName() + ChatColor.DARK_GRAY + "> " + ChatColor.RESET + event.getMessage());
		} else if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Imperial Empire")) {
			event.setFormat(ChatColor.RED + player.getName() + ChatColor.DARK_GRAY + "> " + ChatColor.RESET + event.getMessage());
		} else {
			event.setFormat(player.getName() + "> " + ChatColor.RESET + event.getMessage());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockBreakEvent (BlockBreakEvent event){
		
		String intro = ChatColor.BLACK + "[" + ChatColor.YELLOW + "Mineside" + ChatColor.BLACK + "] " + ChatColor.RESET + "";
		Player player = event.getPlayer();
		if (player.isOp()) {
			event.setCancelled(false);
			player.sendMessage(intro + "You have broken " + event.getBlock().getType());
		} else {
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onBlockPlace (BlockPlaceEvent event){
		String intro = ChatColor.BLACK + "[" + ChatColor.YELLOW + "Mineside" + ChatColor.BLACK + "] " + ChatColor.RESET + "";
		Player player = event.getPlayer();
		if (player.isOp()) {
			event.setCancelled(false);
			player.sendMessage(intro + "You have placed " + event.getBlock().getType());
		} else {
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteractBlock(PlayerInteractEvent event) {
		String intro = ChatColor.BLACK + "[" + ChatColor.YELLOW + "Mineside" + ChatColor.BLACK + "] " + ChatColor.RESET + "";
		Player player = event.getPlayer();
	    if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK){
	    	Chunk chunk = player.getLocation().getChunk();
                          if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Delfacto Rebels")) {
                        	  String x = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".x");
                              String y = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".y");
                              String z = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".z");
                              double xd = Double.parseDouble(x);
                              double yd = Double.parseDouble(y);
                              double zd = Double.parseDouble(z);
                              Location block = new Location(Bukkit.getWorld(player.getWorld().getName()),xd,yd,zd);
                              World w = block.getWorld();
                              Block b = w.getBlockAt(block);
                              b.setTypeId(35);
                        	  player.sendMessage(intro + "Delfacto Rebels have captured " + getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".cp_name"));
                        	  getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team", "Delfacto Rebels");
                        	  
                        	  Block drb = w.getBlockAt(block);
                        	  drb.setTypeIdAndData(35, (byte) 11, true);
                          } else
                          if (getplayersConfig().get("Players." + player.getName() + ".team").equals("Imperial Empire")) {
                        	  String x = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".x");
                              String y = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".y");
                              String z = getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".z");
                              double xd = Double.parseDouble(x);
                              double yd = Double.parseDouble(y);
                              double zd = Double.parseDouble(z);
                              Location block = new Location(Bukkit.getWorld(player.getWorld().getName()),xd,yd,zd);
                              World w = block.getWorld();
                              Block b = w.getBlockAt(block);
                              b.setTypeId(35);
                        	  player.sendMessage(intro + "Imperial Empire have captured " + getConfig().getString("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".cp_name"));
                        	  getConfig().set("Chunks." + chunk.getX() + "|" + chunk.getZ() + ".team", "Imperial Empire");
                        	  Block ieb = w.getBlockAt(block);
                        	  ieb.setTypeIdAndData(35, (byte) 14, true);
                          } else {
                        	  return;
                          }
                          
                          
                          return;
	    }
                          
	    	
	}
	
	
}
