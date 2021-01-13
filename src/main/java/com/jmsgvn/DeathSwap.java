package com.jmsgvn;

import co.aikar.commands.BukkitCommandManager;
import com.jmsgvn.commands.EndCommand;
import com.jmsgvn.commands.StartCommand;
import com.jmsgvn.commands.TeleportCommand;
import com.jmsgvn.game.Game;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class DeathSwap extends JavaPlugin {

    @Getter
    private static DeathSwap instance;
    @Getter
    private Game game;

    @Override
    public void onEnable() {
        instance = this;

        this.getServer().getConsoleSender().sendMessage("Death Swap loaded...");

        this.saveDefaultConfig();
        this.game = new Game();
        this.getServer().getPluginManager().registerEvents(game, this);

        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new StartCommand());
        manager.registerCommand(new TeleportCommand());
        manager.registerCommand(new EndCommand());
    }
}
