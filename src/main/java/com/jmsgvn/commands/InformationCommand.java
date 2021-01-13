package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import com.jmsgvn.DeathSwap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class InformationCommand extends BaseCommand {

    @CommandAlias("info")
    public void status(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Status: " + ChatColor.GOLD + DeathSwap.getInstance().getGame().getPhase());
        player.sendMessage(ChatColor.YELLOW + "Portals Enabled: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getBoolean("portalsEnabled"));
        player.sendMessage(ChatColor.YELLOW + "Swap time: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("swapTime") + " seconds");
        player.sendMessage(ChatColor.YELLOW + "Swap Warning Time: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("swapWarningTime") + " seconds");
        player.sendMessage(ChatColor.YELLOW + "Grace period: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("gracePeriod") + " seconds");
        player.sendMessage(ChatColor.YELLOW + "Cancel Swap Chance: " + ChatColor.GOLD + DeathSwap.getInstance().getConfig().getInt("cancelSwapChance") + "%");

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

        player.sendMessage("");
    }
}
