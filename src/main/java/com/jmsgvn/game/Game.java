package com.jmsgvn.game;

import com.jmsgvn.DeathSwap;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class Game implements Listener {

    private int countdown;
    private final int swapTime;
    @Getter
    private GamePhase phase = GamePhase.LOBBY;

    public Game() {
        this.swapTime = DeathSwap.getInstance().getConfig().getInt("swapTime");
        DeathSwap.getInstance().getServer().getConsoleSender().sendMessage("Game starting");
        Bukkit.getScheduler().runTaskTimer(DeathSwap.getInstance(), this::run, 0L, 20L);
    }

    public void setPhase(GamePhase phase) {
        switch (phase) {
            case LOBBY:
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.getInventory().clear();
                    player.setHealth(20);
                    player.setFoodLevel(20);
                    player.setLevel(0);
                    player.setExp(0);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(player.getWorld().getSpawnLocation());
                });
                sendMessage("");
                sendMessage("&aThe game is over. You have been teleported to the lobby");
                sendMessage("");
                break;
            case GRACE:
                this.countdown = (10);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    Random r = new Random();

                    int low = -1000;
                    int high = 1000;

                    int randomX = r.nextInt(high-low) + low;
                    int randomZ = r.nextInt(high-low) + low;

                    player.teleport(player.getWorld().getHighestBlockAt(randomX, randomZ).getLocation().add(0,20,0));
                });

                sendMessage("");
                sendMessage("&aGrace period has begun!");
                sendMessage("&aYou have " + countdown + " seconds before the game begins.");
                sendMessage("&aPortals enabled: " + DeathSwap.getInstance().getConfig().getBoolean("portalsEnabled"));
                sendMessage("&aSwap time: " + DeathSwap.getInstance().getConfig().getInt("swapTime"));
                sendMessage("");
                break;
            case RUNNING:
                this.countdown = swapTime;
                sendMessage("");
                sendMessage("&aThe game has begun!");
                sendMessage("&aThe first swap will happen in " + countdown + " seconds.");
                sendMessage("");
                break;
            case END:
                Player player = Bukkit.getOnlinePlayers().stream().filter(player1 ->
                        !player1.getGameMode().equals(GameMode.SPECTATOR)).findFirst().orElse(null);
                if (player != null) {
                    sendMessage("&6" + player.getName() + "&e has won!");
                    countdown = 30;
                } else {
                    sendMessage("&cNo one won.");
                    setPhase(GamePhase.LOBBY);
                }
                break;
        }
        this.phase = phase;
    }

    public void run() {
        switch (phase) {
            case LOBBY:
                break;
            case GRACE:
                if (countdown != 0) {
                    if (countdown == 30) {
                        sendMessage("&eGame starting in: &6" + countdown + " seconds.");
                    } else if (countdown <= 15) {
                        sendMessage("&eGame starting in: &6" + countdown + " seconds.");
                        playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
                    }
                } else {
                    setPhase(GamePhase.RUNNING);
                }
                countdown--;
                break;
            case RUNNING:
                if (countdown != 0) {
                    if (countdown <= 10) {
                        sendMessage("&eSwap in: &6" + countdown + " seconds.");
                        playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
                    }
                    countdown--;
                } else {
                    countdown = swapTime;

                    Map<Integer, Location> locations = new HashMap<>();
                    Map<Integer, Player> players = new HashMap<>();

                    for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i ++) {
                        Player player = (Player) Bukkit.getOnlinePlayers().toArray()[i];
                        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                            players.put(i, player);
                            locations.put(i, player.getLocation());
                        }
                    }

                    for (int i = 0; i < players.size(); i++) {
                        Player player = players.get(i);
                        if (i == players.size()-1) {
                            player.teleport(locations.get(0));
                        } else {
                            player.teleport(locations.get(i+1));
                        }

                        player.setFallDistance(0);
                    }


                    sendMessage("");
                    sendMessage("&aSwitching!");
                    sendMessage("&aThe next switch will take place in " + countdown + " seconds.");
                    sendMessage("");
                }
                break;
            case END:
                if (countdown <= 0) {
                    setPhase(GamePhase.LOBBY);
                }
                countdown--;
        }

    }

    public void sendMessage(String string) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', string));
        });
    }

    public void playSound(Sound sound) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.playSound(player.getLocation(), sound, 1F, 1.5F);
        });
    }

    public int getPlayersLeft() {
        return (int) Bukkit.getOnlinePlayers().stream().filter(player -> !player.getGameMode().equals(GameMode.SPECTATOR)).count();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        switch (phase) {
            case LOBBY:
                player.sendMessage(ChatColor.YELLOW + "Please wait for the next game to start...");
                player.getInventory().clear();
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setLevel(0);
                player.setExp(0);
                player.setGameMode(GameMode.ADVENTURE);
                break;
            case GRACE:
                player.sendMessage(ChatColor.YELLOW + "You joined during the grace period but will still be allowed to play.");
                player.setGameMode(GameMode.SURVIVAL);
                player.getInventory().clear();
                player.setHealth(20);
                player.setFoodLevel(20);
                player.setLevel(0);
                player.setExp(0);
                break;
            case RUNNING:
                player.sendMessage(ChatColor.RED + "You joined during a game. You are now a spectator.");
                player.setGameMode(GameMode.SPECTATOR);
                break;
            case END:
                player.sendMessage(ChatColor.YELLOW + "You joined during the end of a game. Please wait for a new game to start.");
                player.setGameMode(GameMode.SPECTATOR);
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(null);
        switch (phase) {
            case LOBBY:
            case END:
                event.setCancelled(true);
                player.setHealth(20);
                break;
            case RUNNING:
                event.setCancelled(true);
                player.setHealth(20);
                player.setGameMode(GameMode.SPECTATOR);

                sendMessage("&6" + player.getName() + "&e died.");
                player.sendMessage(ChatColor.YELLOW + "You can now teleport to other players by typing /tp <player>");

                if (getPlayersLeft() == 1) {
                    setPhase(GamePhase.END);
                }
                break;
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        switch (phase) {
            case LOBBY:
                event.getPlayer().teleport(player.getWorld().getSpawnLocation());
                break;
            case GRACE:
                sendMessage("&6" + player.getName() + "&e died before the game even started! L.");
                playSound(Sound.BLOCK_ANVIL_DESTROY);
                break;
        }
    }

    @EventHandler
    public void playerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            switch (phase) {
                case LOBBY:
                case GRACE:
                case END:
                    event.setCancelled(true);
                    break;
                case RUNNING:
                    break;
            }

        }
    }

    @EventHandler
    public void hungerLoss(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            switch (phase) {
                case LOBBY:
                case GRACE:
                case END:
                    event.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void worldChange(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!DeathSwap.getInstance().getConfig().getBoolean("netherEnabled")) {
            event.setCancelled(true);
            event.setCanCreatePortal(false);
            player.sendMessage(ChatColor.RED + "Portals have been disabled.");
        }
    }

}
