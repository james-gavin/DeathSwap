package com.jmsgvn.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import com.jmsgvn.DeathSwap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("ds")
@CommandPermission("op")
public class ReloadConfig extends BaseCommand {

    @Subcommand("rl")
    public void reloadConfig(CommandSender player) {
        DeathSwap.getInstance().reloadConfig();
        player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.GREEN + "Config reloaded.");
        DeathSwap.getInstance().getGame().sendMessage("&6DeathSwap> &eThe settings have been changed. /info to see changes.");
    }

}
