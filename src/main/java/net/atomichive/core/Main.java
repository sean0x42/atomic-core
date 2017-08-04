package net.atomichive.core;

import com.google.gson.stream.MalformedJsonException;
import io.seanbailey.database.DatabaseManager;
import net.atomichive.core.command.*;
import net.atomichive.core.entity.EntityClock;
import net.atomichive.core.entity.EntityManager;
import net.atomichive.core.listeners.*;
import net.atomichive.core.player.AtomicPlayerDAO;
import net.atomichive.core.player.PlayerManager;
import net.atomichive.core.warp.WarpDAO;
import net.atomichive.core.warp.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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

    private PlayerManager playerManager;
    private DatabaseManager databaseManager;
    private WarpManager warpManager;


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

        initDatabase();
        registerCommands();
        registerEvents();

        if (config.getBoolean("development_mode", false)) {
            logger.log(Level.INFO, "Loading custom entities...");
            try {
                EntityManager.load();
            } catch (MalformedJsonException e) {
                e.printStackTrace();
            }
        }

        playerManager = new PlayerManager();
        warpManager   = new WarpManager();

        // Add currently logged in players to player manager
        for (Player player : Bukkit.getOnlinePlayers())
            playerManager.addPlayer(player);

        log(Level.INFO, "Loading warps.");
        warpManager.load();

        BukkitTask task = new EntityClock().runTaskTimer(this, 0L, 5L);

    }


    /**
     * On disable
     * Run whenever the server disables this plugin.
     */
    @Override
    public void onDisable () {

        // Empty player manager
        playerManager.removeAll();
        warpManager.removeAll();

        databaseManager.closeConnection();

    }


    /**
     * Create database
     * Creates a new database with values from the config
     * file.
     */
    private void initDatabase () {

        logger.log(Level.INFO, "Initialising database...");


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

        logger.log(Level.INFO, "Initialised database.");

    }


    /**
     * Register commands
     * This function registers all commands with Bukkit.
     */
    private void registerCommands () {

        logger.log(Level.INFO, "Registering commands...");

        new CommandEntity();
        new CommandFly();
        new CommandGameMode();
        new CommandGod();
        new CommandHeal();
        // new CommandHelp();
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
        new CommandTeleportAll();
        new CommandTeleportHere();
        new CommandTeleportPosition();
        new CommandWarp();

        logger.log(Level.INFO, "Commands registered.");

    }


    /**
     * Register events
     * This functions registers all events with Bukkit.
     */
    private void registerEvents () {

        logger.log(Level.INFO, "Registering events...");

        // Put all event handlers here
        new CommandListener();
        new EntityChangeBlockListener();
        new EntityDamageListener();
        new EntityDeathListener();
        new EntityTeleportListener();
        new LoginListener();
        new QuitListener();
        new SlimeSplitListener();

        logger.log(Level.INFO, "Events registered.");

    }


    public void log (Level level, String out) {
        logger.log(level, out);
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

    public WarpManager getWarpManager () {
        return warpManager;
    }

}
