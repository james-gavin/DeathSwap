package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class TeleportCommand extends BaseCommand {

    @CommandAlias("tp")
    @CommandCompletion("@players")
    public void teleport(Player player, OnlinePlayer target) {
        if (player.isOp() || player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.teleport(target.getPlayer().getLocation());
            player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.YELLOW + "Teleported to " + target.getPlayer().getName() + ".");
        } else {
            player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.RED + "This command can only be used whilst a spectator.");
        }
    }
}
