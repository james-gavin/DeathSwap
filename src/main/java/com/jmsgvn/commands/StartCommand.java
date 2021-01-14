package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.jmsgvn.DeathSwap;
import com.jmsgvn.game.GamePhase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StartCommand extends BaseCommand {

    @CommandAlias("start")
    @CommandPermission("op")
    public void start(CommandSender player) {

        // check to make sure there is more than one person online
        if (player.getServer().getOnlinePlayers().size() <= 1) {
            player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.RED + "There are not enough players online to start the game.");
            return;
        }

        if(!DeathSwap.getInstance().getGame().getPhase().equals(GamePhase.LOBBY)) {
            player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.RED + "There is already a game in session.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.YELLOW + "Starting game now!");
        DeathSwap.getInstance().getGame().setPhase(GamePhase.GRACE);
    }
}
