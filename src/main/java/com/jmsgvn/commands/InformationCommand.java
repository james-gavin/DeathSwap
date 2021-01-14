package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.jmsgvn.DeathSwap;
import com.jmsgvn.game.GamePhase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

public class InformationCommand extends BaseCommand {

    /**
     * This command will give information about live and non live games. Certain options are only displayed
     * if they are enabled.
     */

    @CommandAlias("info")
    public void status(CommandSender player) {
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------" + ChatColor.GOLD + ChatColor.BOLD + " Game Information " + ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "-----------" );
        player.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GOLD + DeathSwap.getInstance().getGame().getPhase());
        player.sendMessage(ChatColor.YELLOW + "Portals Enabled: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getBoolean("portalsEnabled"));
        player.sendMessage(ChatColor.YELLOW + "Swap Warning Time: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("swapWarningTime") + " seconds");
        player.sendMessage(ChatColor.YELLOW + "Grace period: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("gracePeriod") + " seconds");

        if (DeathSwap.getInstance().getConfig().getInt("cancelSwapChance") != 0) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Cancel Swap Chance: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("cancelSwapChance") + "%");
        }


        if (DeathSwap.getInstance().getConfig().getBoolean("enableMinMaxSwap")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Min/Max Swap Enabled: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getBoolean("enableMinMaxSwap"));
            player.sendMessage(ChatColor.YELLOW + "Min Swap Time: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("minSwapTime") + " seconds");
            player.sendMessage(ChatColor.YELLOW + "Max Swap Time: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("maxSwapTime") + " seconds");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Swap time: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("swapTime") + " seconds");
        }

        if (DeathSwap.getInstance().getConfig().getBoolean("displayTimeLeft")) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Display Time Left Enabled: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getBoolean("displayTimeLeft"));
        }


        if (DeathSwap.getInstance().getGame().getPhase() != GamePhase.LOBBY) {
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "AntiCheat Enabled: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getBoolean("antiCheat"));
            player.sendMessage("");
            StringBuilder sb = new StringBuilder();

            Bukkit.getOnlinePlayers().forEach(player1 -> {
                if (player1.isOp()) {
                    sb.append(player1.getName()).append(", ");
                }
            });

            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }

            player.sendMessage(ChatColor.YELLOW + "Current Operators: " + ChatColor.GOLD + sb.toString());

            sb.setLength(0);

            Bukkit.getOnlinePlayers().forEach(player1 -> {
                if (!player1.getGameMode().equals(GameMode.SPECTATOR)) {
                    sb.append(player1.getName()).append(", ");
                }
            });

            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }

            player.sendMessage(ChatColor.YELLOW + "Players Alive: " + ChatColor.GOLD + sb.toString());
        }

        player.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "----------------------------------------");
    }
}
