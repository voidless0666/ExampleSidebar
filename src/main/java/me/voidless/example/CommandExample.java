package me.voidless.example;

import me.voidless.voidlib.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class CommandExample implements CommandExecutor {
    private final Example example;
    public CommandExample(final Example example){
        this.example = example;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the command sender is a player and not a console
        if (!(sender instanceof Player)){
            sender.sendMessage(ColorUtils.colorText("&c> &fOnly players can use this command."));
            return false;
        }

        final Player player = (Player) sender;

        // Check for 2 arguments (hide/show, player)
        if (args.length == 2){
            if (args[0].equalsIgnoreCase("hide")){
                // Get the target player
                final Player target = Bukkit.getPlayer(args[1]);
                // If the target doesn't exist, stop
                if (target == null){
                    sender.sendMessage(ColorUtils.colorText("&c> &fCould not find the player (&a" + args[1] + "&f)"));
                    return false;
                }

                // Hide the nametag of the target, to the player
                addLocalNametag(player, target.getName());
                hideNametag(player, target.getName());
                sender.sendMessage(ColorUtils.colorText("&a> &fHid the nametag of (&a" + target.getName() + "&f) to (&ayourself&r)"));
                return true;
            } else if (args[0].equalsIgnoreCase("show")){
                // Get the target player
                final Player target = Bukkit.getPlayer(args[1]);
                // If the target doesn't exist, stop
                if (target == null){
                    sender.sendMessage(ColorUtils.colorText("&c> &fCould not find the player (&a" + args[1] + "&f)"));
                    return false;
                }

                // Show the nametag of the target, to the player
                removeLocalNametag(player, target.getName());
                showNametag(player, target.getName());
                sender.sendMessage(ColorUtils.colorText("&a> &fRevealed the nametag of (&a" + target.getName() + "&f) to (&ayourself&r)"));
                return true;
            } else if (args[0].equalsIgnoreCase("hideall")){
                // Get the target player
                final Player target = Bukkit.getPlayer(args[1]);
                // If the target doesn't exist, stop
                if (target == null){
                    sender.sendMessage(ColorUtils.colorText("&c> &fCould not find the player (&a" + args[1] + "&f)"));
                    return false;
                }

                // Hide the nametag of the target, to everyone
                this.example.displayHandler.globalHiddenNames.add(target.getName());
                for (final Player players : Bukkit.getOnlinePlayers()) hideNametag(players, target.getName());
                sender.sendMessage(ColorUtils.colorText("&a> &fHid the nametag of (&a" + target.getName() + "&f) to (&aall&r)"));
                return true;
            } else if (args[0].equalsIgnoreCase("showall")){
                // Get the target player
                final Player target = Bukkit.getPlayer(args[1]);
                // If the target doesn't exist, stop
                if (target == null){
                    sender.sendMessage(ColorUtils.colorText("&c> &fCould not find the player (&a" + args[1] + "&f)"));
                    return false;
                }

                // Show the nametag of the target, to everyone
                this.example.displayHandler.globalHiddenNames.remove(target.getName());
                for (final Player players : Bukkit.getOnlinePlayers()) showNametag(players, target.getName());
                sender.sendMessage(ColorUtils.colorText("&a> &fRevealed the nametag of (&a" + target.getName() + "&f) to (&aall&r)"));
                return true;
            }
        }

        sender.sendMessage(ColorUtils.colorText("&c> &fInvalid command usage, try /help example"));
        return false;
    }

    /**
     * Hides the name tag of the target player, for the player
     */
    public static void hideNametag(final Player player, final String target){
        // If either the player or target is null, stop
        if (player == null || target == null) return;
        final Scoreboard scoreboard = player.getScoreboard();
        Team hide = scoreboard.getTeam("hide");
        if (hide == null) hide = scoreboard.registerNewTeam("hide");
        hide.addEntry(target);
        hide.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
    }

    /**
     * Shows the name tag of the target player, for the player
     */
    public static void showNametag(final Player player, final String target){
        // If either the player or target is null, stop
        if (player == null || target == null) return;
        final Scoreboard scoreboard = player.getScoreboard();
        final Team hide = scoreboard.getTeam("hide");
        if (hide == null) return;
        hide.removeEntry(target);
    }

    public void addLocalNametag(final Player player, final String target){
        // If either the player or target is null, stop
        if (player == null || target == null) return;
        // If they have no local name list add it
        if (!this.example.displayHandler.hiddenNames.containsKey(player.getUniqueId()))
            this.example.displayHandler.hiddenNames.put(player.getUniqueId(), new ArrayList<>());
        // Add the name to the list
        this.example.displayHandler.hiddenNames.get(player.getUniqueId()).add(target);
    }

    public void removeLocalNametag(final Player player, final String target){
        // If either the player or target is null, stop
        if (player == null || target == null) return;
        // If they have no local name list, stop
        if (!this.example.displayHandler.hiddenNames.containsKey(player.getUniqueId())) return;
        // Remove the name from the list
        this.example.displayHandler.hiddenNames.get(player.getUniqueId()).remove(target);
    }
}
