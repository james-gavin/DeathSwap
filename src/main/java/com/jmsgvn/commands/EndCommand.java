package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.jmsgvn.DeathSwap;
import com.jmsgvn.game.GamePhase;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EndCommand extends BaseCommand {

    @CommandAlias("end")
    @CommandPermission("op")
    public void endGame(CommandSender player) {
        if(DeathSwap.getInstance().getGame().getPhase().equals(GamePhase.LOBBY) || DeathSwap.getInstance().getGame().getPhase().equals(GamePhase.END)) {
            player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.RED + "There are no active games.");
            return;
        }

        DeathSwap.getInstance().getGame().setPhase(GamePhase.END);
        DeathSwap.getInstance().getGame().sendMessage("&6DeathSwap> " + player.getName() + "&e has forcefully ended the game.");
    }
}
