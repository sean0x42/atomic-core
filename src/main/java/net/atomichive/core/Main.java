package net.atomichive.core;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.google.gson.stream.MalformedJsonException;
import io.seanbailey.database.DatabaseManager;
import net.atomichive.core.command.*;
import net.atomichive.core.entity.EntityClock;
import net.atomichive.core.entity.EntityManager;
import net.atomichive.core.item.ItemManager;
import net.atomichive.core.listeners.*;
import net.atomichive.core.player.AtomicPlayer;
import net.atomichive.core.player.AtomicPlayerDAO;
import net.atomichive.core.player.PlayerManager;
import net.atomichive.core.warp.WarpDAO;
import net.atomichive.core.warp.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Main
 * Main class for the atomic-core plugin.
 */
public class Main extends JavaPlugin {

    private static Main instance;

    private Configuration config;
    private Logger logger;

    // Management classes
    private PlayerManager playerManager;
    private EntityManager entityManager;
    private ItemManager itemManager;
    private DatabaseManager databaseManager;
    private WarpManager warpManager;
    private ProtocolManager protocolManager;

    private Scoreboard scoreboard;


    /**
     * On enable
     * Run whenever the server enables this plugin.
     */
    @Override
    public void onEnable () {

        // Init
        instance = this;
        logger = getLogger();

        // Load configuration file
        saveDefaultConfig();
        config = this.getConfig();

        playerManager = new PlayerManager();
        entityManager = new EntityManager("entities.json");
        itemManager = new ItemManager("items.json");
        warpManager = new WarpManager();
        protocolManager = ProtocolLibrary.getProtocolManager();

        initDatabase();
        registerCommands();
        registerEvents();

        try {

            logBreak();
            log(Level.INFO, "Loading custom items...");
            itemManager.load();

            logBreak();
            log(Level.INFO, "Loading custom entities...");
            entityManager.load();
            logBreak();

        } catch (MalformedJsonException e) {
            log(Level.SEVERE, "Malformed JSON");
        }

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("level", "level");
        objective.setDisplayName("Level");
        objective.setDisplaySlot(DisplaySlot.BELOW_NAME);

        // Add currently logged in players to player manager
        for (Player player : Bukkit.getOnlinePlayers()) {
            AtomicPlayer atomicPlayer = playerManager.addPlayer(player);
            player.setScoreboard(scoreboard);
            atomicPlayer.updateExperience();
        }

        log(Level.INFO, "Loading warps from database...");
        warpManager.load();

        new EntityClock().runTaskTimer(this, 0L, 5L);

    }


    /**
     * On disable
     * Run whenever the server disables this plugin.
     */
    @Override
    public void onDisable () {

        playerManager.removeAll();

        // Empty player manager
        playerManager = null;
        entityManager = null;
        warpManager = null;

        databaseManager.closeConnection();

    }


    /**
     * Create database
     * Creates a new database with values from the config
     * file.
     */
    private void initDatabase () {

        logBreak();
        log(Level.INFO, "Initialising database...");


        // Init database manager
        databaseManager = new DatabaseManager(
                config.getString("database", "atomic_core"),
                config.getString("host", DatabaseManager.DEFAULT_HOST),
                config.getInt("port", DatabaseManager.DEFAULT_PORT),
                config.getString("username", DatabaseManager.DEFAULT_USERNAME),
                config.getString("password", DatabaseManager.DEFAULT_PASSWORD)
        );


        try {
            AtomicPlayerDAO.init(databaseManager);
            WarpDAO.init(databaseManager);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        databaseManager.setLogger(logger);
        databaseManager.setMigrationsPath("migrations");

        if (config.getBoolean("auto_migrate", true))
            databaseManager.migrate();

        logBreak();

    }


    /**
     * Register commands
     * This function registers all commands with Bukkit.
     */
    private void registerCommands () {

        log(Level.INFO, "Registering commands...");

        new CommandClear();
        new CommandEntity();
        new CommandExperience();
        new CommandFly();
        new CommandGameMode();
        new CommandGod();
        new CommandHeal();
        new CommandItem();
        new CommandJump();
        new CommandKill();
        new CommandKillAll();
        new CommandLevel();
        new CommandListen();
        new CommandMessage();
        new CommandNickname();
        new CommandPing();
        new CommandPosition();
        new CommandReply();
        new CommandSpeed();
        new CommandSudo();
        new CommandSuicide();
        new CommandTeleport();
        new CommandTeleportAccept();
        new CommandTeleportAll();
        new CommandTeleportAsk();
        new CommandTeleportDeny();
        new CommandTeleportHere();
        new CommandTeleportPosition();
        new CommandWarp();

    }


    /**
     * Register events
     * This functions registers all events with Bukkit.
     */
    private void registerEvents () {

        log(Level.INFO, "Registering events...");

        // Put all event handlers here
        new CommandListener();
        new EntityChangeBlockListener();
        new EntityDamageListener();
        new EntityDeathListener();
        new EntityTeleportListener();
        new InteractEntityListener();
        new LoginListener();
        new PacketListener();
        new QuitListener();
        new SlimeSplitListener();

    }


    public void log (Level level, String out) {
        logger.log(level, out);
    }

    public void logBreak () {
        log(Level.INFO, "-*-");
    }


    /*
        Getters and setters from here down.
     */

    public static Main getInstance () {
        return instance;
    }

    public Configuration getBukkitConfig () {
        return config;
    }

    public PlayerManager getPlayerManager () {
        return playerManager;
    }

    public EntityManager getEntityManager () {
        return entityManager;
    }

    public ItemManager getItemManager () {
        return itemManager;
    }

    public WarpManager getWarpManager () {
        return warpManager;
    }

    public ProtocolManager getProtocolManager () {
        return protocolManager;
    }

    public Scoreboard getScoreboard () {
        return scoreboard;
    }

}
