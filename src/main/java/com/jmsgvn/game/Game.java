package com.jmsgvn.game;

import com.jmsgvn.DeathSwap;
import fr.mrmicky.fastboard.FastBoard;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;

public class Game implements Listener {

    private int countdown;

    @Getter
    private GamePhase phase = GamePhase.LOBBY;

    private Player winner;

    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public Game() {
        DeathSwap.getInstance().getServer().getConsoleSender().sendMessage("Game starting");
        Bukkit.getScheduler().runTaskTimer(DeathSwap.getInstance(), this::run, 0L, 20L);
        DeathSwap.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.GOLD + "DeathSwap> " + ChatColor.YELLOW + "Game loop started.");
    }

    public void setPhase(GamePhase phase) {
        switch (phase) {
            case LOBBY:
                boards.forEach((uuid, fastBoard) -> {
                    fastBoard.delete();
                });

                boards.clear();

                Bukkit.getOnlinePlayers().forEach(player -> {
                    resetPlayer(player);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.teleport(player.getWorld().getSpawnLocation());
                });
                sendMessage("");
                sendMessage("&eThe game ended. You have been teleported to the lobby.");
                sendMessage("");

                playSound(Sound.BLOCK_NOTE_BLOCK_CHIME);
                break;
            case GRACE:
                this.countdown = (DeathSwap.getInstance().getConfig().getInt("gracePeriod"));
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setGameMode(GameMode.SURVIVAL);
                    Random r = new Random();

                    int low = -2000;
                    int high = 2000;

                    int randomX = r.nextInt(high-low) + low;
                    int randomZ = r.nextInt(high-low) + low;

                    player.teleport(player.getWorld().getHighestBlockAt(randomX, randomZ).getLocation().add(0,20,0));

                    resetPlayer(player);

                    player.setGameMode(GameMode.SURVIVAL);

                    if (DeathSwap.getInstance().getConfig().getBoolean("displayTimeLeft")) {
                        boards.put(player.getUniqueId(), new FastBoard(player));
                        FastBoard fastBoard = boards.get(player.getUniqueId());
                        fastBoard.updateTitle(ChatColor.GOLD + "DeathSwap");
                        fastBoard.updateLines(
                                "",
                                ChatColor.YELLOW + "Time Left: " + ChatColor.GOLD + countdown + "s",
                                "");
                    } else {
                        FastBoard board = boards.remove(player.getUniqueId());

                        if (board != null) {
                            board.delete();
                        }
                    }
                });

                sendMessage("");
                sendMessage("&eGrace period has begun!");
                sendMessage("&eType /info for game settings");
                sendMessage("");

                break;
            case RUNNING:
                setCountDown();
                sendMessage("");
                sendMessage("&eThe game has begun!");

                if (!DeathSwap.getInstance().getConfig().getBoolean("enableMinMaxSwap")) {
                    sendMessage("&eThe first swap will happen in &6" + countdown + " seconds&e.");
                } else {
                    sendMessage("&eThe swap time has randomly been chosen.");
                }

                sendMessage("");
                playSound(Sound.ENTITY_PLAYER_LEVELUP);
                break;
            case END:
                Player player = Bukkit.getOnlinePlayers().stream().filter(player1 ->
                        !player1.getGameMode().equals(GameMode.SPECTATOR)).findFirst().orElse(null);
                if (player != null) {
                    sendMessage("&6DeathSwap> " + player.getName() + "&e has won!");
                    winner = player;
                    playSound(Sound.ENTITY_PLAYER_LEVELUP);
                    countdown = 15;
                } else {
                    sendMessage("&6DeathSwap> &cNo one won.");
                    setPhase(GamePhase.LOBBY);
                }
                break;
        }
        this.phase = phase;
    }

    private void setCountDown() {
        if (DeathSwap.getInstance().getConfig().getBoolean("enableMinMaxSwap")) {
            Random r = new Random();

            int low = DeathSwap.getInstance().getConfig().getInt("minSwapTime");
            int high = DeathSwap.getInstance().getConfig().getInt("maxSwapTime");;

            countdown = r.nextInt(high-low) + low;
        } else {
            countdown = DeathSwap.getInstance().getConfig().getInt("swapTime");;
        }
    }

    public void run() {

        if (DeathSwap.getInstance().getConfig().getBoolean("displayTimeLeft")) {
            boards.forEach((uuid, fastBoard) -> {
                fastBoard.updateTitle(ChatColor.GOLD + "DeathSwap");
                fastBoard.updateLines(
                        "",
                        ChatColor.YELLOW + "Time Left: " + ChatColor.GOLD + countdown + "s",
                        "");
            });
        }

        switch (phase) {
            case LOBBY:
                break;
            case GRACE:
                if (countdown != 0) {
                    if (countdown == 30) {
                        sendMessage("&6DeathSwap> &eGrace period ending in &6" + countdown + " seconds&e.");
                    } else if (countdown <= 15) {
                        sendMessage("&6DeathSwap> &eGrace period ending in &6" + countdown + " seconds&e.");
                        playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
                    }
                } else {
                    setPhase(GamePhase.RUNNING);
                }
                countdown--;
                break;
            case RUNNING:
                if (countdown != 0) {
                    if (countdown <= DeathSwap.getInstance().getConfig().getInt("swapWarningTime")) {
                        sendMessage("&6DeathSwap> &eSwap in &6" + countdown + " seconds&e.");
                        playSound(Sound.BLOCK_STONE_BUTTON_CLICK_ON);
                    }
                    countdown--;
                } else {

                    setCountDown();

                    Random r = new Random();

                    sendMessage("");
                    sendMessage("&6Swapping!");
                    sendMessage("");

                    playSound(Sound.BLOCK_NOTE_BLOCK_CHIME);

                    if (r.nextInt(100) < DeathSwap.getInstance().getConfig().getInt("cancelSwapChance")) {
                        sendMessage("&6DeathSwap> &eThe swap was randomly canceled.");
                        playSound(Sound.BLOCK_ANVIL_DESTROY);
                    } else {
                        Map<Integer, Location> locations = new HashMap<>();
                        Map<Integer, Player> players = new HashMap<>();

                        int count = 0;

                        for (int i = 0; i < Bukkit.getOnlinePlayers().size(); i ++) {
                            Player player = (Player) Bukkit.getOnlinePlayers().toArray()[i];
                            if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                                players.put(count, player);
                                locations.put(count, player.getLocation());
                                count++;
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

                        locations.clear();
                        players.clear();
                    }
                }
                break;
            case END:

                if (winner != null) {
                    spawnFirework(winner, 80L, FireworkEffect.Type.STAR, Color.ORANGE, Color.YELLOW);
                }

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

    public void spawnFirework(Player player,long detonateTime, FireworkEffect.Type fireworkEffect, Color... colors) {
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation().add(0,2,0), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().with(fireworkEffect).withColor(colors).build();
        fireworkMeta.addEffect(effect);
        firework.setFireworkMeta(fireworkMeta);
        Bukkit.getScheduler().runTaskLater(DeathSwap.getInstance(), firework::detonate, detonateTime);
    }

    public void resetPlayer(Player player) {
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.getActivePotionEffects().forEach(potionEffect -> {
            player.removePotionEffect(potionEffect.getType());
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(ChatColor.GOLD + "DeathSwap> " + player.getName() + ChatColor.YELLOW + " has joined.");

        switch (phase) {
            case LOBBY:
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.YELLOW + "Please wait for the next game to start...");
                player.setGameMode(GameMode.ADVENTURE);
                break;
            case GRACE:
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.YELLOW + "You joined during the grace period but will still be allowed to play.");
                player.setGameMode(GameMode.SURVIVAL);
                break;
            case RUNNING:
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " + ChatColor.RED + "You joined during a game. You are now a spectator.");
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " + ChatColor.YELLOW + "Spectate others by typing /tp <player>");
                player.setGameMode(GameMode.SPECTATOR);
                break;
            case END:
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " + ChatColor.YELLOW + "You joined during the end of a game. Please wait for a new game to start.");
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " + ChatColor.YELLOW + "Spectate others by typing /tp <player>");
                player.setGameMode(GameMode.SPECTATOR);
                break;
        }

        player.teleport(player.getWorld().getSpawnLocation());
        resetPlayer(player);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
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

                sendMessage("&6DeathSwap> " + player.getName() + "&e died. &6(" + event.getDeathMessage() + ")");
                playSound(Sound.ENTITY_LIGHTNING_BOLT_IMPACT);
                player.getWorld().strikeLightningEffect(player.getLocation());
                player.sendMessage(ChatColor.GOLD + "DeathSwap> " + ChatColor.YELLOW + "Spectate others by typing /tp <player>");

                if (getPlayersLeft() == 1) {
                    setPhase(GamePhase.END);
                }
                break;
        }
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(ChatColor.GOLD + "DeathSwap> " + player.getName() + ChatColor.YELLOW + " has left.");

        player.setGameMode(GameMode.SPECTATOR);

        FastBoard board = boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }

        switch (phase) {
            case RUNNING:
            case GRACE:
                if (getPlayersLeft() == 1) {
                    sendMessage("&6DeathSwap> " + player.getName() + "&e was the second to last alive and left the game.");
                    setPhase(GamePhase.END);
                }
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
                sendMessage("&6DeathSwap> " + player.getName() + "&e died before the game even started! L.");
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
    public void chatEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "&c[Dead] &6" + player.getName() + "&7:&e " + event.getMessage()));
        } else if (player.isOp()) {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "&c[OP] &6" + player.getName() + "&7:&e " + event.getMessage()));
        } else if (winner != null && winner == player){
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "&6[Winner] " + player.getName() + "&7:&e " + event.getMessage()));
        } else {
            event.setFormat(ChatColor.translateAlternateColorCodes('&', "&6" + player.getName() + "&7:&e " + event.getMessage()));
        }
    }

    @EventHandler
    public void commandEvent(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        switch (phase) {
            case GRACE:
            case RUNNING:
                if (player.isOp()) {
                    if (DeathSwap.getInstance().getConfig().getBoolean("antiCheat")) {
                        sendMessage("");
                        sendMessage("&c&lWARNING &c" + player.getName() + " executed a command while opped!");
                        sendMessage("&cCommand: " + event.getMessage());
                        sendMessage("");
                    }
                }
        }
    }

    @EventHandler
    public void consoleCommandEvent(ServerCommandEvent event) {
        switch (phase) {
            case GRACE:
            case RUNNING:
                if (DeathSwap.getInstance().getConfig().getBoolean("antiCheat")) {
                    sendMessage("");
                    sendMessage("&c&lWARNING &cThe console has executed a command during the game!");
                    sendMessage("&cCommand: /" + event.getCommand());
                    sendMessage("");
                }
        }
    }



    @EventHandler
    public void worldChange(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        if (!DeathSwap.getInstance().getConfig().getBoolean("netherEnabled")) {
            event.setCancelled(true);
            event.setCanCreatePortal(false);
            player.sendMessage(ChatColor.GOLD + "DeathSwap> " +ChatColor.RED + "Portals have been disabled.");
        }
    }

}
