package org.cubeville.cvtictactoe;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;

public class CVTicTacToe extends JavaPlugin implements Listener, CommandExecutor {

    private Logger logger;
    private Location playAreaMin;
    private Location playAreaMax;
    private Set<Location> grid = new HashSet<>();
    Set<Material> transparent = new HashSet<>();

    public void onEnable() {
        this.logger = this.getLogger();
        playAreaMin = new Location(Bukkit.getWorld("mingames_hub"), -3, 91, -69);
        playAreaMax = new Location(Bukkit.getWorld("mingames_hub"), 5, 102, -59);
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), -1, 87, -63));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), -1, 87, -65));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), -1, 87, -67));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), 1, 87, -67));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), 1, 87, -65));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), 1, 87, -63));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), 3, 87, -63));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), 3, 87, -65));
        grid.add(new Location(Bukkit.getWorld("mingames_hub"), 3, 87, -67));
        transparent.add(Material.AIR);
        transparent.add(Material.GRAY_STAINED_GLASS);
        transparent.add(Material.RED_WOOL);
        transparent.add(Material.BLUE_WOOL);
        transparent.add(Material.LIGHT);
        Bukkit.getPluginManager().registerEvents(this, this);
        logger.info(ChatColor.LIGHT_PURPLE + "Plugin Enabled Successfully");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cannot be executed from console!");
            return false;
        }
        if(!playerInPlayArea((Player) sender)) {
            sender.sendMessage(ChatColor.RED + "You must be in the Tic Tac Toe area to use this command!");
            return false;
        }
        if(args.length == 1 && (args[0].equalsIgnoreCase("red") || args[0].equalsIgnoreCase("blue"))) {
            Location blockLoc = ((Player) sender).getTargetBlock(transparent, 15).getLocation();
            //System.out.println(blockLoc);
            blockLoc.add(0, 1, 0);
            //System.out.println(blockLoc);
            setTile(blockLoc, args[0].equalsIgnoreCase("red"));
            return true;
        }
        if(args.length == 1 && args[0].equalsIgnoreCase("clear")) {
            clearTiles();
            return true;
        }
        sender.sendMessage(ChatColor.RED + "Invalid command! Syntax is /ttt <red | blue | clear>");
        return false;
    }

    @EventHandler
    public void onPlayerClick(PlayerInteractEvent e) {
        if(!playerInPlayArea(e.getPlayer())) return;
        if(Objects.equals(e.getHand(), EquipmentSlot.OFF_HAND)) return;
        if(!Objects.equals(grid.iterator().next().getWorld(), e.getPlayer().getWorld())) return;
        Location blockLoc = e.getPlayer().getTargetBlock(transparent, 10).getLocation().add(0, 1, 0);
        if(grid.contains(blockLoc)) {
            e.setCancelled(true);
            if(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                //System.out.println("Left Clicked for loc: " + blockLoc);
                setTile(blockLoc, true);
            } else if(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                //System.out.println("Right Clicked for loc: " + blockLoc);
                setTile(blockLoc, false);
            }
        }
    }

    public boolean playerInPlayArea(Player player) {
        Location pLoc = player.getLocation();
        //System.out.println(pLoc.getWorld());
        //System.out.println(playAreaMin.getWorld());
        return pLoc.getWorld().equals(playAreaMin.getWorld()) &&
                pLoc.getX() >= playAreaMin.getX() && pLoc.getX() <= playAreaMax.getX() &&
                pLoc.getY() >= playAreaMin.getY() && pLoc.getY() <= playAreaMax.getY() &&
                pLoc.getZ() >= playAreaMin.getZ() && pLoc.getZ() <= playAreaMax.getZ();

    }

    public void setTile(Location blockLoc, Boolean isRed) {
        //System.out.println("attempting to set tile");
        //System.out.println(blockLoc.toString());
        if(grid.contains(blockLoc)) {
            //System.out.println("Attempted location contained in allowed set");
            blockLoc.getWorld().setType(blockLoc, isRed ? Material.RED_WOOL : Material.BLUE_WOOL);
        }
    }

    public void clearTiles() {
        for(Location l : grid) {
            l.getWorld().setType(l, Material.AIR);
        }
    }
}
