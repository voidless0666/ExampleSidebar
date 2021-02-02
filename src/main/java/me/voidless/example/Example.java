package me.voidless.example;

import me.voidless.voidlib.sidebar.SidebarAPI;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Example extends JavaPlugin {
    // Example of Sidebar and Nametags only using VoidLib and Spigot
    public DisplayHandler displayHandler;

    @Override
    public void onEnable() {
        // Initialize the sidebar manager
        SidebarAPI.init(this);
        this.displayHandler = new DisplayHandler(this);

        Bukkit.getPluginManager().registerEvents(this.displayHandler, this);
        getCommand("example").setExecutor(new CommandExample(this));
        Bukkit.getConsoleSender().sendMessage("[Example] Using VoidLib " + SidebarAPI.getVersion());
    }
}
