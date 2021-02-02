package me.voidless.example;

import me.voidless.voidlib.exceptions.MessageTooLongException;
import me.voidless.voidlib.sidebar.SidebarAPI;
import me.voidless.voidlib.sidebar.SidebarBuilder;
import me.voidless.voidlib.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.*;

public class DisplayHandler implements Listener {
    public final List<String> globalHiddenNames;
    public final Map<UUID, List<String>> hiddenNames;
    private BukkitTask task;

    public DisplayHandler(final Plugin plugin){
        this.globalHiddenNames = new ArrayList<>();
        this.hiddenNames = new HashMap<>();
        startUpdateLoop(plugin);
    }

    // Event to handle the sidebar and global hidden names
    @EventHandler
    public void onJoin(final PlayerJoinEvent event){
        final Player player = event.getPlayer();
        // Add the sidebar to the player
        addSidebar(player);
        // Add all the global hidden names
        for (final String name : this.globalHiddenNames){
            CommandExample.hideNametag(player, name);
        }

        // If the player has any hidden players hide them
        if (this.hiddenNames.containsKey(player.getUniqueId())){
            // Add all the local hidden names
            for (final String name : this.hiddenNames.get(player.getUniqueId())){
                CommandExample.hideNametag(player, name);
            }
        }
    }

    private void startUpdateLoop(final Plugin plugin){
        this.task = new BukkitRunnable() {
            @Override
            public void run(){
                // Update the sidebars
                updateSidebar();
            } // The 0 is the delay before it starts, the 20 is the time between each update, this is in ticks
        }.runTaskTimer(plugin, 0, 20);
    }

    public void addSidebar(final Player player){
        // If the player is null stop
        if (player == null) return;
        try {
            // Add the sidebar from the new SidebarBuilder()
            // This is a default sidebar you could make multiple instead but i will just create a default one, to remove it do SidebarAPI.removeSidebar(player|uuid)
            // Or to change it do SidebarAPI.getSidebar(player|uuid).reset("New sidebar title"), you can also add a specific scoreboard on reset() by doing reset("title", scoreboard)
            SidebarAPI.addSidebar(new SidebarBuilder(player, "Sidebar title")
                    // As the time will update this has to be a updateableText
                    .setUpdatableText("time", getTime(), 1)
                    // This will create a empty line
                    .addEmptyLine(2)

                    // Since you can't change name this will be static
                    .setText(ColorUtils.colorText("Name: &a" + player.getName()), 3)
                    // Like the time, since your health can change this will have to update
                    .setUpdatableText("health", ColorUtils.colorText("Health: &a" + player.getHealth()), 4)
                    // Another empty line
                    .addEmptyLine(5)

                    // And same as before this has to be updated
                    .setUpdatableText("online", ColorUtils.colorText("Online: &a" + Bukkit.getOnlinePlayers().size()), 6));
        } catch (MessageTooLongException e) {
            e.printStackTrace();
        }
    }

    private String getTime(){
        // Returns the current time in a 24 hour format
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    private String getHealth(final Player player){
        // Returns the current health
        final String health = String.valueOf(player.getHealth());
        return health.length() > 4 ? health.substring(0, 4) : health;
    }

    private void updateSidebar(){
        // If there are no sidebars active, stop
        if (SidebarAPI.getActiveSidebars() <= 0) return;
        // Loop all players
        for (final Player player : Bukkit.getOnlinePlayers()){
            final UUID id = player.getUniqueId();
            // Check if player has a sidebar
            if (!SidebarAPI.hasSidebar(id)) continue;
            // Get the sidebar
            final SidebarBuilder builder = SidebarAPI.getBuilder(id);
            // If the sidebar doesn't exist, stop
            if (builder == null) continue;
            // Update text
            try {
                builder.updateText("time", getTime());
                builder.updateText("health", ColorUtils.colorText("Health: &a" + player.getHealth()));
                builder.updateText("online", ColorUtils.colorText("Online: &a" + Bukkit.getOnlinePlayers().size()));
            } catch (MessageTooLongException e) {
                e.printStackTrace();
            }
            // Update the sidebar
            builder.update();
        }
    }
}
