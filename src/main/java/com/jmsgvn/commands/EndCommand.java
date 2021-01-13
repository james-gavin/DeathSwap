package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import com.jmsgvn.DeathSwap;
import com.jmsgvn.game.GamePhase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EndCommand extends BaseCommand {

    @CommandAlias("end")
    @CommandPermission("op")
    public void endGame(Player player) {
        if(DeathSwap.getInstance().getGame().getPhase().equals(GamePhase.LOBBY)) {
            player.sendMessage(ChatColor.RED + "There are no active games.");
            return;
        }

        DeathSwap.getInstance().getGame().setPhase(GamePhase.END);
        player.sendMessage(ChatColor.GREEN + "Ending the game!");
    }
}
