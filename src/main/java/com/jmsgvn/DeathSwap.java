package com.jmsgvn;

import org.bukkit.plugin.java.JavaPlugin;

public class DeathSwap extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getServer().getConsoleSender().sendMessage("Death Swap loaded...");
    }
}
