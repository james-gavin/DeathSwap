package com.jmsgvn;

import co.aikar.commands.BukkitCommandManager;
import com.jmsgvn.commands.*;
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

        this.saveDefaultConfig();
        this.game = new Game();
        this.getServer().getPluginManager().registerEvents(game, this);

        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new StartCommand());
        manager.registerCommand(new TeleportCommand());
        manager.registerCommand(new EndCommand());
        manager.registerCommand(new ReloadConfig());
        manager.registerCommand(new InformationCommand());

        MetricsLite metrics = new MetricsLite(this, 9998);
    }
}
