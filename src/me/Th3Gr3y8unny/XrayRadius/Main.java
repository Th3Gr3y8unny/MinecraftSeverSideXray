package me.Th3Gr3y8unny.XrayRadius;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BlockIterator;

public class Main extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
	}
	
	private int radius;
	private boolean enabled;
	private ArrayList<Location> locs = new ArrayList<Location>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (args.length > 0 && !args[0].equals("reset")) {
			enabled = true;
			radius = Integer.parseInt(args[0]);
		}
		else if (args.length > 0 && args[0].equals("reset")) {
			Player p = (Player) sender;
			enabled = false;
			//Resets block back to original
			for (Location loc : locs) {
				while (loc.getY() > 1) {
					for (double x = loc.getX() - radius; x <= loc.getX() + radius; x++) {
						for (double z = loc.getZ() - radius; z <= loc.getZ() + radius; z++) {
							Location block = new Location(p.getWorld(), x, loc.getY(), z);
							p.sendBlockChange(block, block.getBlock().getBlockData());					
						}
					}
					loc = loc.clone().subtract(0, 1, 0);
				}
			}
			locs.clear();
		}
		
		return false;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (enabled) {
			Player p = event.getPlayer();
			Location loc = event.getBlock().getLocation();
			locs.add(loc);
			
			//Makes blocks invisible
			while (loc.getY() > 1) {
				for (double x = loc.getX() - radius; x <= loc.getX() + radius; x++) {
					for (double z = loc.getZ() - radius; z <= loc.getZ() + radius; z++) {
						Location block = new Location(p.getWorld(), x, loc.getY(), z);
						if (block.getBlock().getType() == Material.STONE
								|| block.getBlock().getType() == Material.DIRT
								|| block.getBlock().getType() == Material.GRASS) {
							p.sendBlockChange(block, Material.BARRIER.createBlockData());
						}
					}
				}
				loc = loc.clone().subtract(0, 1, 0);
			}
		}
	}
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("xrayradius")) {
            return Arrays.asList("2", "reset");
        }
        return null;
    }
	
	
	
	@EventHandler
	public void onPlayerClicks(PlayerInteractEvent event) {
		Player player = event.getPlayer();
	    Action action = event.getAction();
	    
	    //Player starts mining block so it updates the block
	     if ( action.equals( Action.LEFT_CLICK_BLOCK )) {
	    	 BlockIterator iter = new BlockIterator(player, 6); //Get location of block with with max distance of 100
	    		
	    	 Block lastBlock = iter.next();

	    	 while (iter.hasNext()) {
	    		 lastBlock = iter.next();

	    		 if (lastBlock.getType() == Material.AIR) {
	    			 continue;
	    		 }
	    		 break;
	    	 }
	    	 Location loc = lastBlock.getLocation();
	    	 player.sendBlockChange(loc, loc.getBlock().getBlockData());
	     }
	}
}
